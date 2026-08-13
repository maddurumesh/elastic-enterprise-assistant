
package com.elasticai.assistant.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.elasticai.assistant.document.KnowledgeDocument;

public interface KnowledgeRepository
        extends ElasticsearchRepository<KnowledgeDocument, String>,
                KnowledgeSearchRepository {

    @Query("""
    {
      "bool": {
        "should": [
          {
            "match": {
              "content": {
                "query": "?0",
                "operator": "or"
              }
            }
          },
          {
            "match": {
              "title": {
                "query": "?0",
                "operator": "or"
              }
            }
          }
        ],
        "minimum_should_match": 1
      }
    }
    """)
    List<KnowledgeDocument> searchByContent(String keyword);
}

