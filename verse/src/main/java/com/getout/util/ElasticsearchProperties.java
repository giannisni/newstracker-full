package com.getout.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
//@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
}
