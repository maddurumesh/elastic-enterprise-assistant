package com.elasticai.assistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elasticai.assistant.dto.ChatRequest;
import com.elasticai.assistant.dto.RagResponse;
import com.elasticai.assistant.service.RagService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(
            @RequestBody ChatRequest request) {

        RagResponse response =
                ragService.ask(request.getPrompt());

        return ResponseEntity.ok(response);
    }
}