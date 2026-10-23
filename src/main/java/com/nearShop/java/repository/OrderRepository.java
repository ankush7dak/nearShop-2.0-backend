
package com.nearShop.java.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Order;
import com.nearShop.java.entity.OtpVerification;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByShopIdOrderByOrderDateDesc
                (
            Long shopId
    );

    Optional<Order> findByOrderIdAndShopId(
            Long orderId,
            Long shopId
    );
   
}
