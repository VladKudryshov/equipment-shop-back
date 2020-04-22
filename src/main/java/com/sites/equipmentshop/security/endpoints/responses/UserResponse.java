package com.sites.equipmentshop.security.endpoints.responses;


import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserRoles;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;

public class UserResponse {

	private String id;
	private String userName;
	private String email;
	private UserRoles userRole;
	private UserStatuses userStatus;

	public String getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public UserStatuses getUserStatus() {
		return userStatus;
	}

	public UserResponse (UserEntity entity){
		this.id = entity.getId();
		this.userName = entity.getUserName();
		this.email = entity.getEmail();
		this.userRole = entity.getUserRole();
		this.userStatus = entity.getUserStatus();
	}
}
