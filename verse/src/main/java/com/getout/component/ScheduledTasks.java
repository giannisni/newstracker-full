package com.getout.component;

import com.getout.service.IndexMap;
import com.getout.service.KeywordFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;


@Service
public class ScheduledTasks {

    @Autowired
    private KeywordFrequencyService wordFrequencyBatch;

    @Autowired
    private IndexMap indexMap;

    public ScheduledTasks(KeywordFrequencyService wordFrequencyBatch) {
        this.wordFrequencyBatch = wordFrequencyBatch;
    }
;
    private void setupPythonEnvironment() {
        try {
            ProcessBuilder setupEnvBuilder = new ProcessBuilder("/bin/bash", "virtualenv.sh");
            setupEnvBuilder.redirectErrorStream(true);
            Process setupProcess = setupEnvBuilder.start();

            // Read output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(setupProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = setupProcess.waitFor();
            System.out.println("Environment setup exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void runScript(String scriptPath, String startDate, String endDate, String domain,String index) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptPath, "--start_date", startDate, "--end_date", endDate, "--domain", domain,"--index", index);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Read output of the script
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Python Output: " + line);
                }

                int exitCode = process.waitFor();
                System.out.println("Script for domain " + domain + " exited with code " + exitCode);
            } catch (IOException | InterruptedException e) {
                System.out.println("Error running script for domain " + domain);
                e.printStackTrace();
            }
        }



    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.username}")
    private String esUsername;

    @Value("${elasticsearch.password}")
    private String esPassword;

    @Value("${elasticsearch.protocol}")
    private String esScheme;

    @Scheduled(cron = "0 00 20 * * *")
    public void runPythonScripts() {
        ExecutorService executor = Executors.newFixedThreadPool(5); // Adjust the number of threads based on your needs

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = LocalDate.now().format(formatter); // Gets today's date in the required format

        // Define different domains and dates
        String[][] parameters = {
                {today, today, "cnn.com", "cnn_articles_newone"},
                {today, today, "foxnews.com", "fox_articles_new"}
                // Add more as needed
        };

        String pythonScriptPath = "verse/src/main/java/com/getout/scripts/gdelt.py";
        for (String[] param : parameters) {
            String startDate = param[0];
            String endDate = param[1];
            String domain = param[2];
            String index = param[3];

            // Submit each script execution as a separate task to the executor
            executor.submit(() -> {
                try {
                    String command = String.format(
                            "python %s --start_date=%s --end_date=%s --domain=%s --index=%s --es_host=%s --es_port=%d --es_scheme=%s --es_username=%s --es_password=%s",
                            pythonScriptPath, startDate, endDate, domain, index, esHost, esPort, esScheme, esUsername, esPassword
                    );
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown(); // Shutdown the executor after submitting all tasks
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); // Optional: wait for all tasks to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



            //    @Scheduled(cron = "0 05 18 * * *")
        //    public void scheduleKeywordCountTask() {
        ////        logger.info("Starting scheduled task for keyword count...");
        //        // Use indexName and toIndex here
        //    }
        public void scheduleKeywordCountTask( List<String> keywords,String fromIndex,String toIndex, String startDate, String endDate ) {


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



