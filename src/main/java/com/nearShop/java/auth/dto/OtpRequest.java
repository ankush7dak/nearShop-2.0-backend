package com.nearShop.java.auth.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String mobile;
    private String role; // "customer" or "shopkeeper"
}


