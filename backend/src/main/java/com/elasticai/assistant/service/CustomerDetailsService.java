package com.elasticai.assistant.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.elasticai.assistant.entity.User;
import com.elasticai.assistant.repository.UserRepository;

@Service
public class CustomerDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
		User user=userRepository.findByEmail(email)
				.orElseThrow(()->
				new UsernameNotFoundException("User NOt found"));
		return new org.springframework.security.core.userdetails.User(user.getEmail(), 
				user.getPassword(),Collections.emptyList()
				);
	}
}
