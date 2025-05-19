package com.yousofdevpro.busticketing.auth.repository;

import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT new com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse(" +
            "u.id, u.firstName, u.lastName, u.email, u.phone, u.role, " +
            "u.createdAt, u.updatedAt, u.createdBy, u.updatedBy) "+
            "FROM User u")
    List<UserDtoResponse>findAllUsers();
    
    @Query("SELECT new com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse(" +
            "u.id, u.firstName, u.lastName, u.email, u.phone, u.role, " +
            "u.createdAt, u.updatedAt, u.createdBy, u.updatedBy) " +
            "FROM User u " +
            "WHERE u.id = :id")
    Optional<UserDtoResponse> findUserById(@Param("id") Long id);
}
