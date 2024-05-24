package com.getout.verse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import com.getout.service.SentimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SentimentServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private SentimentService sentimentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateAverageSentiment() throws IOException {
        String sentimentIndex = "sentiment-index";
        String index = "test-index";
        String term = "test-term";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Create mock hits
        Map<String, Object> sourceMap1 = new HashMap<>();
        sourceMap1.put("average_sentiment", 2.5);

        Map<String, Object> sourceMap2 = new HashMap<>();
        sourceMap2.put("average_sentiment", 3.5);

        Hit<Object> hit1 = Hit.of(h -> h
                .index(sentimentIndex)
                .id("1")
                .source(sourceMap1)
        );

        Hit<Object> hit2 = Hit.of(h -> h
                .index(sentimentIndex)
                .id("2")
                .source(sourceMap2)
        );

        List<Hit<Object>> hits = List.of(hit1, hit2);

        // Create mock response
        HitsMetadata<Object> hitsMetadata = HitsMetadata.of(h -> h
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
        SearchResponse<Object> mockResponse = SearchResponse.of(r -> r
                .took(1L)
                .timedOut(false) // Set timedOut property
                .shards(shardStatistics) // Set shards property
                .hits(hitsMetadata)
        );

        // Mock the search method
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Object.class))).thenReturn(mockResponse);

        double result = sentimentService.calculateAverageSentiment(sentimentIndex, index, term, startDate, endDate);

        assertEquals(3.0, result, 0.1); // Check if the result is correct
    }

    @Test
    void testCalculateAverageSentimentNoDocuments() throws IOException {
        String sentimentIndex = "sentiment-index";
        String index = "test-index";
        String term = "test-term";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Create mock response with no hits
        HitsMetadata<Object> hitsMetadata = HitsMetadata.of(h -> h
                .hits(List.of())
                .total(th -> th
                        .value(0L)
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

        double result = sentimentService.calculateAverageSentiment(sentimentIndex, index, term, startDate, endDate);

        assertEquals(0.0, result); // Check if the result is correct for no documents
    }
}
