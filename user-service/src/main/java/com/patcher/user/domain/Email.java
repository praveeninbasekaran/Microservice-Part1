package com.patcher.user.domain;

import lombok.Data;

@Data
public class Email {
	
	private byte typeCode;
	private String email;
	private boolean isPrimary;
	
	

}
