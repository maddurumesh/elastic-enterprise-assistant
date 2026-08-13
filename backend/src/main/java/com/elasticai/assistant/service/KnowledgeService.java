
package com.elasticai.assistant.service;

import java.util.List;

import com.elasticai.assistant.document.KnowledgeDocument;

public interface KnowledgeService {

    KnowledgeDocument save(
            KnowledgeDocument document
    );

    List<KnowledgeDocument> getAll();

    List<KnowledgeDocument> search(
            String keyword
    );

    List<KnowledgeDocument> semanticSearch(
            String question
    );

    List<KnowledgeDocument> hybridSearch(
            String question
    );

    void deleteAll();
}

