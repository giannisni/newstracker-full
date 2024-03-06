package com.getout.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.getout.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class KeywordFrequencyService {


    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public KeywordFrequencyService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    /**
     * Retrieves keyword counts from Elasticsearch for a given keyword and date range.
     *
//     * @param keyword    The keyword to search for.
//     * @param startDate  The start date of the range.
//     * @param endDate    The end date of the range.
     * @return A map of dates to keyword counts.
     * @throws IOException If there's an issue communicating with Elasticsearch.
     */



    public List<Map<String, Object>> getWordCloudData(String indexName) throws IOException {
        // Create search request
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(indexName)
                .query(q -> q
                        .matchAll(new MatchAllQuery.Builder().build())
                )
                .size(10000) // Adjust the size as needed
        );

        // Execute the search
        SearchResponse<Object> searchResponse = elasticsearchClient.search(searchRequest, Object.class);

        // Process the search hits
        List<Map<String, Object>> wordCloudData = new ArrayList<>();
        for (Hit<Object> hit : searchResponse.hits().hits()) {
            Map<String, Object> sourceAsMap = (Map<String, Object>) hit.source();
            String keyword = (String) sourceAsMap.get("keyword");
            Double frequency = (Double) sourceAsMap.get("frequency");
            wordCloudData.add(Map.of("text", keyword, "value", frequency));
        }

        return wordCloudData;
    }

    public List<OpenAIData> fetchOpenAIData(String indexName) throws IOException {
        // Define the search query
        SearchResponse<Map> searchResponse = elasticsearchClient.search(s -> s
                        .index(indexName)
                        .size(1000) // Adjust size as needed
                        .query(q -> q
                                .matchAll(m -> m)
                        ),
                Map.class
        );

        // Process the search hits
        List<OpenAIData> openAIDataList = new ArrayList<>();
        for (Hit<Map> hit : searchResponse.hits().hits()) {
            Map<String, Object> sourceAsMap = hit.source();
            Object openAIObject = sourceAsMap.get("OpenAI");

            String openAIString = "";
            if (openAIObject instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> openAIList = (List<String>) openAIObject;
                openAIString = String.join(", ", openAIList); // Concatenate list into a string
            } else if (openAIObject instanceof String) {
                openAIString = (String) openAIObject; // Use it directly if it's already a string
            }

            Integer count = (Integer) sourceAsMap.get("Count");
            openAIDataList.add(new OpenAIData(openAIString, count));
        }

        return openAIDataList;
    }


    public class OpenAIData {
        private String openAI;   // If OpenAI is always a single string
        private Integer count;

        public OpenAIData(String openAI, Integer count) {
            this.openAI = openAI;
            this.count = count;
        }

        // Getter for openAI
        public String getOpenAI() {
            return openAI;
        }

        // Getter for count
        public Integer getCount() {
            return count;
        }

        // Optionally, you can add setter methods if you need them
    }


    public String elasticHost = "localhost";

    public double calculateAverageSentiment(String sentiment_index,String index, String term,  LocalDate startDate, LocalDate endDate) throws IOException {
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        System.out.println("Index sent: " + sentiment_index);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String start = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"
        String end = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"

        System.out.println("startDate: " + start);
        System.out.println("endDate: " + end);

        SearchResponse<Object> response = elasticsearchClient.search(s -> s
                        .index(sentiment_index)
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("term.keyword")
                                                        .value(term)
                                                )
                                        )
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("index.keyword")
                                                        .value(index)
                                                )
                                        )
                                        .filter(f -> f
                                                .range(r -> r
                                                        .field("date")
                                                        .gte(JsonData.of(start)) // Keep using JsonData.of() with LocalDate.toString()
                                                        .lte(JsonData.of(end)) // Keep using JsonData.of() with LocalDate.toString()
                                                )
                                        )
                                )
                        ),
                Object.class
        );

        AtomicReference<Double> totalSentiment = new AtomicReference<>(0.0);
        long docCount = response.hits().total().value();
        System.out.println("Doc count: "+docCount);

        if (docCount > 0) {
            response.hits().hits().forEach(hit -> {
                Map<String, Object> sourceAsMap = (Map<String, Object>) hit.source();
                if (sourceAsMap.containsKey("average_sentiment")) {
                    Object sentimentObj = sourceAsMap.get("average_sentiment");
                    double sentiment = 0.0;
                    if (sentimentObj instanceof Double) {
                        sentiment = (Double) sentimentObj;
                    } else if (sentimentObj instanceof Integer) {
                        sentiment = ((Integer) sentimentObj).doubleValue();
                    }
                    System.out.println("sentiment score: " + sentiment);
                    double finalSentiment = sentiment;
                    totalSentiment.updateAndGet(v -> v + finalSentiment);
                }

            });

            double average = totalSentiment.get() / docCount;
            double result = Math.round(average * 10.0) / 10.0;

            System.out.println("Average sentiment: "+result);
            return result; // Round to 1 decimal place
        }


        return 0.0; // Return 0 if no documents are found
    }

    public Map<LocalDate, Integer> getKeywordCounts(String index, String keyword, LocalDate startDate, LocalDate endDate) throws IOException {
        Map<LocalDate, Integer> keywordCounts = new HashMap<>();

        System.out.println("index : "+ index);

        // Build the request
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .term(t -> t
                                                .field("keyword.keyword")
                                                .value(keyword)
                                        )
                                )
                                .filter(f -> f
                                        .range(r -> r
                                                .field("date")
                                                .gte(JsonData.of(startDate.toString())) // Keep using JsonData.of() with LocalDate.toString()
                                                .lte(JsonData.of(endDate.toString()))
                                        )
                                )
                        )
                )
                .size(10000) // Adjust size as needed
        );

        // Execute the search
        SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);

//        System.out.println("response " + response);
        // Process the results
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> source = hit.source();
            LocalDate date = LocalDate.parse((String) source.get("date"));
            Integer value = (Integer) source.get("value");
            keywordCounts.put(date, value);
        }

        // Close the client

        return keywordCounts;
    }//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 443, "https"))
//                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                            @Override
//                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                            }
//                        })
//        );
//        List<DocumentData> documents = new ArrayList<>();
//
//        // Constructing Bool Query for specific topic
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.must(QueryBuilders.termQuery("topic", topicId))
//                .must(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate));
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(10000); // Adjust size as needed
//
//        SearchRequest searchRequest = new SearchRequest(index);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String title = (String) sourceAsMap.get("title");
//            String date = (String) sourceAsMap.get("published_date");
//            String url = (String) sourceAsMap.get("url");
//
//            documents.add(new DocumentData(title, date, url)); // Storing each document's data
//        }
//
//        client.close();
//        return documents;
//    }
//

//    public static Map<String, Integer> fetchWordFrequenciesFromTopicNew(LocalDate startDate, LocalDate endDate) throws IOException {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 443, "https"))
//                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                            @Override
//                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                            }
//                        })
//        );
//        Map<String, Integer> wordFrequencies = new HashMap<>();
//
//        // Create a query to fetch documents based on date range
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
//                .must(QueryBuilders.rangeQuery("date_published").gte(startDate).lte(endDate));
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(10000);  // Adjust size as needed
//
//        SearchRequest searchRequest = new SearchRequest("article-index4");
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//
//
//        searchResponse.getHits().forEach(hit -> {
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            Object topicnewField = sourceAsMap.get("topicnew");
//            if (topicnewField instanceof String) {
//                String category = (String) topicnewField;
//                wordFrequencies.put(category, wordFrequencies.getOrDefault(category, 0) + 1);
//            } else {
//                // Log a warning or handle the case where topicnew is not a string
//                System.out.println("Warning: topicnew is not a string in document " + hit.getId());
//            }
//        });
//
//
//
//        return wordFrequencies;
//    }


    public static class DocumentData {
        private String title;
        private String date;
        private String url;

        // Constructor
        public DocumentData(String title, String date, String url) {
            this.title = title;
            this.date = date;
            this.url = url;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


//    public static List<DocumentData> fetchDocumentsWithWords(LocalDate startDate, LocalDate endDate,List<String> keywords, String index) throws IOException {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 443, "https"))
//                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                            @Override
//                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                            }
//                        })
//        );
//        List<DocumentData> documents = new ArrayList<>();
//
//        // Constructing Bool Query for specific topic
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.must(QueryBuilders.matchPhraseQuery("text", "Israel"))
////                .must(QueryBuilders.matchPhraseQuery("text", "Palestine"))
////                .should(QueryBuilders.matchPhraseQuery("text", "Gaza"))
////                .should(QueryBuilders.matchPhraseQuery("text", "West Bank"))
//                .must(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate));
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(10000); // Adjust size as needed
//
//        SearchRequest searchRequest = new SearchRequest(index);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            String title = (String) sourceAsMap.get("title");
//            String date = (String) sourceAsMap.get("published_date");
//            String url = (String) sourceAsMap.get("url");
//
//            documents.add(new DocumentData(title, date, url)); // Storing each document's data
//        }
//
//        client.close();
//        return documents;
//    }
//



    // Modified Method to fetch keyword frequencies
//    public static Map<String, Integer> fetchKeywordFrequencies(LocalDate startDate, LocalDate endDate) throws IOException {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
//
//        System.out.println(ELASTICSEARCH_USERNAME + ELASTICSEARCH_PASSWORD);
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 443, "https"))
//                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                            @Override
//                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                            }
//                        })
//        );
//        Map<String, Integer> wordFrequencies = new HashMap<>();
//
//        // Create a query to fetch documents based on date range
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
//                .must(QueryBuilders.rangeQuery("date_published").gte(startDate).lte(endDate));
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(10000);  // Adjust size as needed
//
//        SearchRequest searchRequest = new SearchRequest("article-index4");
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        searchResponse.getHits().forEach(hit -> {
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            Object keywordsField = sourceAsMap.get("keywords");
//            if (keywordsField instanceof List) {
//                List<String> keywords = (List<String>) keywordsField;
//                for (String keyword : keywords) {
//                    wordFrequencies.put(keyword, wordFrequencies.getOrDefault(keyword, 0) + 1);
//                }
//            } else {
//                // Log a warning or handle the case where keywords is not a list
//                System.out.println("Warning: keywords is not a list in document " + hit.getId());
//            }
//        });
//
//        client.close();
//
//        return wordFrequencies;
//    }


    /**
     * Calculates the correlation coefficient between two sets of keyword counts.
     *
//     * @param keywordCounts1 The first set of keyword counts.
//     * @param keywordCounts2 The second set of keyword counts.
     * @return The correlation coefficient.
     */

//    public static double calculateCorrelation(Map<LocalDate, Integer> keywordCounts1, Map<LocalDate, Integer> keywordCounts2) {
//        // Convert the keyword counts maps to arrays
//        // Get the common keys (dates) in both maps
//        Set<LocalDate> commonDates = new HashSet<>(keywordCounts1.keySet());
//        commonDates.retainAll(keywordCounts2.keySet());
//
//        // Filter the maps to only include entries with the common keys
//        Map<LocalDate, Integer> filteredCounts1 = keywordCounts1.entrySet().stream()
//                .filter(entry -> commonDates.contains(entry.getKey()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        Map<LocalDate, Integer> filteredCounts2 = keywordCounts2.entrySet().stream()
//                .filter(entry -> commonDates.contains(entry.getKey()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        // Convert the filtered maps to arrays
//        double[] counts1 = filteredCounts1.values().stream().mapToDouble(Integer::doubleValue).toArray();
//        double[] counts2 = filteredCounts2.values().stream().mapToDouble(Integer::doubleValue).toArray();
//
//        // Calculate the correlation coefficient
//        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
//        double correlation = pearsonsCorrelation.correlation(counts1, counts2);
//
//        // Format the correlation
//        DecimalFormat df = new DecimalFormat("#.##");
//        correlation = Double.valueOf(df.format(correlation));
//
//        return correlation;
//
//    }

    public Map<String, Float> fetchTermPercentages(
            String term1, String term2, String term3, String term4,
            String indexName, LocalDate startDate, LocalDate endDate) throws IOException {

        long count1 = executeCountQuery(term1, term2, indexName, startDate, endDate);
        long count2 = executeCountQuery(term3, term4, indexName, startDate, endDate);

        long total = count1 + count2;
        float percentage1 = total > 0 ? (float) count1 / total * 100 : 0;
        float percentage2 = total > 0 ? (float) count2 / total * 100 : 0;

        return Map.of(
                term1 + " " + term2, percentage1,
                term3 + " " + term4, percentage2
        );
    }

    private long executeCountQuery(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        SpanTermQuery termQuery1 = SpanTermQuery.of(st -> st.field("text").value(term1));
        SpanTermQuery termQuery2 = SpanTermQuery.of(st -> st.field("text").value(term2));

        CountResponse countResponse = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .spanNear(sn -> sn
                                                .inOrder(false)
                                                .slop(50)
                                                .clauses(cl -> cl.spanTerm(termQuery1))
                                                .clauses(cl -> cl.spanTerm(termQuery2))
                                        )
                                )
                                .filter(f -> f
                                        .range(r -> r
                                                .field("published_date")
                                                .gte(JsonData.of(startDate.toString()))
                                                .lte(JsonData.of(endDate.toString()))
                                        )
                                )
                        )
                )
        );

        return countResponse.count();
    }//
    public Map<String, List<String>> fetchHighlights(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        Map<String, List<String>> highlights = new HashMap<>();

        SearchResponse<Document> response = elasticsearchClient.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .spanNear(n -> n
                                        .clauses(List.of(
                                                SpanQuery.of(c -> c.spanTerm(st -> st.field("text").value(term1))),
                                                SpanQuery.of(c -> c.spanTerm(st -> st.field("text").value(term2)))
                                        ))
                                        .slop(50)
                                        .inOrder(false)
                                )
                        )
                        .highlight(h -> h
                                .fields("text", f -> f
                                        .preTags("<em>")
                                        .postTags("</em>")
                                )
                        )
                        .size(10)
                , Document.class);

        for (Hit<Document> hit : response.hits().hits()) {
            String documentId = hit.id();
            List<String> highlightTexts = new ArrayList<>();

            if (hit.highlight() != null && hit.highlight().containsKey("text")) {
                // Use the list of strings directly
                highlightTexts = hit.highlight().get("text");
            }

            highlights.put(documentId, highlightTexts);
        }

        return highlights;
    }
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

    public static class HighlightHit { // Add the 'static' keyword here
        private Map<String, HighlightField> highlight;
        public Map<String, HighlightField> getHighlight() {
            return highlight;
        }
        private String title;
        private List<String> authors;
        @JsonProperty("published_date") // Map the property to the JSON field
        private String published_date;
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPublishedDate() {
            return published_date;
        }

        public void setPublishedDate(String published_date) {
            this.published_date = published_date;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Map<String, HighlightField> highlight() {
            return highlight;
        }

        public void setHighlight(Map<String, HighlightField> highlight) {
            this.highlight = highlight;
        }

        static class HighlightField {
            private List<String> fragments;

            public List<String> fragments() {
                return fragments;
            }

            public void setFragments(List<String> fragments) {
                this.fragments = fragments;
            }
        }
    }

    public List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index) throws IOException {

        List<DocumentData> documents = new ArrayList<>();

        try {
            // Constructing the query
            // Constructing the query
            // Constructing the query
            SearchResponse<Document> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("topic")
                                                            .value(topicId) // Corrected line
                                                    )
                                            )
                                            .must(m -> m
                                                    .range(r -> r
                                                            .field("published_date")
                                                            .gte(JsonData.of(startDate.toString())) // Keep using JsonData.of() with LocalDate.toString()
                                                            .lte(JsonData.of(endDate.toString()))
                                                    )
                                            )
                                    )
                            )
                            .size(10000), // Adjust size as needed
                    Document.class
            );

//            System.out.println("response:"+ response);

            // Parsing the response
            for (Hit<Document> hit : response.hits().hits()) {
                Document doc = hit.source();
                String title = doc.getTitle();
                String date = doc.getPublishedDate();
                String url = doc.getUrl();

                documents.add(new DocumentData(title, date, url)); // Storing each document's data
            }

            return documents;


        } finally {

        }
    }



    /**
     * Predicts the keyword count for a given keyword based on the correlation with another keyword.
     *
     * @param keyword1   The first keyword.
     * @param keyword2   The second keyword.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return The predicted keyword count.
     * @throws IOException, InterruptedException If there's an issue executing the Python script or reading its output.
     */
//    public static int predictKeywordCount(String keyword1, String keyword2, LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException {
//        // Calculate keyword counts maps
//        Map<LocalDate, Integer> keywordCountsMap1 = getKeywordCounts(keyword1, startDate, endDate);
//        Map<LocalDate, Integer> keywordCountsMap2 = getKeywordCounts(keyword2, startDate, endDate);
//
//        // Convert the maps to csv strings
//        String csv1 = convertMapToCsv(keywordCountsMap1);
//        String csv2 = convertMapToCsv(keywordCountsMap2);
//
//        //System.out.println(csv1 + " this " + csv2);
//        // Write the csv strings to files
//        Path file1 = Paths.get("keywordCountsMap1.csv");
//        Files.write(file1, csv1.getBytes()
//        );
//
//        Path file2 = Paths.get("keywordCountsMap2.csv");
//        Files.write(file2, csv2.getBytes());
//
//        // Execute the Python script
//
//        // Execute the Python script
//        Process p = Runtime.getRuntime().exec("python3 src/main/resources/time_series_analysis.py " + file1 + " " + file2);
//        // Read the output from the Python script
//        double forecastedCount = 0.0;
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
//            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);  // Print every line of output
//                if (!line.isEmpty()) {
//                    try {
//                        forecastedCount = Double.parseDouble(line);
//                    } catch (NumberFormatException e) {
//                        System.err.println("Python script output is not a number: " + line);
//                    }
//                }
//            }
//        }
//
//        try (BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
//            String errLine;
//            while ((errLine = err.readLine()) != null) {
//                System.out.println(errLine);
//            }
//        }
//
//        p.waitFor();
//
//
//
//        // Delete the csv files
//        Files.delete(file1);
//        Files.delete(file2);
//
//        int roundedForecastedCount = (int) Math.round(forecastedCount);
//
//        // Return the forecasted count
//        return roundedForecastedCount;
//    }

    /**
     * Converts a map of dates to keyword counts to a CSV string.
     *
     * @param map The map to convert.
     * @return The CSV string.
     */
    private static String convertMapToCsv(Map<LocalDate, Integer> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("date,count\n");
        map.forEach((k, v) -> sb.append(k.toString()).append(",").append(v).append("\n"));
        return sb.toString();
    }

    public Map<String, Float> fetchKeywordPercentages(
            String keyword1, String keyword2, String indexName) throws IOException {
        try {
            long count1 = executeCountQuery(keyword1, indexName);
            long count2 = executeCountQuery(keyword2, indexName);

            long total = count1 + count2;
            float percentage1 = total == 0 ? 0 : (float) count1 / total;
            float percentage2 = total == 0 ? 0 : (float) count2 / total;

            return Map.of(
                    keyword1, percentage1,
                    keyword2, percentage2
            );

        } catch (Exception e) {
            // Handle exception
            throw new RuntimeException("Error fetching keyword percentages", e);
        }
    }

    // Method to execute count query for a specific keyword
    private long executeCountQuery(String keyword, String indexName) throws IOException {
        // Building the query
        var response = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q
                        .match(m -> m
                                .field("text")
                                .query(keyword)
                        )
                )
        );

        return response.count();
    }




}
