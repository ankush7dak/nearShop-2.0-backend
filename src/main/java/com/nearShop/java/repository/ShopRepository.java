package com.nearShop.java.repository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
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
=======
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
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
    public Long getShopId(Long user_id);

    public Optional<Shop> findById(Long id);

<<<<<<< HEAD
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
=======
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
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
