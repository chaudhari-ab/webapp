package com.product.crud.repo;

import com.product.crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CrudRepo extends JpaRepository<User, UUID>{

    @Modifying
    @Query("update User u set u.first_name = ?1, u.last_name = ?2, u.password = ?3, account_updated = ?4 where u.id = ?5")
    public void setUserInfoById(String first_name, String last_name, String password , LocalDateTime account_updated, UUID id);

    User findByUsername(String username);
    @Query("SELECT count(username) FROM User WHERE username=:username")
    int isEmailPresent(@Param("username") String username);
}
