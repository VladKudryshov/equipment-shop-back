package com.sites.equipmentshop.security.exceptions;

public class UserNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UserNotExistsException(String userName) {
		super(String.format("User not exists: user_name=%s", userName));
	}

}
