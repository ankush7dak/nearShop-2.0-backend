package com.nearShop.java.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.nearShop.java.entity.Product;


@Repository
public interface ProductRepository extends JpaRepository< Product ,Long> {
     @Query(value = """
                select count(*) from nearshop.products p where p.shop_id = ?1 and p.name = ?2
            """, nativeQuery = true)
    public Integer getProductCountForShop(Long shopId,String productName);
} 
