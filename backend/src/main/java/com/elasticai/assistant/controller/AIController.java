package com.elasticai.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elasticai.assistant.dto.ChatRequest;
import com.elasticai.assistant.dto.ChatResponse;
import com.elasticai.assistant.service.BedrockService;

@RestController
@RequestMapping("/ai")
public class AIController {

	@Autowired
	private BedrockService bedrockService;
	
	@PostMapping("/chat")
	public ChatResponse chat(@RequestBody ChatRequest request) {
		String response=bedrockService.chat(request.getPrompt());
		return new ChatResponse(response);
	}
}
