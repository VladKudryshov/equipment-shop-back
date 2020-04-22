package com.sites.equipmentshop.security.endpoints.dto;


import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;

import java.util.Date;

public class UserDTO extends NewUserDTO {

    public static final String USER_PASSWORD_VALIDATION = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?!.*\\s).{5,15})";
    private String id;
    private UserStatuses userStatus;
    private Date lastLogin;

    public UserDTO(UserEntity entity) {
        this.id = entity.getId();
        setUserName(entity.getUserName());
        setEmail(entity.getEmail());
        setUserRole(entity.getUserRole());
        this.userStatus = entity.getUserStatus();
        this.lastLogin = entity.getLastLogin();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserStatuses getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatuses userStatus) {
        this.userStatus = userStatus;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

}
