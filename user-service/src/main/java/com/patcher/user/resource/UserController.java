package com.patcher.user.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patcher.user.domain.UserProfile;
import com.patcher.user.userinterface.UserInterface;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserInterface userInterface;
	
	
	@GetMapping("/test")
	public String testUserController() {
		return "User Controller";
	}
	
	
	@GetMapping("/all")
	public List<UserProfile> getAllUsers() {
		return userInterface.getAllUsers();
	}
	
	@PostMapping
	public UserProfile saveUserService(@PathVariable String firstName, @PathVariable String lastName) {
		return userInterface.saveUserService();
	}

	@PutMapping
	public UserProfile updateUserService() {
		return userInterface.updateUserService();
	}

	
}
