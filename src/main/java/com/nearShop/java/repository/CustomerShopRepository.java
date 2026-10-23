package com.nearShop.java.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nearShop.java.dto.ResponseDTO.CustomerDTO;
import com.nearShop.java.entity.CustomerShop;

@Repository
public interface CustomerShopRepository
        extends JpaRepository<CustomerShop, Long> {

    @Query("""
        SELECT new com.nearShop.java.dto.ResponseDTO.CustomerDTO(
            u.id,
            u.name,
            u.mobile,
            u.email,
            cs.createdAt
        )
        FROM CustomerShop cs
        JOIN cs.customer u
        WHERE cs.shop.id = :shopId
        ORDER BY cs.createdAt DESC
    """)
    List<CustomerDTO> findCustomersByShopId(
        @Param("shopId") Long shopId
    );

    boolean existsByCustomer_IdAndShop_Id(
            Long customerId,
            Long shopId
    );
}
