package com.getout.model;

public class OpenAIData {
    private String openAI;
    private Integer count;

    public OpenAIData(String openAI, Integer count) {
        this.openAI = openAI;
        this.count = count;
    }

    public String getOpenAI() {
        return openAI;
    }

    public Integer getCount() {
        return count;
    }
}
