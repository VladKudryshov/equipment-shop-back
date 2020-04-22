package com.sites.equipmentshop.security.exceptions;

public class WrongPasswordException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public WrongPasswordException() {
		super("Wrong password");
	}
	
}
