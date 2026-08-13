package com.elasticai.assistant.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {

        return Map.of(
                "status", "UP",
                "application", "Elastic Enterprise Assistant",
                "database", "MySQL",
                "search", "Elasticsearch",
                "ai", "Amazon Bedrock",
                "rag", "Enabled"
        );
    }
}