package com.nearShop.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.nearShop.java.entity.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {
    @Query(value = """
             select name from nearshop.subcategories where category_id = ?1
            """, nativeQuery = true)
    public List<String> getShopSubCategories(Long categoryId);

    @Query(value = """
             select count(*) from nearshop.subcategories where name = ?1
            """, nativeQuery = true)
    public Integer getSubCategoryCount(String shopSubcategoryName);
    
    // @Query(value = """
    //          select * from nearshop.subcategories where name = ?1
    //         """, nativeQuery = true)
    public Optional<SubCategory> findByName(String shopSubcategoryName);
    
}
