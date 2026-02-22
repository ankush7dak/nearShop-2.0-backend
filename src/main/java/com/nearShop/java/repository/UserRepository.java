package com.nearShop.java.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);

    @Query(value = """
                select ur.status from nearshop.users ur where ur.id = ?1
            """, nativeQuery = true)
    String findByUser_id(Long user_id);
}
