# Elastic Enterprise Assistant Architecture

USER
  |
  v
HTML / CSS / JavaScript
  |
  v
Spring Boot Backend
  |--------------------> MySQL
  |
  v
Document Upload
  |
  v
Document Chunking
  |
  v
Amazon Titan Embeddings V2
  |
  v
Elasticsearch
  |                 |
  v                 v
Keyword Search   Vector Search
  |                 |
  +--------+--------+
           |
           v
    Hybrid Retrieval
           |
           v
       RAG Service
           |
           v
Amazon Bedrock / Nova Micro
           |
           v
    Grounded Answer
       + Sources

JWT Authentication ? Spring Security
