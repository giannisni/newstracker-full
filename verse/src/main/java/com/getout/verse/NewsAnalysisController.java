package com.getout.verse;

import com.getout.component.ScheduledTasks;
import com.getout.model.DocumentData;
import com.getout.model.OpenAIData;
import com.getout.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NewsAnalysisController {

    private final KeywordService keywordService;
    private final OpenAIService openAIService;
    private final SentimentService sentimentService;
    private final DocumentService documentService;
    private final KeywordFrequencyService keywordFrequencyService;
    private final ScheduledTasks scheduledTasks;

    @GetMapping("/wordcloud")
    public ResponseEntity<List<Map<String, Object>>> getWordCloudData(@RequestParam String indexName) {
        try {
            List<Map<String, Object>> wordCloudData = keywordService.getWordCloudData(indexName);
            return ResponseEntity.ok(wordCloudData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/openai-data")
    public ResponseEntity<?> getOpenAIData(@RequestParam String indexName) {
        try {
            List<OpenAIData> openAIDataList = openAIService.fetchOpenAIData(indexName);
            return ResponseEntity.ok(openAIDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

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
            Map<String, List<String>> highlights = documentService.fetchHighlights(term1, term2, indexName, startLocalDate, endLocalDate);
            return ResponseEntity.ok(highlights);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<LocalDate, Integer>> getKeywordCounts(
            @RequestParam String keyword,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String index) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            Map<LocalDate, Integer> keywordCounts = keywordService.getKeywordCounts(index, keyword, start, end);
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

        Map<LocalDate, Integer> keywordCounts = keywordService.getKeywordCounts(index, keyword, start, end);
        return keywordService.getTopKeywordCounts(keywordCounts);
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
        return sentimentService.calculateAverageSentiment(index, sentiment_index, term, start, end);
    }

    @GetMapping("/documents-from-top-count-dates")
    public Map<LocalDate, List<DocumentData>> getDocumentsFromTopCountDates(
            @RequestParam String index,
            @RequestParam String keyword,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int topicId) throws IOException {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Map<LocalDate, Integer> keywordCounts = keywordService.getKeywordCounts(index, keyword, start, end);
        Map<LocalDate, Integer> topKeywordCounts = keywordService.getTopKeywordCounts(keywordCounts);

        Map<LocalDate, List<DocumentData>> documentsFromTopDates = new LinkedHashMap<>();

        for (LocalDate date : topKeywordCounts.keySet()) {
            List<DocumentData> documents = documentService.fetchDocumentsByTopic(date, date, topicId, index);
            if (documents.size() > 2) {
                documents = documents.subList(0, 2);
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
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<DocumentData> documents;

            if (searchTerm != null && !searchTerm.isEmpty()) {
                documents = documentService.fetchDocumentsByTopic(start, end, topicId, index, searchTerm);
            } else {
                documents = documentService.fetchDocumentsByTopic(start, end, topicId, index);
            }

            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            Map<String, Float> percentages = documentService.fetchTermPercentages(
                    term1, term2, term3, term4, indexName, start, end);
            return ResponseEntity.ok(percentages);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/keyword-percentages")
    public Map<String, Object> getKeywordPercentages(
            @RequestParam String keyword1,
            @RequestParam String keyword2,
            @RequestParam String indexName) {

        try {
            return keywordService.fetchKeywordPercentages(keyword1, keyword2, indexName)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> (Object) entry.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("error", "An error occurred while fetching keyword percentages");
        }
    }

    @GetMapping("/word-frequency")
    public ResponseEntity<String> getWordFrequency(@RequestParam String keywords,
                                                   @RequestParam String fromIndex,
                                                   @RequestParam String toIndex,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) {
        try {
            List<String> keywordList = Arrays.asList(keywords.split(","));
            scheduledTasks.scheduleKeywordCountTask(keywordList, fromIndex, toIndex, startDate, endDate);
            return ResponseEntity.ok("Task scheduled successfully with provided parameters.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
