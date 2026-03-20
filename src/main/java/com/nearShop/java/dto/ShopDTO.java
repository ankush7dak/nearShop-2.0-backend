package com.nearShop.java.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.cglib.core.Local;

import lombok.Data;

// ShopDTO.java
@Data
public class ShopDTO {
    private Long id;
    private String shopName;
    private String mobile;
    private String email;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String latitude;
    private String longitude;
    private String categoryName;
    private Boolean providesDelivery;
    private Integer deliveryRange;
    private String status;
    private Boolean isActive;

    // getters and setters
}
