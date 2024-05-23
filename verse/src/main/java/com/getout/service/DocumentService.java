package com.getout.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.SpanQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.SpanTermQuery;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.getout.model.Document;
import com.getout.model.DocumentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class DocumentService {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public DocumentService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index, String searchTerm) throws IOException {
        List<DocumentData> documents = new ArrayList<>();
        HashSet<String> uniqueUrls = new HashSet<>();

        SearchResponse<Document> response = elasticsearchClient.search(s -> s
                .index(index)
                .query(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field("topic").value(topicId)))
                        .must(m -> m.match(t -> t.field("document").query(searchTerm)))
                        .must(m -> m.range(r -> r.field("published_date")
                                .gte(JsonData.of(startDate.toString()))
                                .lte(JsonData.of(endDate.toString()))))))
                .size(10000), Document.class);

        for (Hit<Document> hit : response.hits().hits()) {
            Document doc = hit.source();
            String title = doc.getTitle();
            String date = doc.getPublishedDate();
            String url = doc.getUrl();

            if (uniqueUrls.add(title)) {
                documents.add(new DocumentData(title, date, url));
            }
        }

        return documents;
    }

    public List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index) throws IOException {
        List<DocumentData> documents = new ArrayList<>();
        HashSet<String> uniqueUrls = new HashSet<>();

        SearchResponse<Document> response = elasticsearchClient.search(s -> s
                .index(index)
                .query(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field("topic").value(topicId)))
                        .must(m -> m.range(r -> r.field("published_date")
                                .gte(JsonData.of(startDate.toString()))
                                .lte(JsonData.of(endDate.toString()))))))
                .size(10000), Document.class);

        for (Hit<Document> hit : response.hits().hits()) {
            Document doc = hit.source();
            String title = doc.getTitle();
            String date = doc.getPublishedDate();
            String url = doc.getUrl();

            if (uniqueUrls.add(url)) {
                documents.add(new DocumentData(title, date, url));
            }
        }

        return documents;
    }

    public Map<String, List<String>> fetchHighlights(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        Map<String, List<String>> highlights = new HashMap<>();

        SearchResponse<Document> response = elasticsearchClient.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .spanNear(n -> n
                                        .clauses(List.of(
                                                SpanQuery.of(c -> c.spanTerm(st -> st.field("text").value(term1))),
                                                SpanQuery.of(c -> c.spanTerm(st -> st.field("text").value(term2)))
                                        ))
                                        .slop(50)
                                        .inOrder(false)
                                )
                        )
                        .highlight(h -> h
                                .fields("text", f -> f
                                        .preTags("<em>")
                                        .postTags("</em>")
                                )
                        )
                        .size(10)
                , Document.class);

        for (Hit<Document> hit : response.hits().hits()) {
            String documentId = hit.id();
            List<String> highlightTexts = new ArrayList<>();

            if (hit.highlight() != null && hit.highlight().containsKey("text")) {
                // Use the list of strings directly
                highlightTexts = hit.highlight().get("text");
            }

            highlights.put(documentId, highlightTexts);
        }

        return highlights;
    }

    public Map<String, Float> fetchTermPercentages(String term1, String term2, String term3, String term4, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        long count1 = executeCountQuery(term1, term2, indexName, startDate, endDate);
        long count2 = executeCountQuery(term3, term4, indexName, startDate, endDate);

        long total = count1 + count2;
        float percentage1 = total > 0 ? (float) count1 / total * 100 : 0;
        float percentage2 = total > 0 ? (float) count2 / total * 100 : 0;

        return Map.of(
                term1 + " " + term2, percentage1,
                term3 + " " + term4, percentage2
        );
    }

    private long executeCountQuery(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        SpanTermQuery termQuery1 = SpanTermQuery.of(st -> st.field("text").value(term1));
        SpanTermQuery termQuery2 = SpanTermQuery.of(st -> st.field("text").value(term2));

        CountResponse countResponse = elasticsearchClient.count(c -> c
                .index(indexName)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .spanNear(sn -> sn
                                                .inOrder(false)
                                                .slop(50)
                                                .clauses(cl -> cl.spanTerm(termQuery1))
                                                .clauses(cl -> cl.spanTerm(termQuery2))
                                        )
                                )
                                .filter(f -> f
                                        .range(r -> r
                                                .field("published_date")
                                                .gte(JsonData.of(startDate.toString()))
                                                .lte(JsonData.of(endDate.toString()))
                                        )
                                )
                        )
                )
        );

        return countResponse.count();
    }//
}
