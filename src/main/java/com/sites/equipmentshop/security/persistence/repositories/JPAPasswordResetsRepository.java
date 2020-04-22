package com.sites.equipmentshop.security.persistence.repositories;

import com.sites.equipmentshop.security.persistence.entities.PasswordResets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface JPAPasswordResetsRepository extends JpaRepository<PasswordResets, String> {

    @Query(value = "SELECT * FROM password_resets p WHERE id= :id AND p.created> :date", nativeQuery = true)
    PasswordResets findByIdAndCreatedAfter(@Param("id") String id, @Param("date") Date date);

    List<PasswordResets> deleteByCreatedBefore(Date date);

    List<PasswordResets> findByUserId(String userId);

}
