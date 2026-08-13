
package com.elasticai.assistant.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "knowledge")
public class KnowledgeDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Integer)
    private Integer chunkNumber;

    @Field(
        type = FieldType.Dense_Vector,
        dims = 1024,
        index = true,
        similarity = "cosine"
    )
    private float[] embedding;

    public KnowledgeDocument() {
    }

    public KnowledgeDocument(
            String id,
            String title,
            String content,
            String source,
            Integer chunkNumber,
            float[] embedding) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.chunkNumber = chunkNumber;
        this.embedding = embedding;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}

