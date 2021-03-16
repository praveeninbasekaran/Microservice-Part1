package com.patcher.user.userinterface;

import java.util.List;

import com.patcher.user.domain.UserProfile;

public interface UserInterface {

	public List<UserProfile> getAllUsers();

	public UserProfile saveUserService();

	public UserProfile updateUserService();
	
	
}
