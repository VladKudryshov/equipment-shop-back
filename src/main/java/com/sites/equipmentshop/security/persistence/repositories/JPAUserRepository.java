package com.sites.equipmentshop.security.persistence.repositories;

import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAUserRepository extends JpaRepository<UserEntity, String> {
	
	UserEntity findUserByUserName(String userName);
	
	UserEntity findUserByEmailAndUserStatusNotIn(String email, List<UserStatuses> status);
	
	List<UserEntity> findByUserStatusNotInAndIdNot(Sort sorting, List<UserStatuses> status, String id);
	
	UserEntity findUserByEmail(String email);
	
}
