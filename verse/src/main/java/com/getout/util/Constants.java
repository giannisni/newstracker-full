package com.getout.util;

public class Constants {
    public static final String elastic_host;

    public static final String ELASTICSEARCH_USERNAME = System.getenv("ELASTICSEARCH_USERNAME");
    public static final String ELASTICSEARCH_PASSWORD = System.getenv("ELASTICSEARCH_PASSWORD");
    static {


        // Fetching the ELASTICSEARCH_HOST environment variable
        String host = System.getenv("ELASTICSEARCH_HOST");
        if (host != null && !host.isEmpty()) {

            elastic_host = host;
        } else {
            // Default value if the environment variable is not set
            elastic_host = "localhost";
        }
    }
}
