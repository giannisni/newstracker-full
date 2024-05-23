package com.getout.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import com.getout.model.Document;

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


@Service
public class WordFrequencyBatch {
    private static AtomicInteger processedHits = new AtomicInteger(0);

    private final ElasticsearchClient client;

    @Autowired
    public WordFrequencyBatch(ElasticsearchClient client) {
        this.client = client;
    }


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








}



