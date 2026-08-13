package com.elasticai.assistant.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import com.elasticai.assistant.document.KnowledgeDocument;

@Repository
public class KnowledgeSearchRepositoryImpl
        implements KnowledgeSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public KnowledgeSearchRepositoryImpl(
            ElasticsearchOperations elasticsearchOperations) {

        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public List<KnowledgeDocument> semanticSearch(
            float[] embedding,
            int k) {

        // ---------------------------------------
        // Validate embedding
        // ---------------------------------------

        if (embedding == null || embedding.length == 0) {
            return List.of();
        }

        // ---------------------------------------
        // Titan Embed Text V2 = 1024 dimensions
        // ---------------------------------------

        if (embedding.length != 1024) {

            throw new IllegalArgumentException(
                    "Invalid embedding dimension. Expected 1024 but received "
                            + embedding.length
            );
        }

        // ---------------------------------------
        // Validate top K
        // ---------------------------------------

        int topK = k <= 0
                ? 5
                : Math.min(k, 20);

        // ---------------------------------------
        // Convert float[] to List<Float>
        // ---------------------------------------

        List<Float> queryVector =
                convertToList(embedding);

        // ---------------------------------------
        // Build Elasticsearch kNN query
        // ---------------------------------------

        NativeQuery query =
                NativeQuery.builder()
                        .withQuery(
                                q -> q.knn(
                                        knn -> knn
                                                .field("embedding")
                                                .queryVector(queryVector)
                                                .k(topK)
                                                .numCandidates(
                                                        Math.max(
                                                                topK * 10,
                                                                50
                                                        )
                                                )
                                )
                        )
                        .build();

        // ---------------------------------------
        // Execute search
        // ---------------------------------------

        return elasticsearchOperations
                .search(
                        query,
                        KnowledgeDocument.class
                )
                .stream()
                .map(hit -> hit.getContent())
                .toList();
    }

    // ---------------------------------------
    // Convert float[] -> List<Float>
    // ---------------------------------------

    private List<Float> convertToList(
            float[] embedding) {

        List<Float> values = new ArrayList<>(
                embedding.length
        );

        for (float value : embedding) {
            values.add(value);
        }

        return values;
    }
}