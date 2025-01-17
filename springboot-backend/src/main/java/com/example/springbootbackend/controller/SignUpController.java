package com.example.springbootbackend.controller;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootbackend.exception.ResourceNotFoundException;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.Ticket;
import com.example.springbootbackend.model.Event;
import com.example.springbootbackend.repository.UserRepository;
import com.example.springbootbackend.repository.TicketRepository;
import com.example.springbootbackend.repository.EventRepository;
/*
 * Front-end must construct a user object after user enters their information,
 * this user object is sent to back-end and is verified that a duplicate email
 * is not being used, then saving new user to SQL Database and returning
 * "Registration Successful"
 */

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:3000")
public class SignUpController {

	@Autowired
	private UserRepository userRepository;
	
	//Frontend constructs a user on signup page in this implementation
	@PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
		Optional<User> userOptional = userRepository.findById(user.getEmail());
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
    }
	
}