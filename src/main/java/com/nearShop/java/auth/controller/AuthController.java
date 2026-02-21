package com.nearShop.java.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.auth.dto.OtpRequest;
import com.nearShop.java.auth.dto.OtpVerifyRequest;
import com.nearShop.java.auth.dto.response.LoginResponse;
import com.nearShop.java.auth.service.AuthService;
import com.nearShop.java.security.jwt.JwtUtil;
import com.nearShop.java.services.OtpService;
import com.nearShop.java.services.ShopkeeperServices;
import com.nearShop.java.utilities.NearShopUtility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ShopkeeperServices objShopkeeperServices;

    @Autowired
    private NearShopUtility objNearShopUtility;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
            HttpServletResponse response) {

        try {
            //checking for valid request
            if (request == null || request.getMobile() == null || request.getPassword() == null) {
                logger.warn("Login failed: Missing mobile or password");
                return ResponseEntity.badRequest().body("Mobile and Password are required");
            }

            logger.info("Login attempt for mobile: {}", request.getMobile());
            String token = authService.login(request);

            if (token == null || token.isEmpty()) {
                logger.error("Token generation failed for mobile: {}", request.getMobile());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            //adding cookie
            Cookie cookie = authService.createCookie(token);
            response.addCookie(cookie);
            LoginResponse objLoginResponse = new LoginResponse();

            // getting user roles
            List<String> roles = objNearShopUtility.getUserRoles(request.getMobile());
            if (!roles.isEmpty() && roles.contains(request.getLoginRole()))
                objLoginResponse.setRole(request.getLoginRole());
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // HTTP 401
                                 .body("No roles Assigned Contact Administrator!!");
            }

            // ObjectMapper mapper = new ObjectMapper();
            // String json = mapper.writeValueAsString(objLoginResponse);
            // response.getWriter().write(json);
            // logger.info("Login successful for mobile: {}", request.getMobile());
            return ResponseEntity.ok("Login Successful");

        } catch (Exception e) {
            logger.error("Exception during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong during login");
        }
    }

    // ================= GET USER ROLE =================
    @GetMapping("/getUserRole")
    public ResponseEntity<String> getRole(HttpServletRequest request) {

        try {
            String token = extractJwtFromCookies(request);

            if (token == null) {
                logger.warn("JWT token not found in cookies");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token found");
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);

            if (role == null) {
                logger.warn("Role not found in JWT claims");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            logger.info("Role fetched successfully: {}", role);
            return ResponseEntity.ok(role);

        } catch (JwtException e) {
            logger.error("Invalid or expired JWT token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        } catch (Exception e) {
            logger.error("Exception while fetching role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
    }

    // ================= SEND OTP =================
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {

        try {
            if (request == null || request.getMobile() == null || request.getRole() == null) {
                logger.warn("OTP send failed: Missing mobile or role");
                return ResponseEntity.badRequest().body("Mobile and role are required");
            }

            logger.info("Sending OTP to mobile: {}", request.getMobile());

            String msg = otpService.sendOtp(request.getMobile(), request.getRole());

            logger.info("OTP sent successfully to mobile: {}", request.getMobile());

            return ResponseEntity.ok(msg);

        } catch (Exception e) {
            logger.error("Exception while sending OTP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP");
        }
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request, HttpServletResponse response) {

        try {
            if (request == null ||
                    request.getMobile() == null ||
                    request.getOtp() == null ||
                    request.getPassword() == null ||
                    request.getRole() == null) {

                logger.warn("OTP verification failed: Missing required fields");
                return ResponseEntity.badRequest().body("All fields are required");
            }

            logger.info("Verifying OTP for mobile: {}", request.getMobile());

            boolean isVerified = otpService.verifyOtp(
                    request.getMobile(),
                    request.getOtp(),
                    request.getPassword(),
                    request.getRole());

            if (!isVerified) {
                logger.warn("OTP verification failed for mobile: {}", request.getMobile());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid OTP or expired OTP");
            }

            logger.info("OTP verified successfully for mobile: {}", request.getMobile());
            LoginRequest objLoginRequest = new LoginRequest();
            objLoginRequest.setMobile(request.getMobile());
            objLoginRequest.setPassword(request.getPassword());
            objLoginRequest.setLoginRole(request.getRole());

            String token = authService.login(objLoginRequest);

            if (token == null || token.isEmpty()) {
                logger.error("Token generation failed for mobile: {}", request.getMobile());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            Cookie cookie = authService.createCookie(token);
            response.addCookie(cookie);

            logger.info("Login successful for mobile: {}", request.getMobile());

            return ResponseEntity.ok("Login Successful");

        } catch (Exception e) {
            logger.error("Exception during OTP verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong during OTP verification");
        }
    }

    // ================= HELPER METHOD =================
    private String extractJwtFromCookies(HttpServletRequest request) {

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
}
