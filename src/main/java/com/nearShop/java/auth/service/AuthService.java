package com.nearShop.java.auth.service;

import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.auth.dto.response.LoginResponse;
import com.nearShop.java.entity.User;
import com.nearShop.java.entity.UserRole;
import com.nearShop.java.repository.RoleRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.repository.UserRoleRepository;
import com.nearShop.java.security.jwt.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Login user and generate JWT token
     */
    public String login(LoginRequest request) {
        logger.info("Login attempt for mobile: {}", request.getMobile());

        Optional<User> optionalUser = userRepository.findByMobile(request.getMobile());
        if (optionalUser.isEmpty()) {
            logger.warn("Login failed for mobile {}: user not found", request.getMobile());
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // Check password (replace with BCrypt check in production)
        if (!request.getPassword().equals(user.getPassword())) {
            logger.warn("Login failed for mobile {}: invalid password", request.getMobile());
            throw new RuntimeException("Invalid password");
        }

        Optional<UserRole> optionalRole = userRoleRepository.findByUser_Id(user.getId());
        if (optionalRole.isEmpty()) {
            logger.warn("Login failed for mobile {}: role not assigned", request.getMobile());
            throw new RuntimeException("Role not assigned");
        }

        UserRole userRole = optionalRole.get();
        String roleName = userRole.getRole().getName();
        Long userId = user.getId();

        String token = jwtUtil.generateToken(user.getMobile(), roleName, userId);
        logger.info("Login successful for mobile {} with role {}", user.getMobile(), roleName);

        
        // LoginResponse loginResponse = new LoginResponse();
        // loginResponse.setToken(token);

        // setting user
        // if(user.getStatus().equals("PENDING")){
        // loginResponse.setAccountStatus(user.getStatus());
        // loginResponse.setMessage("Please register you shop, Redirecting you to
        // registration Page");
        // }
        // else{
        // loginResponse.setAccountStatus(user.getStatus());
        // loginResponse.setMessage("Registered Shop");

        // }

        return token;
    }

    public Cookie createCookie(String token){
        Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            return cookie;
    }
}
