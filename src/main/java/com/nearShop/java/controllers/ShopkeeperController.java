package com.nearShop.java.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.security.jwt.JwtUtil;
import com.nearShop.java.services.ShopkeeperServices;
import com.nearShop.java.utilities.NearShopUtility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/shop")
public class ShopkeeperController {
    @Autowired
    ShopkeeperServices objShopkeeperServices;
    @Autowired
    NearShopUtility objNearShopUtility;
    @Autowired
    JwtUtil obJwtUtil;

    @GetMapping("/isShopRegistered")
    public ResponseEntity<?> isShopRegistered(HttpServletRequest request,HttpServletResponse response){
        String token = objNearShopUtility.extractJwtFromCookies(request);
        final Logger logger = LoggerFactory.getLogger(AuthController.class);

            if (token == null) {
                logger.warn("JWT token not found in cookies");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token found");
            }
            //token claim
            Claims claims = obJwtUtil.extractAllClaims(token);

            Long userId = claims.get("userId", Long.class);
            boolean isRegistered = objShopkeeperServices.isShopRegistered(userId);


        return ResponseEntity.ok((isRegistered)?1:0);
    }

    @GetMapping("/getAllShopCategories")
    public ResponseEntity<?> getAllShopCategories(HttpServletResponse response){
        List<String> shopCategories = objShopkeeperServices.getAllShopCategories();
        return ResponseEntity.ok(shopCategories);
    }
}
