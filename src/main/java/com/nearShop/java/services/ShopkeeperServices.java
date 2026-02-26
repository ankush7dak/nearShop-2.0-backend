package com.nearShop.java.services;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nearShop.java.dto.ShopDTO;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.User;
import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.repository.ShopRepository;
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
    @Autowired
    GCSService objGcsService;
    @Autowired
    ShopRepository objShopRepository;

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

    public boolean registerShop(ShopDTO shopDTO, MultipartFile logo, Long userId) throws IOException {

    // Convert userId to Long
    try{
        Long id = userId;

    // Fetch user properly (not Optional)
    User user = objUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

    // Fetch category by name
    Category category = objCategoryRepository.findByName(shopDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

    // Create Shop object
    Shop shop = new Shop();
    shop.setOwner(user);
    shop.setShopName(shopDTO.getShopName());
    shop.setAddress(shopDTO.getAddress());
    shop.setDescription(shopDTO.getDescription());
    shop.setClosingTime(LocalTime.parse(shopDTO.getClosingTime()));
    shop.setOpeningTime(LocalTime.parse(shopDTO.getOpeningTime()));
    shop.setCategory(category);
    shop.setStatus("open");
    shop.setLatitude(Double.parseDouble(shopDTO.getLatitude()));
    shop.setLongitude(Double.parseDouble(shopDTO.getLongitude()));
    // Handle logo (optional)
    if (logo != null && !logo.isEmpty()) {
        shop.setLogoUrl(objGcsService.uploadFile(logo));
    }

    // Save to database
    objShopRepository.save(shop);
    }catch(Exception e){
        return false;
    }

    return true;   // now returning true after saving
}



}
