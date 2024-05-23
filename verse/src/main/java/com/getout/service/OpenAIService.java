package com.getout.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.getout.model.OpenAIData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public OpenAIService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<OpenAIData> fetchOpenAIData(String indexName) throws IOException {
        SearchResponse<Map> searchResponse = elasticsearchClient.search(s -> s
                .index(indexName)
                .size(1000)
                .query(q -> q.matchAll(m -> m)), Map.class);

        List<OpenAIData> openAIDataList = new ArrayList<>();
        for (Hit<Map> hit : searchResponse.hits().hits()) {
            Map<String, Object> sourceAsMap = hit.source();
            Object openAIObject = sourceAsMap.get("OpenAI");

            String openAIString = "";
            if (openAIObject instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> openAIList = (List<String>) openAIObject;
                openAIString = String.join(", ", openAIList);
            } else if (openAIObject instanceof String) {
                openAIString = (String) openAIObject;
            }

            Integer count = (Integer) sourceAsMap.get("Count");
            openAIDataList.add(new OpenAIData(openAIString, count));
        }

        return openAIDataList;
    }
}
