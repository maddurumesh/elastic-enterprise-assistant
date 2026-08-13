
package com.elasticai.assistant.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.elasticai.assistant.document.KnowledgeDocument;
import com.elasticai.assistant.dto.RagResponse;
import com.elasticai.assistant.dto.SourceResponse;

@Service
public class RagServiceImpl implements RagService {

    private final KnowledgeService knowledgeService;

    private final BedrockService bedrockService;

    public RagServiceImpl(
            KnowledgeService knowledgeService,
            BedrockService bedrockService) {

        this.knowledgeService = knowledgeService;
        this.bedrockService = bedrockService;
    }

    @Override
    public RagResponse ask(String question) {

        // ---------------------------------------
        // 1. Validate question
        // ---------------------------------------

        if (question == null ||
                question.isBlank()) {

            return new RagResponse(
                    "Please provide a valid question.",
                    List.of()
            );
        }

        question = question.trim();

        // ---------------------------------------
        // 2. Hybrid search
        // ---------------------------------------

        List<KnowledgeDocument> documents =
                knowledgeService.hybridSearch(question);

        // ---------------------------------------
        // 3. No information found
        // ---------------------------------------

        if (documents == null ||
                documents.isEmpty()) {

            return new RagResponse(
                    "I could not find this information " +
                    "in the knowledge base.",
                    List.of()
            );
        }

        // ---------------------------------------
        // 4. Build knowledge context
        // ---------------------------------------

        StringBuilder context =
                new StringBuilder();

        for (KnowledgeDocument document : documents) {

            if (document == null) {
                continue;
            }

            context.append("SOURCE DOCUMENT: ")
                   .append(document.getTitle())
                   .append("\n");

            context.append("CONTENT:\n")
                   .append(document.getContent())
                   .append("\n\n");

            context.append("--------------------------------")
                   .append("\n\n");
        }

        // ---------------------------------------
        // 5. Create grounded prompt
        // ---------------------------------------

        String prompt = """
                You are an enterprise knowledge assistant.

                Your task is to answer the user's question
                using ONLY the provided knowledge context.

                IMPORTANT RULES:

                1. Do not invent information.

                2. Do not use unrelated general knowledge.

                3. Do not make assumptions.

                4. If the answer cannot be found in the
                   knowledge context, respond exactly with:

                   "I could not find this information
                   in the knowledge base."

                5. Give a clear and concise answer.

                6. When possible, mention the source
                   document used for the answer.

                KNOWLEDGE CONTEXT:
                ==================

                %s

                USER QUESTION:
                ==============

                %s

                ANSWER:
                """.formatted(
                        context.toString(),
                        question
                );

        // ---------------------------------------
        // 6. Send prompt to AWS Bedrock
        // ---------------------------------------

        String answer =
                bedrockService.chat(prompt);

        // ---------------------------------------
        // 7. Convert KnowledgeDocument
        //    into SourceResponse
        // ---------------------------------------

        List<SourceResponse> sources =
                new ArrayList<>();

        for (KnowledgeDocument document : documents) {

            if (document == null) {
                continue;
            }

            SourceResponse source =
                    new SourceResponse();

            source.setId(
                    document.getId()
            );

            source.setTitle(
                    document.getTitle()
            );

            source.setContent(
                    document.getContent()
            );

            source.setSource(
                    document.getSource()
            );

            source.setChunkNumber(
                    document.getChunkNumber()
            );

            sources.add(source);
        }

        // ---------------------------------------
        // 8. Return answer + sources
        // ---------------------------------------

        return new RagResponse(
                answer,
                sources
        );
    }
}

