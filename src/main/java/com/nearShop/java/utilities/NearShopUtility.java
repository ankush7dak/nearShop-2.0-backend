package com.nearShop.java.utilities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.repository.RoleRepository;
import com.nearShop.java.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NearShopUtility {
    @Autowired
    RoleRepository objRoleRepository;

    @Autowired
    JwtUtil obJwtUtil;

    Logger logger = LoggerFactory.getLogger(NearShopUtility.class);
    public List<String> getUserRoles(String mobile) {
        // TODO Auto-generated method stub
        return objRoleRepository.findRoleNamesByMobile(mobile);

    }

    public String extractJwtFromCookies(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public Claims claimParser(String token) {
        // TODO Auto-generated method stub
        Claims claims = null;
        try {
            claims = obJwtUtil.extractAllClaims(token);
            Jwts.parserBuilder()
                    .setSigningKey(obJwtUtil.getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            
        }
        return claims;
    }

}
