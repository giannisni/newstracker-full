package com.getout.verse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.getout.model.OpenAIData;
import com.getout.service.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenAIServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private OpenAIService openAIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchOpenAIData() throws IOException {
        String indexName = "test-index";
        
        // Create mock hits
        Map<String, Object> sourceMap1 = new HashMap<>();
        sourceMap1.put("OpenAI", Arrays.asList("data1", "data2"));
        sourceMap1.put("Count", 10);

        Map<String, Object> sourceMap2 = new HashMap<>();
        sourceMap2.put("OpenAI", "data3");
        sourceMap2.put("Count", 20);

        Hit<Map> hit1 = Hit.of(h -> h
                .index(indexName)
                .id("1")
                .source(sourceMap1)
        );

        Hit<Map> hit2 = Hit.of(h -> h
                .index(indexName)
                .id("2")
                .source(sourceMap2)
        );

        List<Hit<Map>> hits = List.of(hit1, hit2);

        // Create mock response
        HitsMetadata<Map> hitsMetadata = HitsMetadata.of(h -> h
                .hits(hits)
                .total(th -> th
                        .value(2L)
                        .relation(TotalHitsRelation.Eq) // Use TotalHitsRelation enum
                )
        );
        ShardStatistics shardStatistics = ShardStatistics.of(s -> s
                .total(1)
                .successful(1)
                .skipped(0)
                .failed(0)
        );
        SearchResponse<Map> mockResponse = SearchResponse.of(r -> r
                .took(1L)
                .timedOut(false) // Set timedOut property
                .shards(shardStatistics) // Set shards property
                .hits(hitsMetadata)
        );
        
        // Mock the search method
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class))).thenReturn(mockResponse);

        List<OpenAIData> result = openAIService.fetchOpenAIData(indexName);

        assertEquals(2, result.size());
        assertEquals("data1, data2", result.get(0).getOpenAI());
        assertEquals(10, result.get(0).getCount());
        assertEquals("data3", result.get(1).getOpenAI());
        assertEquals(20, result.get(1).getCount());
    }
}
