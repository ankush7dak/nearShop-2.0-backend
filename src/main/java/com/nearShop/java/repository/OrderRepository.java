
package com.nearShop.java.repository;

<<<<<<< HEAD
import java.util.Collection;
import java.util.List;
=======
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Order;
import com.nearShop.java.entity.OtpVerification;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
<<<<<<< HEAD

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByShopIdOrderByOrderDateDesc
                (
            Long shopId
    );

    Optional<Order> findByOrderIdAndShopId(
            Long orderId,
            Long shopId
    );
=======
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
   
}
