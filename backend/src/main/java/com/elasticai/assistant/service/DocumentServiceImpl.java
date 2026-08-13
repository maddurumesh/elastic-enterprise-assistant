package com.elasticai.assistant.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elasticai.assistant.document.KnowledgeDocument;
import com.elasticai.assistant.repository.KnowledgeRepository;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final KnowledgeRepository repository;
    private final EmbeddingService embeddingService;

    // ---------------------------------------
    // Chunk configuration
    // ---------------------------------------

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    // ---------------------------------------
    // Constructor
    // ---------------------------------------

    public DocumentServiceImpl(
            KnowledgeRepository repository,
            EmbeddingService embeddingService) {

        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    // ---------------------------------------
    // Upload document
    // ---------------------------------------

    @Override
    public void upload(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String content = new String(
                file.getBytes(),
                StandardCharsets.UTF_8
        );

        if (content.isBlank()) {
            throw new IllegalArgumentException(
                    "File contains no readable text"
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            fileName = "unknown-document";
        }

        List<String> chunks = createChunks(content);

        System.out.println("==================================");
        System.out.println("File: " + fileName);
        System.out.println("Total chunks: " + chunks.size());
        System.out.println("==================================");

        for (int i = 0; i < chunks.size(); i++) {

            String chunk = chunks.get(i);

            System.out.println(
                    "Processing chunk " +
                    (i + 1) +
                    " / " +
                    chunks.size()
            );

            float[] embedding =
                    embeddingService.createEmbedding(chunk);

            KnowledgeDocument document =
                    new KnowledgeDocument();

            document.setId(UUID.randomUUID().toString());

            document.setTitle(
                    fileName + " - Chunk " + (i + 1)
            );

            document.setSource(fileName);

            document.setChunkNumber(i + 1);

            document.setContent(chunk);

            document.setEmbedding(embedding);

            repository.save(document);
        }

        System.out.println(
                "Document upload completed successfully."
        );
    }

    // ---------------------------------------
    // Improved chunking
    // ---------------------------------------

    private List<String> createChunks(String content) {

        List<String> chunks = new ArrayList<>();

        content = content
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();

        if (content.isBlank()) {
            return chunks;
        }

        /*
         * First split by paragraphs.
         */
        String[] paragraphs =
                content.split("\\n\\s*\\n");

        StringBuilder current =
                new StringBuilder();

        for (String paragraph : paragraphs) {

            paragraph = paragraph.trim();

            if (paragraph.isBlank()) {
                continue;
            }

            /*
             * If a single paragraph itself is larger
             * than CHUNK_SIZE, split it safely by words.
             */
            if (paragraph.length() > CHUNK_SIZE) {

                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                }

                chunks.addAll(
                        splitLargeParagraph(paragraph)
                );

                continue;
            }

            int requiredLength =
                    current.length() == 0
                            ? paragraph.length()
                            : current.length()
                                    + 2
                                    + paragraph.length();

            /*
             * Current chunk can accept paragraph.
             */
            if (requiredLength <= CHUNK_SIZE) {

                if (current.length() > 0) {
                    current.append("\n\n");
                }

                current.append(paragraph);

            } else {

                /*
                 * Save current chunk.
                 */
                if (current.length() > 0) {

                    String completed =
                            current.toString().trim();

                    chunks.add(completed);

                    /*
                     * Create overlap using complete words.
                     */
                    String overlap =
                            createWordOverlap(
                                    completed,
                                    CHUNK_OVERLAP
                            );

                    current.setLength(0);

                    if (!overlap.isBlank()) {
                        current.append(overlap)
                               .append("\n\n");
                    }
                }

                /*
                 * Add current paragraph.
                 */
                current.append(paragraph);
            }
        }

        /*
         * Add final chunk.
         */
        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }

    // ---------------------------------------
    // Split large paragraph by words
    // ---------------------------------------

    private List<String> splitLargeParagraph(
            String paragraph) {

        List<String> chunks = new ArrayList<>();

        String[] words =
                paragraph.split("\\s+");

        StringBuilder current =
                new StringBuilder();

        for (String word : words) {

            int requiredLength =
                    current.length() == 0
                            ? word.length()
                            : current.length()
                                    + 1
                                    + word.length();

            if (requiredLength <= CHUNK_SIZE) {

                if (current.length() > 0) {
                    current.append(" ");
                }

                current.append(word);

            } else {

                if (current.length() > 0) {

                    String completed =
                            current.toString().trim();

                    chunks.add(completed);

                    String overlap =
                            createWordOverlap(
                                    completed,
                                    CHUNK_OVERLAP
                            );

                    current.setLength(0);

                    if (!overlap.isBlank()) {
                        current.append(overlap)
                               .append(" ");
                    }
                }

                current.append(word);
            }
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }

    // ---------------------------------------
    // Create overlap without breaking words
    // ---------------------------------------

    private String createWordOverlap(
            String text,
            int overlapSize) {

        if (text == null || text.isBlank()) {
            return "";
        }

        if (text.length() <= overlapSize) {
            return text;
        }

        int start =
                text.length() - overlapSize;

        /*
         * Move forward until we reach
         * the beginning of a complete word.
         */
        while (start < text.length() &&
                !Character.isWhitespace(text.charAt(start))) {

            start++;
        }

        if (start >= text.length()) {
            return "";
        }

        return text.substring(start).trim();
    }
}