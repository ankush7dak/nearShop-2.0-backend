package com.nearShop.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nearShop.java.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long>{
    
}
