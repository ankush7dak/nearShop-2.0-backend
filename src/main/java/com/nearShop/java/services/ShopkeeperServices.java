package com.nearShop.java.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.utilities.NearShopUtility;
@Service
public class ShopkeeperServices {
    @Autowired 
    NearShopUtility objNearShopUtility;
    @Autowired
    UserRepository objUserRepository;
    @Autowired
    CategoryRepository objCategoryRepository;

    public boolean isShopRegistered(Long userId) {
        // TODO Auto-generated method stub
         String status = objUserRepository.findByUser_id(userId);

        if(status.equals("PENDING")) return false;
        return true;
        
    }

    public List<String> getAllShopCategories() {
        // TODO Auto-generated method stub
        return objCategoryRepository.findAllCategories();
    }



}
