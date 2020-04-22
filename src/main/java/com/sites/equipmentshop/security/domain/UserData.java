package com.sites.equipmentshop.security.domain;


import com.sites.equipmentshop.security.persistence.entities.UserEntity;

public class UserData {

	private String userId;
	private String userName;
	private String userStatus;
	
	public UserData(UserEntity entity) {
		super();
		this.userId = entity.getId();
		this.userName = entity.getUserName();
		this.userStatus = entity.getUserStatus().toString();
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}
