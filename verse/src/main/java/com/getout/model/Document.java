package com.getout.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private String title;
    private String text;
    private String summary;
    private String document;
    private List<String> authors;
    private String topic;

    @JsonProperty("published_date")
    private String publishedDate;

    private String url;
    private String tone;
}
