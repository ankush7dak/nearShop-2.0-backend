package com.nearShop.java.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.ShopSubcategory;

@Repository
public interface ShopSubcategoryRepository extends JpaRepository<ShopSubcategory,Long> {
    @Query(value = """
                select name from shop_subcategories where shop_id = ?1
            """, nativeQuery = true)
    List<String> findBy_Id(Long shopId);
    
}
