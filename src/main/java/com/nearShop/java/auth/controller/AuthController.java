package com.nearShop.java.auth.controller;

import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.auth.dto.OtpRequest;
import com.nearShop.java.auth.dto.OtpVerifyRequest;
import com.nearShop.java.auth.dto.response.LoginResponse;
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }



    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {
        String msg = otpService.sendOtp(request.getMobile(),request.getRole());
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestBody OtpVerifyRequest request) {
        LoginResponse loginResponse = otpService.verifyOtp(request.getMobile(), request.getOtp(),request.getPassword(),request.getRole());
        return ResponseEntity.ok(loginResponse);
    }
}
