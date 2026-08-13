package com.elasticai.assistant.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
  void upload(MultipartFile file)throws IOException;
}
