//package com.getout.service;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import co.elastic.clients.elasticsearch.core.ScrollRequest;
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.*;
//import org.elasticsearch.client.Request;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.core.TimeValue;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.Scroll;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.xcontent.XContentBuilder;
//import org.elasticsearch.xcontent.XContentFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WordFrequencyBatch {
//
//    public static Map<LocalDate, Integer> searchKeywordFrequency(String indexName, String keyword, int batchSize) throws IOException {
//        // Set up Elasticsearch client and query for keyword frequency data
//        long startTime = System.currentTimeMillis();
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        Map<LocalDate, Integer> dateFrequencyMap = new HashMap<>();
//
//        // Define search query
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("content", keyword))
//                .filter(QueryBuilders.existsQuery("published_date")));
//
//        searchSourceBuilder.size(batchSize);
//
//        // Initialize scroll
//        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
//        SearchRequest searchRequest = new SearchRequest(indexName);
//        searchRequest.scroll(scroll);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHit[] searchHits = searchResponse.getHits().getHits();
//
//        // Process search query results using scroll
//        while (searchHits != null && searchHits.length > 0) {
//            // Iterate through Elasticsearch query results and populate map
//            for (SearchHit hit : searchHits) {
//                Object publishedDateObj = hit.getSourceAsMap().get("published_date");
//                LocalDate date = null;
//
//                if (publishedDateObj instanceof String) {
//                    date = LocalDate.parse(publishedDateObj.toString(), formatter);
//                } else if (publishedDateObj instanceof List) {
//                    List<String> publishedDateList = (List<String>) publishedDateObj;
//                    if (!publishedDateList.isEmpty()) {
//                        date = LocalDate.parse(publishedDateList.get(0), formatter);
//                    }
//                }
//
//                // Count frequency of keyword in content field
//                String content = hit.getSourceAsMap().get("content").toString();
//                int frequency = (content.split(keyword, -1).length) - 1;
//
//                // Check if date already exists in map; if not, create new entry
//                if (!dateFrequencyMap.containsKey(date)) {
//                    dateFrequencyMap.put(date, 0);
//                }
//
//                // Add frequency to existing total for this date
//                int existingTotal = dateFrequencyMap.get(date);
//                dateFrequencyMap.put(date, existingTotal + frequency);
//            }
//
//            // Prepare next scroll iteration
//            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
//            scrollRequest.scroll(scroll);
//            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchResponse.getScrollId();
//            searchHits = searchResponse.getHits().getHits();
//        }
//
//        // Clear scroll
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//
//        // Clean up Elasticsearch client resources
//        client.close();
//
//        // Sort the dateFrequencyMap by date
//        Map<LocalDate, Integer> sortedMap = new TreeMap<>(Comparator.naturalOrder());
//        sortedMap.putAll(dateFrequencyMap);
//
//        // Index the sorted map
//        indexMap.indexSortedMap("sorted", sortedMap);
//
//        // Calculate elapsed time and print it
//        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
//        double elapsedTimeSec = elapsedTimeMillis / 1000.0;
//        System.out.printf("Elapsed time: %.3f seconds%n", elapsedTimeSec);
//
//        // Return the sorted map
//        return sortedMap;
//    }
//}
