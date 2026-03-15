package com.nearShop.java.services;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.dto.AddProductDTO;
import com.nearShop.java.dto.ShopDTO;
import com.nearShop.java.dto.ShopSubCategoryDTO;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Product;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.ShopSubcategory;
import com.nearShop.java.entity.SubCategory;
import com.nearShop.java.entity.User;
import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.repository.ProductRepository;
import com.nearShop.java.repository.ShopRepository;
import com.nearShop.java.repository.ShopSubcategoryRepository;
import com.nearShop.java.repository.SubCategoryRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.utilities.NearShopUtility;
import java.util.Optional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    SubCategoryRepository objSubCategoryRepository;
    @Autowired
    ShopSubcategoryRepository objShopSubcategoryRepository;
    @Autowired
    ProductRepository objpProductRepository;


    public boolean isShopRegistered(Long userId) {
        // TODO Auto-generated method stub
        //  String status = objUserRepository.findByUser_id(userId);
        int isShopEntryAvailable = objShopRepository.findShopByOwnerId(userId);

        if(isShopEntryAvailable == 0) return false;
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

    public List<String> getShopSubCategories(Long userId) {
        // TODO Auto-generated method stub
        Long categoryId = objShopRepository.getShopCategoryId(userId);
        Long shopId = objShopRepository.getShopId(userId);
        List<String> shopSubCategories = objShopSubcategoryRepository.findBy_Id(shopId);
        shopSubCategories.addAll(objSubCategoryRepository.getShopSubCategories(categoryId));
        return shopSubCategories;

    }

    public void addShopSubCategory(ShopSubCategoryDTO shopSubCategoryDTO, Long shopId) {
        // TODO Auto-generated method stub
            final Logger logger = LoggerFactory.getLogger(AuthController.class);

        logger.info("Request received to add subcategory for shopId: {}", shopId);

        try {

            ShopSubcategory objShopSubcategory = new ShopSubcategory();
            objShopSubcategory.setName(shopSubCategoryDTO.getName());
            objShopSubcategory.setIsActive(shopSubCategoryDTO.isActive());

            Optional<Shop> shop = objShopRepository.findById(shopId);

            if (shop.isEmpty()) {
                logger.error("Shop not found for shopId: {}", shopId);
                throw new RuntimeException("Shop not found with id: " + shopId);
            }
            objShopSubcategory.setCategory(shop.get().getCategory());
            objShopSubcategory.setShop(shop.get());

            objShopSubcategoryRepository.save(objShopSubcategory);

            logger.info("Subcategory '{}' saved successfully for shopId: {}",
                    shopSubCategoryDTO.getName(), shopId);

        } catch (Exception e) {

            logger.error("Error while adding subcategory for shopId: {}", shopId, e);
            throw new RuntimeException("Failed to add subcategory", e);
        }
    }

    public String addProduct(AddProductDTO addProductDTO, Long userId ,String productImageLink) {
        // TODO Auto-generated method stub
        Product product = new Product();
        Long shopId = objShopRepository.getShopId(userId);
        if(objpProductRepository.getProductCountForShop(shopId,addProductDTO.getName()) != 0){
            return "This product is Already Present!!";
        }

        Optional<Shop> shop = objShopRepository.findById(shopId);
        product.setName(addProductDTO.getName());
        product.setPrice(addProductDTO.getPrice());
        product.setIsAvailable(addProductDTO.isAvailable());
        product.setShop(shop.get());
        product.setStock(addProductDTO.getStock());
        product.setWeight(addProductDTO.getWeight());

        Integer isSubCategory =  objSubCategoryRepository.getSubCategoryCount(addProductDTO.getShopSubcategoryName());
        if(isSubCategory >= 1){
            Optional<SubCategory> subCategory = objSubCategoryRepository.findByName(addProductDTO.getShopSubcategoryName());
            if(subCategory.isPresent()){
            product.setSubcategory(subCategory.get());
        }
        }        
        else{
            Optional<ShopSubcategory> shopSubCategory = objShopSubcategoryRepository.findByName(addProductDTO.getShopSubcategoryName());
            if(shopSubCategory.isPresent()){
            product.setShopSubcategory(shopSubCategory.get());
            }

        }
        objpProductRepository.save(product);
        return "Product Added Successfully!!";
        
    }



}
