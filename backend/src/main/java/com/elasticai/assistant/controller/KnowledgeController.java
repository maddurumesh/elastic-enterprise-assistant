package com.elasticai.assistant.controller;

import java.util.List;
import java.io.IOException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elasticai.assistant.document.KnowledgeDocument;
import com.elasticai.assistant.service.KnowledgeService;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

	@Autowired
	private KnowledgeService knowledgeService;
	
	@PostMapping
	public KnowledgeDocument save(@RequestBody KnowledgeDocument document) {
		return knowledgeService.save(document);
	}
	@GetMapping
	public List<KnowledgeDocument> getAll(){
		return knowledgeService.getAll();
	}
	
	@GetMapping("/search")
	public List<KnowledgeDocument>search(@RequestParam String keyword){
		return knowledgeService.search(keyword);
	}
	@DeleteMapping
	public String deleteAll() {
	    knowledgeService.deleteAll();
	    return "All knowledge documents deleted";
	}
	@PostMapping("/upload")
	public KnowledgeDocument upload(@RequestPart("file") MultipartFile file) throws IOException {

	    String title = file.getOriginalFilename();
	    String content = new String(file.getBytes());

	    KnowledgeDocument document = new KnowledgeDocument();

	    // Create a unique ID from title + content
	    String documentId = title + "_" + content.hashCode();

	    document.setId(documentId);
	    document.setTitle(title);
	    document.setContent(content);

	    return knowledgeService.save(document);
	}
}
