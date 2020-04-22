package com.sites.equipmentshop.security.endpoints.dto;


import com.sites.equipmentshop.security.persistence.entities.UserRoles;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class NewUserDTO {
	
	@NotNull(message = "User name cannot be null")
	private String userName;

	@Email(message = "Email validation error")
	private String email;
	
	private UserRoles userRole;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

}
