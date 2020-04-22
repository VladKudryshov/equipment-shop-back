package com.sites.equipmentshop.security.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class PasswordResets {

	@Id
	private String id;
	
	private String userId;
	
	private Date created;
	
	public PasswordResets() {}
	
	public PasswordResets(String userId) {
		super();
		this.id = UUID.randomUUID().toString();
		this.userId = userId;	
		this.created = new Date();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}	
	
}
