package com.elasticai.assistant.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elasticai.assistant.service.DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentUploadController {

    private final DocumentService documentService;

    public DocumentUploadController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // TEST ENDPOINT
    @GetMapping("/test")
    public String test() {
        return "DocumentUploadController is working";
    }

    // FILE UPLOAD
    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file)
            throws IOException {

        System.out.println("========== FILE UPLOAD ==========");
        System.out.println(
                "File received: " + file.getOriginalFilename());

        System.out.println(
                "File size: " + file.getSize());

        System.out.println(
                "Content type: " + file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File is empty");
        }

        documentService.upload(file);

        return ResponseEntity.ok(
                "Document uploaded successfully");
    }
}