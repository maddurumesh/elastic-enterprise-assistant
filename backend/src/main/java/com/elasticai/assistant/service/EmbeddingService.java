package com.elasticai.assistant.service;

public interface EmbeddingService {

    float[] createEmbedding(String text);
}