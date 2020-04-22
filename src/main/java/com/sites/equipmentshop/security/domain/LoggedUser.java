package com.sites.equipmentshop.security.domain;


import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserRoles;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;

public class LoggedUser {

	private String userId;
	private String userEmail;
	private String userName;
	private UserStatuses userStatus;
	private UserRoles userRole;
	
	public LoggedUser(UserEntity entity) {
		super();
		this.userId = entity.getId();
		this.userEmail = entity.getEmail();
		this.userName = entity.getUserName();
		this.userStatus = entity.getUserStatus();
		this.userRole = entity.getUserRole();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserStatuses getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatuses userStatus) {
		this.userStatus = userStatus;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

}
