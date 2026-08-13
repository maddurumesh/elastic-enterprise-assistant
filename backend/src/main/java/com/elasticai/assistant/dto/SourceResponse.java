
package com.elasticai.assistant.dto;

public class SourceResponse {

    private String id;

    private String title;

    private String source;

    private Integer chunkNumber;

    private String content;

    public SourceResponse() {
    }

    public SourceResponse(
            String id,
            String title,
            String source,
            Integer chunkNumber,
            String content) {

        this.id = id;
        this.title = title;
        this.source = source;
        this.chunkNumber = chunkNumber;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

