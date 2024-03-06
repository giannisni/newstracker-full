//package com.getout.verse;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.IndexRequest;
//import co.elastic.clients.json.JsonData;
//import com.getout.service.IndexMap;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.verify;
//
//class IndexMapTest {
//
//    @Mock
//    private ElasticsearchClient client;
//
//    @InjectMocks
//    private IndexMap IndexMapService; // Assuming indexSortedMap is in YourService
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testIndexSortedMap() throws IOException, InterruptedException {
//        Map<LocalDate, Integer> sortedMap = new ConcurrentHashMap<>();
//        sortedMap.put(LocalDate.now(), 1); // Add test data as needed
//
//        doAnswer(invocation -> {
//            IndexRequest<JsonData> request = invocation.getArgument(0);
//            // Optionally, inspect the request object to verify its contents
//            System.out.println("Indexing request: " + request);
//            return null; // Mock the response
//        }).when(client).index(any(IndexRequest.class));
//
//        IndexMapService.indexSortedMap("fromIndex", sortedMap, "testKeyword", "toIndex");
//
//        // Verify the index method was called the expected number of times
//        verify(client).index(any(IndexRequest.class));
//        // Additional verifications can be added here
//    }
//}
