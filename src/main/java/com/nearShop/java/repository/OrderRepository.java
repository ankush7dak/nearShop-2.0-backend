
package com.nearShop.java.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Order;
import com.nearShop.java.entity.OtpVerification;
import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    Page<Order> findByShopIdOrderByOrderDateDesc(
    Long shopId,
    Pageable pageable
);

    Optional<Order> findByOrderIdAndShopId(
            Long orderId,
            Long shopId
    );
   
}
