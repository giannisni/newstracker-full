package com.getout.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeywordService {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public KeywordService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<Map<String, Object>> getWordCloudData(String indexName) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(indexName)
                .query(q -> q.matchAll(m -> m))
                .size(10000));

        SearchResponse<Object> searchResponse = elasticsearchClient.search(searchRequest, Object.class);

        List<Map<String, Object>> wordCloudData = new ArrayList<>();
        for (Hit<Object> hit : searchResponse.hits().hits()) {
            Map<String, Object> sourceAsMap = (Map<String, Object>) hit.source();
            String keyword = (String) sourceAsMap.get("keyword");
            Double frequency = (Double) sourceAsMap.get("frequency");
            wordCloudData.add(Map.of("text", keyword, "value", frequency));
        }

        return wordCloudData;
    }

    public Map<LocalDate, Integer> getKeywordCounts(String index, String keyword, LocalDate startDate, LocalDate endDate) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field("keyword.keyword").value(keyword)))
                        .filter(f -> f.range(r -> r.field("date")
                                .gte(JsonData.of(startDate.toString()))
                                .lte(JsonData.of(endDate.toString()))))))
                .size(10000));

        SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);

        Map<LocalDate, Integer> keywordCounts = new HashMap<>();
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> source = hit.source();
            LocalDate date = LocalDate.parse((String) source.get("date"));
            Integer value = (Integer) source.get("value");
            keywordCounts.put(date, value);
        }

        return keywordCounts;
    }

    public static Map<LocalDate, Integer> getTopKeywordCounts(Map<LocalDate, Integer> keywordCounts) {
        return keywordCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<LocalDate, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    public Map<String, Float> fetchKeywordPercentages(String keyword1, String keyword2, String indexName) throws IOException {
        long count1 = executeCountQuery(keyword1, indexName);
        long count2 = executeCountQuery(keyword2, indexName);

        long total = count1 + count2;
        float percentage1 = total == 0 ? 0 : (float) count1 / total;
        float percentage2 = total == 0 ? 0 : (float) count2 / total;

        return Map.of(
                keyword1, percentage1,
                keyword2, percentage2
        );
    }

    private long executeCountQuery(String keyword, String indexName) throws IOException {
        var response = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q.match(m -> m.field("text").query(keyword))));

        return response.count();
    }
}
