package com.nearShop.java.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long>{
    @Query(value = """
                select count(*) from nearshop.shops sp where sp.owner_id = ?1
            """, nativeQuery = true)
    public int findShopByOwnerId(Long owner_id);

    @Query(value = """
             select category_id from nearshop.shops where owner_id = ?1
            """, nativeQuery = true)
    public Long getShopCategoryId(Long user_id);
    @Query(value = """
             select category_id from nearshop.shops where id = ?1
            """, nativeQuery = true)
    public Long getShopCategoryIdByShopId(Long shopId);
     @Query(value = """
             select id from nearshop.shops where owner_id = ?1
            """, nativeQuery = true)
    public Long getShopId(Long user_id);

    public Optional<Shop> findById(Long id);

    @Query(value = """
    SELECT *,
    (6371 * acos(
        cos(radians(?4)) *
        cos(radians(s.latitude)) *
        cos(radians(s.longitude) - radians(?5)) +
        sin(radians(?4)) *
        sin(radians(s.latitude))
    )) AS distance
    FROM shops s
    WHERE 
        (?1 IS NULL OR LOWER(s.shop_name) LIKE LOWER(CONCAT('%', ?1, '%')))
        AND (?2 IS NULL OR s.category_id = ?2)
        AND (s.is_active = true)
    HAVING distance <= ?3
    ORDER BY distance
    """,

    countQuery = """
    SELECT COUNT(*) FROM (
        SELECT s.id,
        (6371 * acos(
            cos(radians(?4)) *
            cos(radians(s.latitude)) *
            cos(radians(s.longitude) - radians(?5)) +
            sin(radians(?4)) *
            sin(radians(s.latitude))
        )) AS distance
        FROM shops s
        WHERE 
            (?1 IS NULL OR LOWER(s.shop_name) LIKE LOWER(CONCAT('%', ?1, '%')))
            AND (?2 IS NULL OR s.category_id = ?2)
            AND (s.is_active = true)
    ) AS temp
    WHERE distance <= ?3
    """,

    nativeQuery = true)
    public Page<Shop> fetchShopData(String shopSearch, Long catId, Long shopDistanceRange, Double userLatitude, Double userLongitude , Pageable pageable);
}
