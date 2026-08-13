
package com.elasticai.assistant.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.elasticai.assistant.document.KnowledgeDocument;
import com.elasticai.assistant.repository.KnowledgeRepository;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeRepository repository;
    private final EmbeddingService embeddingService;

    // ---------------------------------------
    // Constructor Injection
    // ---------------------------------------

    public KnowledgeServiceImpl(
            KnowledgeRepository repository,
            EmbeddingService embeddingService) {

        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    // ---------------------------------------
    // Save document
    // ---------------------------------------

    @Override
    public KnowledgeDocument save(KnowledgeDocument document) {

        if (document.getContent() == null ||
                document.getContent().isBlank()) {

            throw new IllegalArgumentException(
                    "Document content cannot be empty");
        }

        // Generate embedding from document content
        float[] embedding =
                embeddingService.createEmbedding(
                        document.getContent().trim()
                );

        document.setEmbedding(embedding);

        // Default title
        if (document.getTitle() == null ||
                document.getTitle().isBlank()) {

            document.setTitle("Untitled Document");
        }

        // Generate ID if missing
        if (document.getId() == null ||
                document.getId().isBlank()) {

            document.setId(
                    document.getTitle().replaceAll("\\s+", "_")
                    + "_" + System.currentTimeMillis()
            );
        }

        // Default source
        if (document.getSource() == null ||
                document.getSource().isBlank()) {

            document.setSource("manual");
        }

        // Default chunk number
        if (document.getChunkNumber() == null) {

            document.setChunkNumber(0);
        }

        return repository.save(document);
    }

    // ---------------------------------------
    // Get all documents
    // ---------------------------------------

    @Override
    public List<KnowledgeDocument> getAll() {

        List<KnowledgeDocument> documents =
                new ArrayList<>();

        for (KnowledgeDocument document :
                repository.findAll()) {

            documents.add(document);
        }

        return documents;
    }

    // ---------------------------------------
    // Keyword search
    // ---------------------------------------

    @Override
    public List<KnowledgeDocument> search(
            String keyword) {

        if (keyword == null ||
                keyword.isBlank()) {

            return List.of();
        }

        List<KnowledgeDocument> results =
                repository.searchByContent(
                        keyword.trim()
                );

        // Limit results sent to RAG
        if (results.size() > 5) {

            return results.subList(0, 5);
        }

        return results;
    }

    // ---------------------------------------
    // Semantic / Vector search
    // ---------------------------------------

    @Override
    public List<KnowledgeDocument> semanticSearch(
            String question) {

        if (question == null ||
                question.isBlank()) {

            return List.of();
        }

        // -----------------------------------
        // 1. Convert question into embedding
        // -----------------------------------

        float[] embedding =
                embeddingService.createEmbedding(
                        question.trim()
                );

        // -----------------------------------
        // 2. Search Elasticsearch using vector
        // -----------------------------------

        List<KnowledgeDocument> results =
                repository.semanticSearch(
                        embedding,
                        5
                );

        return results;
    }

    // ---------------------------------------
    // Delete all documents
    // ---------------------------------------

    @Override
    public void deleteAll() {

        repository.deleteAll();
    }
    
    @Override
    public List<KnowledgeDocument> hybridSearch(
            String question) {

        if (question == null ||
                question.isBlank()) {

            return List.of();
        }

        // ---------------------------------------
        // 1. Keyword search
        // ---------------------------------------

        List<KnowledgeDocument> keywordResults =
                search(question);

        // ---------------------------------------
        // 2. Semantic search
        // ---------------------------------------

        List<KnowledgeDocument> semanticResults =
                semanticSearch(question);

        // ---------------------------------------
        // 3. Combine results
        // ---------------------------------------

        List<KnowledgeDocument> combined =
                new ArrayList<>();

        combined.addAll(keywordResults);
        combined.addAll(semanticResults);

        // ---------------------------------------
        // 4. Remove duplicate documents
        // ---------------------------------------

        List<KnowledgeDocument> uniqueResults =
                new ArrayList<>();

        java.util.HashSet<String> seenIds =
                new java.util.HashSet<>();

        for (KnowledgeDocument document : combined) {

            if (document == null ||
                    document.getId() == null) {

                continue;
            }

            if (seenIds.add(document.getId())) {

                uniqueResults.add(document);
            }
        }

        // ---------------------------------------
        // 5. Limit context size
        // ---------------------------------------

        if (uniqueResults.size() > 8) {

            return uniqueResults.subList(0, 8);
        }

        return uniqueResults;
    }
   

}

