package com.getout.verse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.getout.service.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeywordServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private KeywordService keywordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWordCloudData() throws IOException {
        String indexName = "test-index";

        // Create mock hits
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("keyword", "testKeyword");
        sourceMap.put("frequency", 10.0);

        Hit<Object> hit = Hit.of(h -> h
                .index(indexName)
                .id("1")
                .source(sourceMap)
        );
        List<Hit<Object>> hits = List.of(hit);

        // Create mock response
        HitsMetadata<Object> hitsMetadata = HitsMetadata.of(h -> h
                .hits(hits)
                .total(th -> th
                        .value(1L)
                        .relation(TotalHitsRelation.Eq) // Use TotalHitsRelation enum
                )
        );
        ShardStatistics shardStatistics = ShardStatistics.of(s -> s
                .total(1)
                .successful(1)
                .skipped(0)
                .failed(0)
        );
        SearchResponse<Object> mockResponse = SearchResponse.of(r -> r
                .took(1L)
                .timedOut(false) // Set timedOut property
                .shards(shardStatistics) // Set shards property
                .hits(hitsMetadata)
        );

        // Mock the search method
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Object.class))).thenReturn(mockResponse);

        List<Map<String, Object>> result = keywordService.getWordCloudData(indexName);

        assertEquals(1, result.size());
        assertEquals("testKeyword", result.get(0).get("text"));
        assertEquals(10.0, result.get(0).get("value"));
    }

    @Test
    void testGetKeywordCounts() throws IOException {
        String index = "test-index";
        String keyword = "testKeyword";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Create mock hits
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("date", "2023-01-01");
        sourceMap.put("value", 5);

        Hit<Map> hit = Hit.of(h -> h
                .index(index)
                .id("1")
                .source(sourceMap)
        );
        List<Hit<Map>> hits = List.of(hit);

        // Create mock response
        HitsMetadata<Map> hitsMetadata = HitsMetadata.of(h -> h
                .hits(hits)
                .total(th -> th
                        .value(1L)
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

        Map<LocalDate, Integer> result = keywordService.getKeywordCounts(index, keyword, startDate, endDate);

        assertEquals(1, result.size());
        assertEquals(5, result.get(LocalDate.of(2023, 1, 1)));
    }

    @Test
    void testGetTopKeywordCounts() {
        Map<LocalDate, Integer> keywordCounts = new HashMap<>();
        keywordCounts.put(LocalDate.of(2023, 1, 1), 5);
        keywordCounts.put(LocalDate.of(2023, 1, 2), 10);
        keywordCounts.put(LocalDate.of(2023, 1, 3), 3);

        Map<LocalDate, Integer> result = KeywordService.getTopKeywordCounts(keywordCounts);

        assertEquals(3, result.size());
        assertEquals(10, result.get(LocalDate.of(2023, 1, 2)));
        assertEquals(5, result.get(LocalDate.of(2023, 1, 1)));
        assertEquals(3, result.get(LocalDate.of(2023, 1, 3)));
    }

    @Test
    void testFetchKeywordPercentages() throws IOException {
        String keyword1 = "keyword1";
        String keyword2 = "keyword2";
        String indexName = "test-index";

        CountResponse countResponse1 = mock(CountResponse.class);
        CountResponse countResponse2 = mock(CountResponse.class);
        when(countResponse1.count()).thenReturn(30L);
        when(countResponse2.count()).thenReturn(70L);

        when(elasticsearchClient.count(any(CountRequest.class))).thenReturn(countResponse1).thenReturn(countResponse2);

        Map<String, Float> result = keywordService.fetchKeywordPercentages(keyword1, keyword2, indexName);

        assertEquals(0.3f, result.get(keyword1));
        assertEquals(0.7f, result.get(keyword2));
    }
}
