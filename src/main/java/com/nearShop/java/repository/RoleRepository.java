package com.nearShop.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query(value = """
                SELECT rs.name
                FROM users u, user_roles ur, roles rs
                WHERE u.id = ur.user_id
                  AND ur.role_id = rs.id
                  AND u.mobile = ?1
            """, nativeQuery = true)
    List<String> findRoleNamesByMobile(String mobile);
}
