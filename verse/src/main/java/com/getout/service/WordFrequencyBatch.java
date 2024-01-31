package com.getout.service;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeFormatterBuilder;
//import java.time.temporal.ChronoField;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.logging.Logger;
//import java.util.concurrent.atomic.AtomicInteger;
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch._types.ElasticsearchException;
//import co.elastic.clients.elasticsearch._types.query_dsl.Query;
//import co.elastic.clients.elasticsearch._types.SortOrder;
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
//import co.elastic.clients.json.JsonData;
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.*;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.core.TimeValue;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.Scroll;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import co.elastic.clients.elasticsearch.core.search.Hit;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import com.getout.model.Document;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static com.getout.util.Constants.elastic_host;

@Service
public class WordFrequencyBatch {
    private static AtomicInteger processedHits = new AtomicInteger(0);

    private final ElasticsearchClient client;

    @Autowired
    public WordFrequencyBatch(ElasticsearchClient client) {
        this.client = client;
    }

    private static final Map<Character, String> greekToGreeklishMap = new HashMap<Character, String>() {{
        put('α', "a"); put('Α', "A");
        put('β', "v"); put('Β', "V");
        put('γ', "g"); put('Γ', "G");
        put('δ', "d"); put('Δ', "D");
        put('ε', "e"); put('Ε', "E");
        put('ζ', "z"); put('Ζ', "Z");
        put('η', "i"); put('Η', "H");
        put('θ', "th"); put('Θ', "Th");
        put('ι', "i"); put('Ι', "I");
        put('κ', "k"); put('Κ', "K");
        put('λ', "l"); put('Λ', "L");
        put('μ', "m"); put('Μ', "M");
        put('ν', "n"); put('Ν', "N");
        put('ξ', "x"); put('Ξ', "X");
        put('ο', "o"); put('Ο', "O");
        put('π', "p"); put('Π', "P");
        put('ρ', "r"); put('Ρ', "R");
        put('σ', "s"); put('ς', "s"); put('Σ', "S");
        put('τ', "t"); put('Τ', "T");
        put('υ', "y"); put('Υ', "Y");
        put('φ', "f"); put('Φ', "F");
        put('χ', "x"); put('Χ', "X");
        put('ψ', "ps"); put('Ψ', "Ps");
        put('ω', "o"); put('Ω', "O");
    }};
    private static final Logger logger = Logger.getLogger(WordFrequencyBatch.class.getName());

    /**
     * Searches for the frequency of given keywords within a specified date range in an Elasticsearch index.
     *
     * @param indexName  The name of the Elasticsearch index to search.
     * @param topic      The topic of interest.
     * @param keywords   List of keywords to search for.
     * @param batchSize  The number of search results to retrieve in each batch.
     * @param startDate  The start date of the search range.
     * @param endDate    The end date of the search range.
     * @return A sorted map with dates as keys and keyword frequencies as values.
     * @throws IOException, InterruptedException, ExecutionException
     */




//    public static Map<LocalDate, Integer> searchTopicFrequency(String indexName,String toindex, String topic, List<String> keywords, int batchSize, String startDate, String endDate) throws IOException, InterruptedException, ExecutionException {
//        long overallStartTime = System.currentTimeMillis();
//
//        // Initialize Elasticsearch client
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 9200, "http")));
//
//        // Define date formatter for parsing dates from Elasticsearch
//        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
//                .optionalStart()
//                .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 1, 9, true)
//                .optionalEnd()
//                .optionalStart()
//                .appendLiteral('Z')
//                .optionalEnd()
//                .toFormatter();
//
//        Map<LocalDate, Integer> dateFrequencyMap = new ConcurrentHashMap<>();
//
//        // Build the search query
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        for (String keyword : keywords) {
//            boolQuery.should(QueryBuilders.matchQuery("text", keyword));
//        }
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(boolQuery
//                        .filter(QueryBuilders.existsQuery("published_date"))
//                        .filter(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate)))
//                .size(batchSize);
//
//        // Initialize scroll for batch processing
//        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
//        SearchRequest searchRequest = new SearchRequest(indexName).scroll(scroll).source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHits hits = searchResponse.getHits();
//        final long totalHits = hits.getTotalHits().value;
//        SearchHit[] searchHits = hits.getHits();
//
//        // Create a fixed thread pool for concurrent processing
//        int numberOfThreads = 4;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//
//        // Process search results in batches using scroll
//        while (searchHits != null && searchHits.length > 0) {
//            List<Future<Void>> futures = new ArrayList<>();
//
//            for (SearchHit hit : searchHits) {
//                Callable<Void> task = () -> {
//                    int currentProcessedHits = processedHits.incrementAndGet();
//                    logger.info("Total processed hits: " + currentProcessedHits + " / " + totalHits);
//
//                    Object publishedDateObj = hit.getSourceAsMap().get("published_date");
//                    LocalDate date = extractDate(publishedDateObj, formatter);
//
//                    String content = hit.getSourceAsMap().get("text").toString();
//                    int frequency = countKeywordFrequency(content, keywords);
//
//                    dateFrequencyMap.merge(date, frequency, Integer::sum);
//
//                    return null;
//                };
//
//                futures.add(executorService.submit(task));
//            }
//
//            for (Future<Void> future : futures) {
//                future.get();
//            }
//
//            // Fetch the next batch of search results
//            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId).scroll(scroll);
//            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchResponse.getScrollId();
//            searchHits = searchResponse.getHits().getHits();
//        }
//
//        executorService.shutdown();
//
//        // Clear the scroll context
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//
//        client.close();
//
//
//        // Sort the map by date
//        Map<LocalDate, Integer> sortedMap = new TreeMap<>(dateFrequencyMap);
//
//        System.out.print("Map " + sortedMap);
//        indexSortedMap(toindex, sortedMap, topic,indexName);
//
//        long overallElapsedTimeMillis = System.currentTimeMillis() - overallStartTime;
//        double overallElapsedTimeSec = overallElapsedTimeMillis / 1000.0;
//        System.out.printf("Total Elapsed time: %.3f seconds%n", overallElapsedTimeSec);
//
//        System.out.println(String.join(",", keywords) + " sorted");
//        return sortedMap;
//    }

    /**
     * Extracts a LocalDate from an object, using the provided formatter.
     *
     * @param publishedDateObj The object containing the date.
     * @param formatter        The formatter to use for parsing.
     * @return The extracted LocalDate, or null if extraction is not possible.
     */
    private static LocalDate extractDate(Object publishedDateObj, DateTimeFormatter formatter) {
        if (publishedDateObj instanceof String) {
            return LocalDate.parse(publishedDateObj.toString(), formatter);
        } else if (publishedDateObj instanceof List) {
            List<String> publishedDateList = (List<String>) publishedDateObj;
            if (!publishedDateList.isEmpty()) {
                return LocalDate.parse(publishedDateList.get(0), formatter);
            }
        }
        return null;
    }

    /**
     * Counts the frequency of each keyword in the provided content.
     *
     * @param content  The content to search within.
     * @param keywords The keywords to count.
     * @return The total frequency of all keywords.
     */
    private static int countKeywordFrequency(String content, List<String> keywords) {
        int frequency = 0;

        String contentLower = content.toLowerCase(); // Convert content to lower case

        for (String keyword : keywords) {
            String keywordLower = keyword.toLowerCase(); // Convert keyword to lower case
            frequency += (contentLower.split(keywordLower, -1).length) - 1;
        }
//        System.out.print("Frequency : " + frequency);

        return frequency;
    }



    /**
     * Searches for the frequency of a given keyword within a specified date range in an Elasticsearch index.
     *
     * @param indexName  The name of the Elasticsearch index to search.
     * @param keyword    The keyword to search for.
     * @param startDate  The start date of the search range.
     * @param endDate    The end date of the search range.
     * @return A sorted map with dates as keys and keyword frequencies as values.
     */


    public Map<LocalDate, Integer> searchKeywordFrequency(String indexName,String toIndex, String keyword, String startDate, String endDate) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<LocalDate, Integer> dateFrequencyMap = new ConcurrentHashMap<>();



        try {
            // Preparing the search request
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index(indexName)
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m
                                            .match(t -> t
                                                    .field("text")
                                                    .query(keyword)
                                            )
                                    )
                                    .filter(f -> f
                                            .range(r -> r
                                                    .field("published_date")
                                                    .gte(JsonData.of(startDate))
                                                    .lte(JsonData.of(endDate))
                                            )
                                    )
                            )
                    )
                    .size(1000) // Adjust batch size as needed
                    .build();


            SearchResponse<Document> searchResponse = client.search(searchRequest, Document.class);
//            System.out.println("searchResponse = " + searchResponse);


            HitsMetadata<Document> hits = searchResponse.hits();
//            System.out.println("Hits: " + Arrays.toString(hits.hits().toArray()));

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            try {
                for (Hit<Document> hit : hits.hits()) {
                    executorService.submit(() -> {
                        try {
                            Document doc = hit.source();
                            if (doc != null && doc.getText() != null) {
                               // LocalDate date = LocalDate.parse(doc.getPublishedDate(), formatter);
                                String datePart = doc.getPublishedDate().split("T")[0]; // This gets only the date part
                                LocalDate dates = LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE);

                                int frequency = countKeywordFrequency(doc.getText(), Collections.singletonList(keyword));

                                dateFrequencyMap.merge(dates, frequency, Integer::sum);
                            } else {
                                System.out.println("Document or text is null.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // This will print any exception that occurs within the lambda
                        }
                    });
                }
            } finally {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        executorService.shutdownNow(); // Cancel currently executing tasks
                        if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                            System.err.println("ExecutorService did not terminate");
                    }
                } catch (InterruptedException ie) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt(); // Preserve interrupt status
                }
            }

        } catch (Exception e) {  // Catching a general Exception to capture any type of error
            e.printStackTrace(); // Log the stack trace for debugging
        }



        return new TreeMap<>(dateFrequencyMap);
    }


//    public static Map<LocalDate, Integer> searchKeywordFrequency(String indexName,String toindex, String keyword, int batchSize, String startDate, String endDate) throws IOException, InterruptedException, ExecutionException {
//        long overallStartTime = System.currentTimeMillis();
//
//        // Initialize Elasticsearch client
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(elastic_host, 9200, "http")));
//
//        System.out.println("keyword : " + keyword);
//        // Define date formatter for parsing dates from Elasticsearch
//        DateTimeFormatter formatterWithZone = new DateTimeFormatterBuilder()
//                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
//                .optionalStart()
//                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
//                .optionalEnd()
//                .appendOffset("+HH:MM", "Z")
//                .toFormatter();
//
//        DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//
//        DateTimeFormatter combinedFormatter = new DateTimeFormatterBuilder()
//                .appendOptional(formatterWithZone)
//                .appendOptional(formatterWithoutZone)
//                .toFormatter();
//
//
//        Map<LocalDate, Integer> dateFrequencyMap = new ConcurrentHashMap<>();
//
//        // Build the search query
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.matchQuery("text", keyword))
//                        .filter(QueryBuilders.existsQuery("published_date"))
//                        .filter(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate)))
//                .size(batchSize);
//
//        // Initialize scroll for batch processing
//        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
//        SearchRequest searchRequest = new SearchRequest(indexName).scroll(scroll).source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHits hits = searchResponse.getHits();
//        final long totalHits = hits.getTotalHits().value;
//        SearchHit[] searchHits = hits.getHits();
//        System.out.println(searchHits);
//        // Create a fixed thread pool for concurrent processing
//        int numberOfThreads = 4;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//
//        // Process search results in batches using scroll
//        while (searchHits != null && searchHits.length > 0) {
//            List<Future<Void>> futures = new ArrayList<>();
//
//            for (SearchHit hit : searchHits) {
//                Callable<Void> task = () -> {
//                    int currentProcessedHits = processedHits.incrementAndGet();
//                    logger.info("Total processed hits: " + currentProcessedHits + " / " + totalHits);
//
//                    Object publishedDateObj = hit.getSourceAsMap().get("published_date");
//                    LocalDate date = extractDate(publishedDateObj, combinedFormatter);
//
//                    String content = hit.getSourceAsMap().get("text").toString();
//
////                    System.out.println("Content: " + content);
//                    List<String> keywordList = Collections.singletonList(keyword);
//                    System.out.println("KeywordList: " + keywordList);
//                    int frequency = countKeywordFrequency(content, keywordList);
//                    System.out.println("Frequency: " + frequency);
//
//                    dateFrequencyMap.merge(date, frequency, Integer::sum);
//
//                    return null;
//                };
//
//                futures.add(executorService.submit(task));
//            }
//
//            for (Future<Void> future : futures) {
//                future.get();
//            }
//
//            // Fetch the next batch of search results
//            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId).scroll(scroll);
//            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchResponse.getScrollId();
//            searchHits = searchResponse.getHits().getHits();
//        }
//
//        executorService.shutdown();
//
//        // Clear the scroll context
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//
//        client.close();
//
//
//        // Sort the map by date
//        Map<LocalDate, Integer> sortedMap = new TreeMap<>(dateFrequencyMap);
//
//        indexSortedMap(toindex, sortedMap, keyword, indexName);
//
//        long overallElapsedTimeMillis = System.currentTimeMillis() - overallStartTime;
//        double overallElapsedTimeSec = overallElapsedTimeMillis / 1000.0;
//        System.out.printf("Total Elapsed time: %.3f seconds%n", overallElapsedTimeSec);
//
//        System.out.println(keyword + " sorted");
//        return sortedMap;
//    }


    static class MyDocumentClass {
        private String text;
        private String publishedDate;
        private String date; // Instead of LocalDate

        public List<String> getAuthors() {
            return authors;
        }

        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        private List<String> authors;
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        private String title;

        private String keyword; // Add this field

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        private String index; // Add this field

        private Integer value;
        // Getters and setters
        public String getText() {
            return text;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
        }
        public LocalDate getLocalDate() {
            return LocalDate.parse(date);
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }


    }





}



