package com.patcher.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Phone {
	
	private byte type;
	private String number;
	private int countryCode;
	

}
