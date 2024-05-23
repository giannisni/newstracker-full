package com.getout.verse;

import com.getout.component.ScheduledTasks;
import com.getout.model.DocumentData;
import com.getout.model.OpenAIData;
import com.getout.service.TweetMetricsService;
import com.getout.service.WordFrequencyBatch;
import com.getout.service.KeywordFrequencyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.getout.service.KeywordFrequencyService.*;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class WordFrequencyController {
    @Autowired
    private KeywordFrequencyService keywordFrequencyService; // Inject the service

    private final TweetMetricsService tweetMetricsService;

    public WordFrequencyController(TweetMetricsService tweetMetricsService, WordFrequencyBatch wordFrequencyBatch) {
        this.tweetMetricsService = tweetMetricsService;
        this.wordFrequencyBatch = wordFrequencyBatch;

    }

//    @CrossOrigin(origins = "http://localhost:3000")

    @GetMapping("/wordcloud")
    public ResponseEntity<List<Map<String, Object>>> getWordCloudData(@RequestParam String indexName) {
        try {
            List<Map<String, Object>> wordCloudData = keywordFrequencyService.getWordCloudData(indexName);
            return ResponseEntity.ok(wordCloudData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
//
//    @PostMapping("/tweet-metrics")
//    public ResponseEntity<Void> calculateTweetMetrics(@RequestParam String startDate, @RequestParam String endDate) throws IOException {
//        tweetMetricsService.calculateTweetMetrics(startDate, endDate);
//        return ResponseEntity.ok().build();
//    }
//
    @GetMapping("/openai-data")
    public ResponseEntity<?> getOpenAIData(@RequestParam String indexName) {
        try {
            List<OpenAIData> openAIDataList = keywordFrequencyService.fetchOpenAIData(indexName); // Use the injected service
            return ResponseEntity.ok(openAIDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
//
//
//    @GetMapping("/word-frequencies")
//    public Map<String, Integer> getWordFrequenciesFromTopicNew(
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//        try {
//            LocalDate startLocalDate = LocalDate.parse(startDate);
//            LocalDate endLocalDate = LocalDate.parse(endDate);
//            return fetchWordFrequenciesFromTopicNew(startLocalDate, endLocalDate);
//        } catch (Exception e) {
//            // Log the exception for debugging purposes
//            e.printStackTrace();
//            // You might want to handle this differently, perhaps returning an empty map or a default value
//            return new HashMap<>();
//        }
//    }
//
//
//
//
//
            @GetMapping("/highlights")
            public ResponseEntity<Map<String, List<String>>> getHighlights(
                    @RequestParam String term1,
                    @RequestParam String term2,
                    @RequestParam String indexName,
                    @RequestParam String startDate,
                    @RequestParam String endDate) {
                try {
                    LocalDate startLocalDate = LocalDate.parse(startDate);
                    LocalDate endLocalDate = LocalDate.parse(endDate);
                    Map<String, List<String>> highlights = keywordFrequencyService.fetchHighlights(term1, term2, indexName, startLocalDate, endLocalDate);
                    return ResponseEntity.ok(highlights);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().build();
                }
            }


//    @GetMapping("/keyword-frequencies")
//    public Map<String, Integer> getKeywordFrequencies(
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//        try {
//            LocalDate startLocalDate = LocalDate.parse(startDate);
//            LocalDate endLocalDate = LocalDate.parse(endDate);
//            return fetchKeywordFrequencies(startLocalDate, endLocalDate);
//        } catch (Exception e) {
//            // Log the exception for debugging purposes
//            e.printStackTrace();
//            // You might want to handle this differently, perhaps returning an empty map or a default value
//            return new HashMap<>();
//        }
//    }








//    @GetMapping("/correlation")
//    public ResponseEntity<Double> getCorrelation(
//            @RequestParam String keyword1,
//            @RequestParam String keyword2,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
//        // Fetch keyword counts from your service
//        Map<LocalDate, Integer> keywordCounts1 = KeywordFrequencyService.getKeywordCounts(keyword1, startDate, endDate);
//        Map<LocalDate, Integer> keywordCounts2 = KeywordFrequencyService.getKeywordCounts(keyword2, startDate, endDate);
//        // Calculate correlation
//        double correlation = KeywordFrequencyService.calculateCorrelation(keywordCounts1, keywordCounts2);
//        return ResponseEntity.ok(correlation);
//    }
//
            @GetMapping("/counts")
            public ResponseEntity<Map<LocalDate, Integer>> getKeywordCounts(
                    @RequestParam String keyword,
                    @RequestParam String startDate,
                    @RequestParam String endDate, @RequestParam String index) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    LocalDate end = LocalDate.parse(endDate);
                    Map<LocalDate, Integer> keywordCounts = keywordFrequencyService.getKeywordCounts(index, keyword, start, end);
                    return ResponseEntity.ok(keywordCounts);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            @GetMapping("/top-keyword-counts")
            public Map<LocalDate, Integer> getTopKeywordCounts(
                    @RequestParam String index,
                    @RequestParam String keyword,
                    @RequestParam String startDate,
                    @RequestParam String endDate) throws IOException {

                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);

                // Assuming getKeywordCounts is a method within your service class
                Map<LocalDate, Integer> keywordCounts = keywordFrequencyService.getKeywordCounts(index, keyword, start, end);
                return keywordFrequencyService.getTopKeywordCounts(keywordCounts);
            }

            @GetMapping("/calculate")
            public double calculateAverageSentiment(
                    @RequestParam String index,
                    @RequestParam String sentiment_index,
                    @RequestParam String term,
                    @RequestParam String startDate,
                    @RequestParam String endDate) throws Exception {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                Date start = formatter.parse(startDate);
//                Date end = formatter.parse(endDate);
                Double d = keywordFrequencyService.calculateAverageSentiment(index, sentiment_index,term, start, end);
                return d;
            }

//
//    @GetMapping("/getdocs")
//    public ResponseEntity<List<KeywordFrequencyService.DocumentData>> fetchDocuments(
//            @RequestParam String startDate,
//            @RequestParam String endDate,
//            @RequestParam List<String> keywords,
//            @RequestParam String index) {
//        try {
//            List<KeywordFrequencyService.DocumentData> documents = KeywordFrequencyService.fetchDocumentsWithWords(LocalDate.parse(startDate), LocalDate.parse(endDate), keywords, index);
//            return ResponseEntity.ok(documents);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//

    @GetMapping("/documents-from-top-count-dates")
    public Map<LocalDate, List<DocumentData>> getDocumentsFromTopCountDates(
            @RequestParam String index,
            @RequestParam String keyword,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int topicId) throws IOException {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Fetch keyword counts and get the top 3 dates
        Map<LocalDate, Integer> keywordCounts = keywordFrequencyService.getKeywordCounts(index, keyword, start, end);
        Map<LocalDate, Integer> topKeywordCounts = keywordFrequencyService.getTopKeywordCounts(keywordCounts);

        Map<LocalDate, List<DocumentData>> documentsFromTopDates = new LinkedHashMap<>();

        for (LocalDate date : topKeywordCounts.keySet()) {
            List<DocumentData> documents = keywordFrequencyService.fetchDocumentsByTopic(date, date, topicId, index);

            // If you need to limit to 2 documents and your fetch method doesn't support limiting
            if (documents.size() > 2) {
                documents = documents.subList(0, 2); // Keep only the first 2 documents
            }

            documentsFromTopDates.put(date, documents);
        }

        return documentsFromTopDates;
    }

    @GetMapping("/by-topic")
    public ResponseEntity<List<DocumentData>> getDocumentsByTopic(


            @RequestParam int topicId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String index,
            @RequestParam(required = false) String searchTerm) {
        try {
//            System.out.println("Topicindex: " + index);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<DocumentData> documents;

            // Check if searchTerm is present and call the appropriate method
            if (searchTerm != null && !searchTerm.isEmpty()) {
                documents = keywordFrequencyService.fetchDocumentsByTopic(start, end, topicId, index, searchTerm);
            } else {
                documents = keywordFrequencyService.fetchDocumentsByTopic(start, end, topicId, index);
            }

            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger instead of printStackTrace in a real application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
//
            @GetMapping("/term-percentages")
            public ResponseEntity<Map<String, Float>> getTermPercentages(
                    @RequestParam String term1,
                    @RequestParam String term2,
                    @RequestParam String term3,
                    @RequestParam String term4,
                    @RequestParam String indexName,
                    @RequestParam String startDate,
                    @RequestParam String endDate) {

                try {
                    LocalDate start = LocalDate.parse(startDate);
                    LocalDate end = LocalDate.parse(endDate);
                    Map<String, Float> percentages = keywordFrequencyService.fetchTermPercentages(
                            term1, term2, term3, term4, indexName, start, end);
                    return ResponseEntity.ok(percentages);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().build();
                }
            }
//
            @GetMapping("/keyword-percentages")
            public Map<String, Object> getKeywordPercentages(
                    @RequestParam String keyword1,
                    @RequestParam String keyword2,
                    @RequestParam String indexName) {

                try {
                    return keywordFrequencyService.fetchKeywordPercentages(keyword1, keyword2, indexName)
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> (Object) entry.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Map.of("error", "An error occurred while fetching keyword percentages");
                }
            }



    //    @GetMapping("/forecast")
//    public ResponseEntity<Double> getForecast(
//            @RequestParam String keyword1,
//            @RequestParam String keyword2,
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//        try {
//            double forecast = KeywordFrequencyService.predictKeywordCount(keyword1, keyword2, LocalDate.parse(startDate), LocalDate.parse(endDate));
//            return ResponseEntity.ok(forecast);
//        } catch (IOException | InterruptedException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    private WordFrequencyBatch wordFrequencyBatch;

    @Autowired
    public void ElasticsearchController(WordFrequencyBatch wordFrequencyBatch) {
        this.wordFrequencyBatch = wordFrequencyBatch;
    }



    @Autowired
    private ScheduledTasks scheduledTasks;

    @GetMapping("/word-frequency")
    public ResponseEntity<String> getWordFrequency(@RequestParam String keywords,
                                                   @RequestParam String fromIndex,
                                                   @RequestParam String toIndex,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) {
        try {
            // Splitting the keywords string into a list
            List<String> keywordList = Arrays.asList(keywords.split(","));

            // Calling the scheduleKeywordCountTask method
            scheduledTasks.scheduleKeywordCountTask(keywordList, fromIndex, toIndex, startDate, endDate);

            return ResponseEntity.ok("Task scheduled successfully with provided parameters.");
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/topic-frequency")
//    public ResponseEntity<Map<LocalDate, Integer>> getTopicFrequency(@RequestParam String indexName,@RequestParam String toindex,@RequestParam String topic,
//                                                                     @RequestParam List<String> keywords, @RequestParam String startDate, @RequestParam String endDate,
//                                                                     @RequestParam int batchSize){
//        try {
//            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchTopicFrequency(indexName,toindex,topic, keywords, batchSize,startDate,endDate);
//            return ResponseEntity.ok(wordFrequency);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }


}
