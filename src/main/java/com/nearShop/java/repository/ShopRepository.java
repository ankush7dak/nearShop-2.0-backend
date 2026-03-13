package com.nearShop.java.repository;

import java.util.Optional;

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
             select id from nearshop.shops where owner_id = ?1
            """, nativeQuery = true)
    public Long getShopId(Long user_id);

    public Optional<Shop> findById(Long id);
}
