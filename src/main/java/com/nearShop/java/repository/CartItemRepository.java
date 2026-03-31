package com.nearShop.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nearShop.java.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
        @Query(value = """
            select * from nearshop.cart_items ci where ci.product_id =?1 and ci.cart_id = ?2
            """, nativeQuery = true)
    Optional<CartItem> getCartItem(Long productId, Long cartId);
    @Query(value = """
            select * from nearshop.cart_items ci where ci.cart_id = ?1
            """, nativeQuery = true)
    List<CartItem> getAllCartData(Long cartId);
    
}
