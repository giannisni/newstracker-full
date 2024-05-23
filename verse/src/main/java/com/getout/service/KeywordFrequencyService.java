package com.getout.service;

import com.getout.model.DocumentData;
import com.getout.model.OpenAIData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class KeywordFrequencyService {

    private final KeywordService keywordService;
    private final OpenAIService openAIService;
    private final SentimentService sentimentService;
    private final DocumentService documentService;

    @Autowired
    public KeywordFrequencyService(KeywordService keywordService, OpenAIService openAIService, SentimentService sentimentService, DocumentService documentService) {
        this.keywordService = keywordService;
        this.openAIService = openAIService;
        this.sentimentService = sentimentService;
        this.documentService = documentService;
    }

    public List<Map<String, Object>> getWordCloudData(String indexName) throws IOException {
        return keywordService.getWordCloudData(indexName);
    }

    public List<OpenAIData> fetchOpenAIData(String indexName) throws IOException {
        return openAIService.fetchOpenAIData(indexName);
    }

    public double calculateAverageSentiment(String sentimentIndex, String index, String term, LocalDate startDate, LocalDate endDate) throws IOException {
        return sentimentService.calculateAverageSentiment(sentimentIndex, index, term, startDate, endDate);
    }

    public Map<LocalDate, Integer> getKeywordCounts(String index, String keyword, LocalDate startDate, LocalDate endDate) throws IOException {
        return keywordService.getKeywordCounts(index, keyword, startDate, endDate);
    }

    public static Map<LocalDate, Integer> getTopKeywordCounts(Map<LocalDate, Integer> keywordCounts) {
        return KeywordService.getTopKeywordCounts(keywordCounts);
    }

    public Map<String, Float> fetchKeywordPercentages(String keyword1, String keyword2, String indexName) throws IOException {
        return keywordService.fetchKeywordPercentages(keyword1, keyword2, indexName);
    }

    public List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index, String searchTerm) throws IOException {
        return documentService.fetchDocumentsByTopic(startDate, endDate, topicId, index, searchTerm);
    }

    public List<DocumentData> fetchDocumentsByTopic(LocalDate startDate, LocalDate endDate, int topicId, String index) throws IOException {
        return documentService.fetchDocumentsByTopic(startDate, endDate, topicId, index);
    }

    public Map<String, List<String>> fetchHighlights(String term1, String term2, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        return documentService.fetchHighlights(term1, term2, indexName, startDate, endDate);
    }

    public Map<String, Float> fetchTermPercentages(String term1, String term2, String term3, String term4, String indexName, LocalDate startDate, LocalDate endDate) throws IOException {
        return documentService.fetchTermPercentages(term1, term2, term3, term4, indexName, startDate, endDate);
    }
}
