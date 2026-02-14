package com.nearShop.java.auth.controller;

import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.auth.dto.OtpRequest;
import com.nearShop.java.auth.dto.OtpVerifyRequest;
import com.nearShop.java.auth.service.AuthService;
import com.nearShop.java.services.OtpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;
     @Autowired
    private OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {
        otpService.sendOtp(request.getMobile());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        String token = otpService.verifyOtp(request.getMobile(), request.getOtp(),request.getPassword(),request.getRole());
        return ResponseEntity.ok(token);
    }
}
