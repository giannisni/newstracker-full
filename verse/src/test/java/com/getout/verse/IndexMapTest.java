//package com.getout.verse;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.IndexRequest;
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import co.elastic.clients.json.JsonData;
//import com.getout.service.IndexMap;
//import jakarta.json.Json;
//import jakarta.json.JsonObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//class IndexMapTest {
//
//    @Mock
//    private ElasticsearchClient client;
//
//    @InjectMocks
//    private IndexMap indexMap;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testIndexSortedMap() throws IOException, InterruptedException {
//        String fromIndex = "source-index";
//        String toIndex = "target-index";
//        String keyword = "test-keyword";
//
//        // Create a sorted map
//        Map<LocalDate, Integer> sortedMap = new HashMap<>();
//        sortedMap.put(LocalDate.of(2023, 1, 1), 10);
//        sortedMap.put(LocalDate.of(2023, 1, 2), 20);
//
//        // Create a mock IndexResponse
//        IndexResponse mockResponse = mock(IndexResponse.class);
//
//        // Capture the IndexRequest argument
//        ArgumentCaptor<IndexRequest> argumentCaptor = ArgumentCaptor.forClass(IndexRequest.class);
//
//        // Mock the index method
//        when(client.index(any(IndexRequest.class))).thenReturn(mockResponse);
//
//        // Call the method to be tested
//        indexMap.indexSortedMap(fromIndex, sortedMap, keyword, toIndex);
//
//        // Verify that the index method was called twice
//        verify(client, times(2)).index(argumentCaptor.capture());
//
//        // Check the captured arguments
//        for (IndexRequest<JsonData> request : argumentCaptor.getAllValues()) {
//            assertEquals(toIndex, request.index());
//
//            // Parse the JSON string stored in JsonData
//            JsonObject jsonObject = Json.createReader(new StringReader(request.source().toString())).readObject();
//
//            assertEquals(keyword, jsonObject.getString("keyword"));
//            assertEquals(fromIndex, jsonObject.getString("index"));
//            assertTrue(sortedMap.containsKey(LocalDate.parse(jsonObject.getString("date"))));
//            assertTrue(sortedMap.containsValue(jsonObject.getInt("value")));
//        }
//    }
//}
