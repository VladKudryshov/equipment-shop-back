package com.sites.equipmentshop.security;

public enum Tokens {

	ACCESS_TOKEN("access", "Authorization"), REFRESH_TOKEN("refresh", "Refresh-token");
	
	private String type;
	private String header;
	
	Tokens(String type, String header) { 
		this.type = type;
		this.header = header;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getHeader() {
		return this.header;
	}	
	
}
