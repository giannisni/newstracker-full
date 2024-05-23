//package com.getout.verse;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.CountResponse;
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.core.search.Hit;
//import com.getout.service.KeywordFrequencyService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class KeywordFrequencyServiceTest {
//
//    @Mock
//    private ElasticsearchClient elasticsearchClient;
//
//    @InjectMocks
//    private KeywordFrequencyService keywordFrequencyService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//
//
//
//
//    @Test
//    void testFetchOpenAIData() throws IOException {
//        // Mock elasticsearchClient.search() to return a predefined SearchResponse
//        // Assert the expected behavior of fetchOpenAIData()
//    }
//
//    @Test
//    void testCalculateAverageSentiment() throws IOException {
//        // Mock elasticsearchClient.search() to return a predefined SearchResponse
//        // Assert the expected behavior of calculateAverageSentiment()
//    }
//}
