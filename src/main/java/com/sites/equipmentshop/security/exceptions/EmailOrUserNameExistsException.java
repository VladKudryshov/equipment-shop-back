package com.sites.equipmentshop.security.exceptions;

public class EmailOrUserNameExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailOrUserNameExistsException(String userName, String email) {
		super(String.format("UserName or Email exists: user_name=%s, email=%s", userName, email));
	}

}
