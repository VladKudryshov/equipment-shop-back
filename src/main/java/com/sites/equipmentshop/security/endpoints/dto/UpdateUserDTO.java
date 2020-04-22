package com.sites.equipmentshop.security.endpoints.dto;


import com.sites.equipmentshop.security.persistence.entities.UserRoles;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;

import javax.validation.constraints.NotNull;

public class UpdateUserDTO {
	
	@NotNull(message = "User role cannot be null")
	private UserRoles userRole;
	
	@NotNull(message = "User status cannot be null")
	private UserStatuses userStatus;

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

	public UserStatuses getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatuses userStatus) {
		this.userStatus = userStatus;
	}
	
}
