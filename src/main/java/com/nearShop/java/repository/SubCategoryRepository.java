package com.nearShop.java.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.ShopSubcategory;
import com.nearShop.java.entity.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {
    @Query(value = """
             select name from nearshop.subcategories where category_id = ?1
            """, nativeQuery = true)
    List<String> getShopSubCategories(Long categoryId);
    
}
