package com.sites.equipmentshop.security.exceptions;

public class PasswordResetNotExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public PasswordResetNotExistsException(String resetId) {
		super(String.format("Request for reset password not found: resetId=%s", resetId));
	}
	
}
