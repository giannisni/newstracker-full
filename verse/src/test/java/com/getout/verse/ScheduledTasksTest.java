//package com.getout.verse;
//
//import com.getout.component.ScheduledTasks;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Arrays;
//import java.util.List;
//
//@SpringBootTest
//public class ScheduledTasksTest {
//
//    @Autowired
//    private ScheduledTasks scheduledTasks;
//
//    @Test
//    public void testScheduleKeywordCountTask() {
//        List<String> keywords = Arrays.asList("israeli", "israel", "palestine", "palestinian", "palestinians", "hamas", "israelis");
//        String fromIndex = "cnn_articles_newone";
//        String toIndex = "cnn_articles_newone_counts3";
//        String startDate = "2023-10-01";
//        String endDate = "2023-12-31";
//        scheduledTasks.scheduleKeywordCountTask(keywords, fromIndex, toIndex, startDate, endDate);
//    }
//
//
//}
