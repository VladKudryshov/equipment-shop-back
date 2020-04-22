package com.sites.equipmentshop.security.endpoints.dto;

import javax.validation.constraints.Pattern;

public class ModifyUserDTO {

	private String userName;

	@Pattern(regexp = UserDTO.USER_PASSWORD_VALIDATION, message = "Password validation error")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
