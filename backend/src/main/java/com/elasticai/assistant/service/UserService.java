package com.elasticai.assistant.service;

import com.elasticai.assistant.dto.LoginRequest;
import com.elasticai.assistant.dto.LoginResponse;
import com.elasticai.assistant.dto.RegisterRequest;
import com.elasticai.assistant.dto.RegisterResponse;

public interface UserService {
RegisterResponse registerUser(RegisterRequest request);
LoginResponse loginUser(LoginRequest request);
}
