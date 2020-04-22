package com.sites.equipmentshop.security.persistence.entities;

import java.util.Arrays;
import java.util.List;

public enum UserStatuses {
	
	ACTIVE, NEW, DELETED, DISABLED;

	public static List<UserStatuses> getInActive (){
		return Arrays.asList(DELETED, DISABLED);
	}
}
