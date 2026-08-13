
package com.elasticai.assistant.service;

import com.elasticai.assistant.dto.RagResponse;

public interface RagService {

    RagResponse ask(String question);
}

