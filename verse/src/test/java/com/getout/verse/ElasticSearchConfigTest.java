//package com.getout.verse;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@TestPropertySource(properties = {
//        "elasticsearch.host=localhost",  // These property names should match the field names in ElasticsearchProperties class
//        "elasticsearch.port=9200",
//        "elasticsearch.username=username",
//        "elasticsearch.password=password",
//        "elasticsearch.protocol=http"
//})
//public class ElasticSearchConfigTest {
//
//    @Autowired
//    private ElasticsearchClient elasticsearchClient;
//
//    @Test
//    public void elasticsearchClientBeanExists() {
//        assertNotNull(elasticsearchClient, "Elasticsearch client bean should be configured");
//    }
//}
