package com.nearShop.java.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.dto.AddProductDTO;
import com.nearShop.java.dto.ProductDTO;
import com.nearShop.java.dto.ShopDTO;
import com.nearShop.java.dto.ShopSubCategoryDTO;
import com.nearShop.java.dto.RequestDTO.NavDTO;
import com.nearShop.java.dto.ResponseDTO.ShopInventoryDataDTO;
import com.nearShop.java.dto.ResponseDTO.ShopProfileDTO;
import com.nearShop.java.dto.ResponseDTO.ShopkeeperDashboardDTO;
import com.nearShop.java.repository.ShopRepository;
import com.nearShop.java.security.jwt.JwtUtil;
import com.nearShop.java.services.R2Service;
import com.nearShop.java.services.ShopkeeperServices;
import com.nearShop.java.utilities.NearShopUtility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/shop")
public class ShopkeeperController {
    @Autowired
    ShopkeeperServices objShopkeeperServices;
    @Autowired
    NearShopUtility objNearShopUtility;
    @Autowired
    JwtUtil obJwtUtil;
    @Autowired
    ShopRepository objShopRepository;
    @Autowired
    R2Service objR2Service;


    @GetMapping("/getDashboardData")
    public ResponseEntity<?> getDashboardData(HttpServletRequest req){
        try{
            Long userId = objNearShopUtility.getUserIdUsingRequest(req);
        ShopkeeperDashboardDTO objShopkeeperDashboardDTO = new ShopkeeperDashboardDTO();
        objShopkeeperDashboardDTO = objShopkeeperServices.getDashboardData(userId);
        // fetching product count
        return ResponseEntity.ok(objShopkeeperDashboardDTO);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error");
        }

    }

    @GetMapping("/getNavData")
    public ResponseEntity<?> getNavData(HttpServletRequest req){
        try{
            NavDTO objNavDTO = objShopkeeperServices.getNavData(req);
            return ResponseEntity.ok(objNavDTO);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error");
        }
    }

    @GetMapping("/getAllInvertoryData")
    public ResponseEntity<ShopInventoryDataDTO> getAllInvertoryData(HttpServletRequest req){
        ShopInventoryDataDTO objShopInventoryDataDTO = new ShopInventoryDataDTO();
        try{
            Long userId = objNearShopUtility.getUserIdUsingRequest(req);
            objShopInventoryDataDTO = objShopkeeperServices.getAllInvertoryData(objShopInventoryDataDTO,userId);
            return ResponseEntity.ok(objShopInventoryDataDTO);
        }catch(Exception e){
            objShopInventoryDataDTO.setErrCode("ERROR");
            objShopInventoryDataDTO.setErrMsg(e.getMessage());
            return ResponseEntity.status(500).body(objShopInventoryDataDTO);
        }
    }

    @GetMapping("/isShopRegistered")
    public ResponseEntity<?> isShopRegistered(HttpServletRequest request) {
        Long userId = objNearShopUtility.getUserIdUsingRequest(request);
        boolean isRegistered = objShopkeeperServices.isShopRegistered(userId);

        return ResponseEntity.ok((isRegistered) ? 1 : 0);
    }

    @GetMapping("/getAllShopCategories")
    public ResponseEntity<?> getAllShopCategories(HttpServletResponse response) {
        List<String> shopCategories = objShopkeeperServices.getAllShopCategories();
        return ResponseEntity.ok(shopCategories);
    }

    @GetMapping("/getShopSubCategories")
    public ResponseEntity<?> getShopSubCategories(HttpServletRequest request) {
        try {
            Logger logger = LoggerFactory.getLogger(getClass());

            String token = objNearShopUtility.extractJwtFromCookies(request);
            Claims claims = objNearShopUtility.claimParser(token);
            Long userId = claims.get("userId", Long.class);
            List<String> shopSubCategories = objShopkeeperServices.getShopSubCategories(userId);

            return ResponseEntity.ok(shopSubCategories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }

    }

    @GetMapping("/getShopProfile")
    public ResponseEntity<?> getShopProfile(HttpServletRequest request){
        try{
            Long userId = objNearShopUtility.getUserIdUsingRequest(request);
            ShopProfileDTO objShopProfileDTO = new ShopProfileDTO();
            objShopProfileDTO = objShopkeeperServices.getShopProfile(objShopProfileDTO,userId);
            return ResponseEntity.ok(objShopProfileDTO);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error");
        }
    }

    @PostMapping("/addShopSubCategory")
    public ResponseEntity<?> addShopSubCategory(
            HttpServletRequest request,
            @RequestBody ShopSubCategoryDTO shopSubCategoryDTO) {

        Logger logger = LoggerFactory.getLogger(getClass());

        logger.info("Received request to add shop subcategory");

        try {

            Long userId = objNearShopUtility.getUserIdUsingRequest(request);
            logger.info("User ID extracted from token: {}", userId);

            Long shopId = objShopRepository.getShopId(userId);
            if (shopId == null) {
                logger.error("No shop found for userId: {}", userId);
                return ResponseEntity.status(404).body("Shop not found for user");
            }

            logger.info("Shop ID found: {}", shopId);

            objShopkeeperServices.addShopSubCategory(shopSubCategoryDTO, shopId);

            logger.info("Subcategory '{}' added successfully for shopId: {}",
                    shopSubCategoryDTO.getName(), shopId);

            return ResponseEntity.ok("SubCategory Added Successfully!!");

        } catch (Exception e) {

            logger.error("Error while adding shop subcategory", e);

            return ResponseEntity
                    .status(500)
                    .body("Error while adding subcategory: " + e.getMessage());
        }
    }

    @PostMapping("/registorShop")
    public ResponseEntity<?> registorShop(
            HttpServletRequest request,
            @ModelAttribute ShopDTO shopDTO,
            @RequestParam("logo") MultipartFile logo) {
        try {
            Long userId = objNearShopUtility.getUserIdUsingRequest(request);
            boolean isRegistrationDone = objShopkeeperServices.registerShop(shopDTO, logo, userId);
            return ResponseEntity.ok(isRegistrationDone);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(
                        HttpServletRequest request,
                        @ModelAttribute AddProductDTO addProductDTO,
                        @RequestParam("productImage") MultipartFile productImage
    ){
        try{
            Long userId = objNearShopUtility.getUserIdUsingRequest(request);
            String productImageLink = objR2Service.uploadFile(productImage);
            String resMessage = objShopkeeperServices.addProduct(addProductDTO,userId,productImageLink);
            return ResponseEntity.ok(resMessage);

        }catch(Exception e){
            return ResponseEntity.status(500).body("Error" + e.getMessage());
        }
    }

     @PostMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(
                        HttpServletRequest request,
                        @RequestBody ProductDTO productDTO
                        
    ){
        try{
            Long userId = objNearShopUtility.getUserIdUsingRequest(request);
            // String productImageLink = objGCSService.uploadFile(productImage);
            String resMessage = objShopkeeperServices.updateProduct(productDTO,userId);
            return ResponseEntity.ok(resMessage);

        }catch(Exception e){
            return ResponseEntity.status(500).body("Error" + e.getMessage());
        }
    }

    @PostMapping("/updateShopProfile")
    public ResponseEntity<?> updateShopProfile(HttpServletRequest req , @RequestBody ShopProfileDTO objShopProfileDTO){
        try{
            String isUpdated = objShopkeeperServices.updateShopProfile(req,objShopProfileDTO);
            return ResponseEntity.ok(isUpdated);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error while Updating");
        }
    }
}


