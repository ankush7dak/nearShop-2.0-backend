package com.nearShop.java.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.nearShop.java.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
                select count(*) from nearshop.products p where p.shop_id = ?1 and p.name = ?2
            """, nativeQuery = true)
    public Integer getProductCountForShop(Long shopId, String productName);

    @Query(value = """
                select count(*) from nearshop.products p where p.shop_id = ?1
            """, nativeQuery = true)
    public Integer getProductCount(Long shopId);

    public List<Product> findByShop_Id(Long shopId);

    @Query(value = """
            SELECT * FROM products p
            WHERE
            (?1 IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')))
            AND
            (?2 IS NULL OR p.shop_sub_category_id = ?2 OR p.sub_category_id = ?2) and p.shop_id = ?3
            """, countQuery = """
            SELECT COUNT(*) FROM products p
            WHERE
            (?1 IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')))
            AND
            (?2 IS NULL OR p.shop_sub_category_id = ?2 OR p.sub_category_id = ?2) and p.shop_id = ?3
            """, nativeQuery = true)
    public Page<Product> searchProducts(String search, Long category ,Long shopId,Pageable pageable);

}
