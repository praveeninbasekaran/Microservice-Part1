package com.patcher.user.domain;

import lombok.Data;

@Data
public class UserProfile {
	
	private int number;
	private String firstName;
	private String lastName;
	private Organization organization;
	private Phone[] phones;
	private Email[] emails;
	private Address[] addresses;
	

	
}
