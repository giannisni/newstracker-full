package com.getout.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SentimentService {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public SentimentService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public double calculateAverageSentiment(String sentiment_index,String index, String term,  LocalDate startDate, LocalDate endDate) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String start = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"
        String end = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"


        SearchResponse<Object> response = elasticsearchClient.search(s -> s
                        .index(sentiment_index)
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("term.keyword")
                                                        .value(term)
                                                )
                                        )
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("index.keyword")
                                                        .value(index)
                                                )
                                        )
                                        .filter(f -> f
                                                .range(r -> r
                                                        .field("date")
                                                        .gte(JsonData.of(start)) // Keep using JsonData.of() with LocalDate.toString()
                                                        .lte(JsonData.of(end)) // Keep using JsonData.of() with LocalDate.toString()
                                                )
                                        )
                                )
                        ),
                Object.class
        );

        AtomicReference<Double> totalSentiment = new AtomicReference<>(0.0);
        long docCount = response.hits().total().value();
//        System.out.println("Doc count: "+docCount);

        if (docCount > 0) {
            response.hits().hits().forEach(hit -> {
                Map<String, Object> sourceAsMap = (Map<String, Object>) hit.source();
                if (sourceAsMap.containsKey("average_sentiment")) {
                    Object sentimentObj = sourceAsMap.get("average_sentiment");
                    double sentiment = 0.0;
                    if (sentimentObj instanceof Double) {
                        sentiment = (Double) sentimentObj;
                    } else if (sentimentObj instanceof Integer) {
                        sentiment = ((Integer) sentimentObj).doubleValue();
                    }
//                    System.out.println("sentiment score: " + sentiment);
                    double finalSentiment = sentiment;
                    totalSentiment.updateAndGet(v -> v + finalSentiment);
                }

            });

            double average = totalSentiment.get() / docCount;
            double result = Math.round(average * 10.0) / 10.0;

//            System.out.println("Average sentiment: "+result);
            return result; // Round to 1 decimal place
        }


        return 0.0; // Return 0 if no documents are found
    }
}
