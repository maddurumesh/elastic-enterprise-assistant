
package com.elasticai.assistant.dto;

import java.util.List;

public class RagResponse {

    private String answer;

    private List<SourceResponse> sources;

    public RagResponse() {
    }

    public RagResponse(
            String answer,
            List<SourceResponse> sources) {

        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<SourceResponse> getSources() {
        return sources;
    }

    public void setSources(
            List<SourceResponse> sources) {

        this.sources = sources;
    }
}

