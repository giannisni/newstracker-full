package com.getout.util;

import java.time.LocalDate;
import java.util.Map;

public class CSVUtil {

    public static String convertMapToCsv(Map<LocalDate, Integer> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("date,count\n");
        map.forEach((k, v) -> sb.append(k.toString()).append(",").append(v).append("\n"));
        return sb.toString();
    }
}
