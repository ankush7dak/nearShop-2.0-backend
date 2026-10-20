package com.nearShop.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUser_Id(Long user_id);
    
  @Query(value = """
                select r.name from nearshop.users u,nearshop.user_roles ur, nearshop.roles r where u.id = ?1
                and ur.user_id = u.id and ur.role_id = r.id
            """, nativeQuery = true)
    List<String> getRoles(Long userId);
}

