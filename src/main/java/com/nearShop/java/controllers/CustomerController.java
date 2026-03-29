package com.nearShop.java.controllers;

import java.util.List;
import java.util.Optional;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nearShop.java.dto.ProductDTO;
import com.nearShop.java.dto.ShopDTO;
import com.nearShop.java.dto.ResponseDTO.ShopInventoryDataDTO;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Product;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.User;
import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.services.CustomerServices;
import com.nearShop.java.services.ShopkeeperServices;
import com.nearShop.java.utilities.NearShopUtility;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/customer")
public class CustomerController {
    @Autowired
    CustomerServices objCustomerServices;
    @Autowired
    ModelMapper objModelMapper;
    @Autowired
    CategoryRepository objCategoryRepository;
    @Autowired
    NearShopUtility objNearShopUtility;
    @Autowired
    ShopkeeperServices objShopkeeperServices;

    @GetMapping("/getShopData")
    ResponseEntity<?> getShopData(HttpServletRequest req,
        @RequestParam Long shopDistanceRange,
        @RequestParam String shopSearch,
        @RequestParam  String shopCategory,
        @RequestParam Integer shopPage,
        @RequestParam Integer shopSize,
        @RequestParam Double userLatitude,
        @RequestParam Double userLongitude
    ){
        try{
            Page<Shop> shops = objCustomerServices.getShopData(shopSearch,shopCategory ,shopDistanceRange ,shopPage,shopSize,userLatitude,userLongitude);
            List<ShopDTO> shopDtoList = shops.getContent().stream().map(shop->{
                ShopDTO shopDto = objModelMapper.map(shop,ShopDTO.class);
                Optional<Category> category = objCategoryRepository.findById(shop.getCategory().getId());
                shopDto.setCategoryName(category.get().getName());
                if(shop.getOwner() != null){
                    User user = shop.getOwner();
                    shopDto.setEmail(user.getEmail());
                    shopDto.setMobile(user.getMobile());
                }
                return shopDto;
            }).toList();
            return ResponseEntity.ok(shopDtoList);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error");
        }
    }

    @GetMapping("/getShopProducts")
    ResponseEntity<?> getShopProducts(HttpServletRequest req,
        @RequestParam Long shopId,
        @RequestParam Integer page,
        @RequestParam Integer size,
        @RequestParam String search,
        @RequestParam String category
    ){
        try{
            ShopInventoryDataDTO objShopInventoryDataDTO = new ShopInventoryDataDTO();
            Page<Product> lstProductPage = objCustomerServices.getProductsForShopId(page,size,search,category,shopId);
            objShopInventoryDataDTO.setProductDTOList(lstProductPage.getContent().stream().map(product->{
                ProductDTO dto = objModelMapper.map(product,ProductDTO.class);
                dto.setShopId(product.getShop().getId());
                if (product.getShopSubcategory() != null) {
                    dto.setShopSubcategoryName(product.getShopSubcategory().getName());
                } else if (product.getSubcategory() != null) {
                    dto.setSubcategoryName(product.getSubcategory().getName());
                }
                return dto;
            }).toList());
            return ResponseEntity.ok(objShopInventoryDataDTO);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error");
        }
    }

    @GetMapping("/getSelectedShopSubCategories")
    public ResponseEntity<?> getSelectedShopSubCategories(HttpServletRequest request,
        @RequestParam Long shopId
    ) {
        try {
            // Logger logger = LoggerFactory.getLogger(getClass());

            List<String> shopSubCategories = objCustomerServices.getSelectedShopSubCategories(shopId);

            return ResponseEntity.ok(shopSubCategories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }

    }
}
