
package com.elasticai.assistant.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final int EMBEDDING_DIMENSIONS = 1024;

    private final BedrockRuntimeClient bedrockRuntimeClient;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public EmbeddingServiceImpl(
            BedrockRuntimeClient bedrockRuntimeClient) {

        this.bedrockRuntimeClient =
                bedrockRuntimeClient;
    }

    @Override
    public float[] createEmbedding(String text) {

        // ---------------------------------------
        // 1. Validate text
        // ---------------------------------------

        if (text == null ||
                text.isBlank()) {

            throw new IllegalArgumentException(
                    "Text cannot be empty"
            );
        }

        try {

            // ---------------------------------------
            // 2. Create Bedrock request
            // ---------------------------------------

            String requestBody = """
                    {
                      "inputText": "%s",
                      "dimensions": 1024,
                      "normalize": true
                    }
                    """.formatted(
                            escapeJson(text)
                    );

            // ---------------------------------------
            // 3. Invoke Titan Embeddings V2
            // ---------------------------------------

            InvokeModelRequest request =
                    InvokeModelRequest.builder()
                            .modelId(
                                    "amazon.titan-embed-text-v2:0"
                            )
                            .contentType(
                                    "application/json"
                            )
                            .accept(
                                    "application/json"
                            )
                            .body(
                                    SdkBytes.fromUtf8String(
                                            requestBody
                                    )
                            )
                            .build();

            InvokeModelResponse response =
                    bedrockRuntimeClient.invokeModel(
                            request
                    );

            // ---------------------------------------
            // 4. Read response
            // ---------------------------------------

            String responseBody =
                    response.body().asUtf8String();

            JsonNode root =
                    objectMapper.readTree(
                            responseBody
                    );

            JsonNode embeddingNode =
                    root.get("embedding");

            if (embeddingNode == null ||
                    !embeddingNode.isArray()) {

                throw new IllegalStateException(
                        "Embedding was not returned by Bedrock"
                );
            }

            // ---------------------------------------
            // 5. Verify dimensions
            // ---------------------------------------

            int dimension =
                    embeddingNode.size();

            if (dimension != EMBEDDING_DIMENSIONS) {

                throw new IllegalStateException(
                        "Invalid embedding dimension. " +
                        "Expected " +
                        EMBEDDING_DIMENSIONS +
                        " but received " +
                        dimension
                );
            }

            // ---------------------------------------
            // 6. Convert JSON → float[]
            // ---------------------------------------

            float[] embedding =
                    new float[dimension];

            for (int i = 0;
                    i < dimension;
                    i++) {

                embedding[i] =
                        (float) embeddingNode
                                .get(i)
                                .asDouble();
            }

            System.out.println(
                    "Embedding generated successfully. " +
                    "Dimensions: " +
                    embedding.length
            );

            return embedding;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create embedding",
                    e
            );
        }
    }

    // ---------------------------------------
    // Escape JSON characters
    // ---------------------------------------

    private String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
