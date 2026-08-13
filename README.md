# Elastic Enterprise Assistant

A grounded enterprise knowledge assistant powered by Elasticsearch and Amazon Bedrock.

## Problem

Enterprise employees often spend time searching internal policies, procedures, security guidelines, IT documentation, training information, and other organizational knowledge.

Traditional keyword search can fail when users ask questions using different wording from the source documents.

## Solution

Elastic Enterprise Assistant allows users to upload enterprise documents and ask natural-language questions.

Uploaded documents are:

1. Split into smaller chunks
2. Converted into vector embeddings
3. Indexed in Elasticsearch

When a user asks a question:

1. The question is converted into an embedding
2. Elasticsearch performs semantic/vector retrieval
3. Keyword retrieval is also performed
4. Results are combined through hybrid retrieval
5. Relevant context is passed to Amazon Bedrock
6. The assistant generates a grounded answer
7. Source document and chunk information are returned

## Key Features

- JWT authentication
- User registration and login
- Enterprise document upload
- Automatic document chunking
- 1024-dimensional embeddings
- Elasticsearch vector search
- Elasticsearch keyword search
- Hybrid retrieval
- Retrieval-Augmented Generation (RAG)
- Amazon Bedrock response generation
- Source and chunk attribution
- Simple HTML/CSS/JavaScript frontend

## Technology Stack

### Frontend

- HTML
- CSS
- JavaScript

### Backend

- Java 17
- Spring Boot
- Spring Security
- JWT
- Maven

### Data

- MySQL
- Elasticsearch 8.x

### AI

- Amazon Bedrock
- Amazon Titan Embeddings V2
- Amazon Nova Micro

## Architecture

```text
                    USER
                     |
                     v
             HTML/CSS/JS UI
                     |
                     v
             Spring Boot API
             /             \
            v               v
         MySQL       Document Upload
                           |
                           v
                       Chunking
                           |
                           v
                Titan Embeddings V2
                           |
                           v
                    Elasticsearch
                    /           \
                   v             v
            Keyword Search   Vector Search
                   \             /
                    \           /
                     v         v
                    Hybrid Retrieval
                           |
                           v
                       RAG Context
                           |
                           v
                    Amazon Bedrock
                           |
                           v
                   Answer + Sources
