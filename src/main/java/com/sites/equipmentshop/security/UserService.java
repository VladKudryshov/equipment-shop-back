package com.sites.equipmentshop.security;

import com.sites.equipmentshop.security.domain.UserData;
import com.sites.equipmentshop.security.endpoints.dto.ModifyUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.NewUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.ResetPasswordDTO;
import com.sites.equipmentshop.security.endpoints.dto.UpdateUserDTO;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Service
public interface UserService {

    UserEntity createNewUser(NewUserDTO userDto);

    void deleteUserById(String id);

    UserEntity updateUser(String id, UpdateUserDTO userDto);

    Collection<UserEntity> getAllUsers();

    UserEntity getUserById(String id);

    UserEntity getUserByEmail(String email);

    UserEntity getActiveUserByEmail(String email);

    UserData modifyUser(HttpServletRequest request, HttpServletResponse response, ModifyUserDTO passwordDto, String userId);

    void passwordToReset(String id);

    void resetPassword(ResetPasswordDTO rpDto);

    static String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    static String md5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }
}
