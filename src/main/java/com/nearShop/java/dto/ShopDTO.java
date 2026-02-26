package com.nearShop.java.dto;

import lombok.Data;

// ShopDTO.java
@Data
public class ShopDTO {
    private String shopName;
    private String mobile;
    private String email;
    private String description;
    private String openingTime;
    private String closingTime;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String latitude;
    private String longitude;
    private String categoryId;

    // getters and setters
}
