package com.elasticai.assistant.dto;

public class ChatRequest {
 private String prompt;
 public ChatRequest() {
	 
 }
public String getPrompt() {
	return prompt;
}
public void setPrompt(String prompt) {
	this.prompt = prompt;
}
public ChatRequest(String prompt) {
	super();
	this.prompt = prompt;
}
 
}
