package com.nearShop.java.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nearShop.java.entity.Cart;

public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query(value = """
            select count(*) from nearshop.cart c where c.user_id =?2 and c.shop_id = ?1
            """, nativeQuery = true)
     public Integer isCartAvailable(Long shopId,Long userId);
    @Query(value = """
            select * from nearshop.cart c where c.user_id =?2 and c.shop_id = ?1
            """, nativeQuery = true)
     public Optional<Cart> getCartData(Long shopId, Long userId);
}
