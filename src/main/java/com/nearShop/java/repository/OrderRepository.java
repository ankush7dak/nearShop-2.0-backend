
package com.nearShop.java.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    @Query(value = "select sum(ords.total_amount) from nearshop.orders ords where ords.shop_id = ?1 and ords.order_status = 'DELIVERED'" , nativeQuery = true)
    String getTotalRevenue(Long shopId);
    @Query(value = "select count(*) from nearshop.orders ords where ords.shop_id = ?1 and ords.order_status = 'PLACED'" , nativeQuery = true)
    String getNewOrdersCount(Long shopId);
   
}
