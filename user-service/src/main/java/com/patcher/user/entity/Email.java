package com.patcher.user.entity;

import lombok.Data;

@Data
public class Email {
	
	private byte typeCode;
	private String email;
	private boolean isPrimary;
	
	

}
