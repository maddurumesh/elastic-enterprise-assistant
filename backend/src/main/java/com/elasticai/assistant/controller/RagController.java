
package com.elasticai.assistant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elasticai.assistant.dto.AskRequest;
import com.elasticai.assistant.dto.RagResponse;
import com.elasticai.assistant.service.RagService;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(
            @RequestBody AskRequest request) {

        // Validate request
        if (request == null ||
                request.getQuestion() == null ||
                request.getQuestion().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(
                        new RagResponse(
                            "Question cannot be empty.",
                            List.of()
                        )
                    );
        }

        // Send question to RAG service
        RagResponse response =
                ragService.ask(
                    request.getQuestion()
                );

        return ResponseEntity.ok(response);
    }
}
