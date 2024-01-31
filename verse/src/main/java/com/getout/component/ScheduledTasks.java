package com.getout.component;

import com.getout.service.IndexMap;
import com.getout.service.WordFrequencyBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;


@Service
public class ScheduledTasks {

    @Autowired
    private WordFrequencyBatch wordFrequencyBatch;

    @Autowired
    private IndexMap indexMap;

    public ScheduledTasks(WordFrequencyBatch wordFrequencyBatch) {
        this.wordFrequencyBatch = wordFrequencyBatch;
    }
;

//    @Scheduled(cron = "0 05 18 * * *")
//    public void scheduleKeywordCountTask() {
////        logger.info("Starting scheduled task for keyword count...");
//        // Use indexName and toIndex here
//    }
    public void scheduleKeywordCountTask( List<String> keywords,String fromIndex,String toIndex, String startDate, String endDate ) {


        System.out.println("Index = " + fromIndex);
        System.out.println("Keywords = " + keywords);

        System.out.println("EndIndex = " + toIndex);
        System.out.println("startDate =  " + startDate);
        System.out.println("endDate =  " + endDate);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<KeywordResult>> futures = new ArrayList<>();

        for (String keyword : keywords) {
            final String currentKeyword = keyword;
            Callable<KeywordResult> task = () -> {
                try {
//                    System.out.println("Keyword " + keyword );
                    Map<LocalDate, Integer> resultMap = wordFrequencyBatch.searchKeywordFrequency(fromIndex, toIndex, currentKeyword, startDate, endDate);

                    return new KeywordResult(currentKeyword, resultMap);
                } catch (Exception e) {
//                    logger.error("Error processing keyword: " + currentKeyword, e);
                    return new KeywordResult(currentKeyword, Collections.emptyMap());
                }
            };
            futures.add(executorService.submit(task));
        }

        for (Future<KeywordResult> future : futures) {
            try {
                KeywordResult result = future.get();
                Map<LocalDate, Integer> resultMap = result.getFrequencyMap();
                if (!resultMap.isEmpty()) {


                    indexMap.indexSortedMap(fromIndex, new TreeMap<>(resultMap), result.getKeyword(), toIndex);
                }
//                logger.info("Result for keyword '" + result.getKeyword() + "': " + resultMap);
            } catch (InterruptedException | ExecutionException e) {
//                logger.error("Error retrieving keyword count result", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdown();
//        logger.info("Finished scheduled task for keyword count.");
    }
}

//    @Scheduled(cron = "0 53  21 * * *")
//    public void scheduleTopicCountTask() {
//        logger.info("Starting scheduled task for keyword count...");
//
//        // Define the topics and their associated keywords
//        Map<String, List<String>> topicKeywords = new HashMap<>();
//        topicKeywords.put("Μεταναστευτικό", Arrays.asList("μετανάστης", "διακινητής"));
//        topicKeywords.put("Οικονομία", Arrays.asList("ευρώ", "τράπεζα"));
//        // add more topics and their associated keywords...
//
//        // Calculate the time frame for the search
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//        LocalDateTime now = LocalDateTime.now();
//        String endDate = now.format(formatter);
//        String startDate = now.minusDays(30).format(formatter);
//
//        System.out.println("Start date: " + startDate);
//        System.out.println("End date: " + endDate);
//
//        // Initialize an ExecutorService with a fixed number of threads
//        int numberOfThreads = 4; // Adjust this value according to your system's capabilities
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//
//        // Prepare a list of futures
//        List<Future<Map<LocalDate, Integer>>> futures = new ArrayList<>();
//
//        // Search for keyword counts within the specified time frame using multiple threads
//        for (Map.Entry<String, List<String>> entry : topicKeywords.entrySet()) {
//            String topic = entry.getKey();
//            List<String> keywords = entry.getValue();
//
//            Callable<Map<LocalDate, Integer>> task = () -> {
//                try {
//                    return wordFrequencyBatch.searchTopicFrequency("norconex2","norconex2_counts", topic, keywords, 500, startDate, endDate);
//                } catch (Exception e) {
//                    logger.error("Error processing keywords for topic: " + topic, e);
//                    return Collections.emptyMap();
//                }
//            };
//            futures.add(executorService.submit(task));
//        }
//
//        // Wait for all tasks to complete and print the results
//        for (Future<Map<LocalDate, Integer>> future : futures) {
//            try {
//                Map<LocalDate, Integer> resultMap = future.get();
//                logger.info("Result: " + resultMap);
//            } catch (InterruptedException | ExecutionException e) {
//                logger.error("Error retrieving keyword count result", e);
//            }
//        }
//
//        // Shutdown the ExecutorService
//        executorService.shutdown();
//
//        logger.info("Finished scheduled task for keyword count.");
//    }



