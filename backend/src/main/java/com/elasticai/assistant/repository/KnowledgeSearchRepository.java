
package com.elasticai.assistant.repository;

import java.util.List;

import com.elasticai.assistant.document.KnowledgeDocument;

public interface KnowledgeSearchRepository {

    List<KnowledgeDocument> semanticSearch(
            float[] embedding,
            int k
    );
}
