package com.nearShop.java.auth.dto;

import lombok.Data;
@Data
public class OtpVerifyRequest {
    private String mobile;
    private String otp;
    private String password;
    private String role;
}