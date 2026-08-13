package com.elasticai.assistant.controller;

import org.springframework.web.bind.annotation.*;

import com.elasticai.assistant.dto.AskRequest;
import com.elasticai.assistant.dto.RagResponse;
import com.elasticai.assistant.service.RagService;

@RestController
@RequestMapping("/ask")
public class AskController {

    private final RagService ragService;

    public AskController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public RagResponse ask(@RequestBody AskRequest request) {

        return ragService.ask(request.getQuestion());
    }
}