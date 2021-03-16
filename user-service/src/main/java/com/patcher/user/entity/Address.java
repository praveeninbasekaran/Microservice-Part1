package com.patcher.user.entity;

import lombok.Data;

@Data
public class Address {
	
	private byte typeCode;
	private String addressLine1;
	private String addressLine2;
	private String State;
	private String Country;
	private int pin;
	
}
