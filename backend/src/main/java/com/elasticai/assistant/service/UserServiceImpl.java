package com.elasticai.assistant.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elasticai.assistant.dto.LoginRequest;
import com.elasticai.assistant.dto.LoginResponse;
import com.elasticai.assistant.dto.RegisterRequest;
import com.elasticai.assistant.dto.RegisterResponse;
import com.elasticai.assistant.entity.User;
import com.elasticai.assistant.repository.UserRepository;
import com.elasticai.assistant.util.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public RegisterResponse registerUser(RegisterRequest request) {
		if(userRepository.existsByEmail(request.getEmail())) {
			return new RegisterResponse("Emaill already exists");
		}
		
		User user=new User();
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		user.setRole("USER");
		userRepository.save(user);
		return new RegisterResponse("User Registered Successfully");
	}

	
	@Override
	public LoginResponse loginUser(LoginRequest request) {
		Optional<User> optionalUser=userRepository.findByEmail(request.getEmail());
		if(optionalUser.isEmpty()) {
			return new LoginResponse(null,"Invalid Email");
		}
		User user=optionalUser.get();
		if(!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
			return new LoginResponse(null,"Invalid Password");
		}
		String token =jwtUtil.generateToken(user.getEmail());
		return new LoginResponse(token,"Login Successful");
	}
	
}
