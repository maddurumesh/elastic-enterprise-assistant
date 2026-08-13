package com.elasticai.assistant.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Service
public class BedrockService {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;

    public BedrockService(
            BedrockRuntimeClient bedrockRuntimeClient) {

        this.bedrockRuntimeClient = bedrockRuntimeClient;
        this.objectMapper = new ObjectMapper();
    }

    public String chat(String prompt) {

        try {

            // ---------------------------------------
            // 1. Build valid JSON using Jackson
            // ---------------------------------------

            ObjectNode root =
                    objectMapper.createObjectNode();

            ArrayNode messages =
                    root.putArray("messages");

            ObjectNode message =
                    messages.addObject();

            message.put("role", "user");

            ArrayNode content =
                    message.putArray("content");

            ObjectNode textBlock =
                    content.addObject();

            textBlock.put("text", prompt);

            // ---------------------------------------
            // 2. Add inference configuration
            // ---------------------------------------

            ObjectNode inferenceConfig =
                    root.putObject("inferenceConfig");

            inferenceConfig.put("maxTokens", 1000);
            inferenceConfig.put("temperature", 0.2);

            // ---------------------------------------
            // 3. Convert JSON to string
            // ---------------------------------------

            String requestBody =
                    objectMapper.writeValueAsString(root);

            System.out.println("===== BEDROCK REQUEST =====");
            System.out.println(requestBody);
            System.out.println("===========================");

            // ---------------------------------------
            // 4. Create request
            // ---------------------------------------

            InvokeModelRequest request =
                    InvokeModelRequest.builder()
                            .modelId(
                                    "amazon.nova-micro-v1:0"
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

            // ---------------------------------------
            // 5. Invoke Bedrock
            // ---------------------------------------

            InvokeModelResponse response =
                    bedrockRuntimeClient.invokeModel(
                            request
                    );

            String responseBody =
                    response.body().asUtf8String();

            System.out.println("===== BEDROCK RESPONSE =====");
            System.out.println(responseBody);
            System.out.println("============================");

            // ---------------------------------------
            // 6. Extract only generated text
            // ---------------------------------------

            JsonNode rootResponse =
                    objectMapper.readTree(
                            responseBody
                    );

            JsonNode textNode =
                    rootResponse
                            .path("output")
                            .path("message")
                            .path("content")
                            .get(0)
                            .path("text");

            if (!textNode.isMissingNode()) {
                return textNode.asText();
            }

            // Fallback
            return responseBody;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to call Amazon Bedrock",
                    e
            );
        }
    }
}