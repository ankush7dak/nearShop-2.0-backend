package com.nearShop.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Order;
import com.nearShop.java.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM nearshop.shops sp
        WHERE sp.owner_id = ?1
        """, nativeQuery = true)
    public int findShopByOwnerId(Long owner_id);

    @Query(value = """
        SELECT category_id
        FROM nearshop.shops
        WHERE owner_id = ?1
        """, nativeQuery = true)
    public Long getShopCategoryId(Long user_id);

    @Query(value = """
        SELECT category_id
        FROM nearshop.shops
        WHERE id = ?1
        """, nativeQuery = true)
    public Long getShopCategoryIdByShopId(Long shopId);

    @Query(value = """
        SELECT id
        FROM nearshop.shops
        WHERE owner_id = ?1
        """, nativeQuery = true)
    public Long getShopId(Long user_id);

    public Optional<Shop> findById(Long id);

    @Query(
        value = """
            SELECT s.*,
                (6371 * ACOS(
                    COS(RADIANS(?4)) *
                    COS(RADIANS(s.latitude)) *
                    COS(RADIANS(s.longitude) - RADIANS(?5)) +
                    SIN(RADIANS(?4)) *
                    SIN(RADIANS(s.latitude))
                )) AS distance
            FROM nearshop.shops s
            WHERE
                (?1 IS NULL OR
                    LOWER(s.shop_name) LIKE
                    LOWER(CONCAT('%', ?1, '%')))
                AND (?2 IS NULL OR s.category_id = ?2)
                AND EXISTS (
                    SELECT 1
                    FROM nearshop.customer_shop cs
                    WHERE cs.shop_id = s.id
                      AND cs.customer_id = ?6
                )
            HAVING distance <= ?3
            ORDER BY distance
            """,
        countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT s.id,
                    (6371 * ACOS(
                        COS(RADIANS(?4)) *
                        COS(RADIANS(s.latitude)) *
                        COS(RADIANS(s.longitude) - RADIANS(?5)) +
                        SIN(RADIANS(?4)) *
                        SIN(RADIANS(s.latitude))
                    )) AS distance
                FROM nearshop.shops s
                WHERE
                    (?1 IS NULL OR
                        LOWER(s.shop_name) LIKE
                        LOWER(CONCAT('%', ?1, '%')))
                    AND (?2 IS NULL OR s.category_id = ?2)
                    AND EXISTS (
                        SELECT 1
                        FROM nearshop.customer_shop cs
                        WHERE cs.shop_id = s.id
                          AND cs.customer_id = ?6
                    )
            ) AS temp
            WHERE distance <= ?3
            """,
        nativeQuery = true
    )
    public Page<Shop> fetchShopData(
        String shopSearch,
        Long catId,
        Long shopDistanceRange,
        Double userLatitude,
        Double userLongitude,
        Pageable pageable,
        Long customerId
    );

    List<Shop> findByOwner_Id(Long userId);


}