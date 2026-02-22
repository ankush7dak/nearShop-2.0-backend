package com.nearShop.java.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query(value = """
                SELECT c.name
                FROM nearshop.categories c
            """, nativeQuery = true)
   List<String> findAllCategories();
    
}
