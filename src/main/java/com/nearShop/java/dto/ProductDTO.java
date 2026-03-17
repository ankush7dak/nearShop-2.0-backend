package com.nearShop.java.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal cost;
    private String description;
    private Integer stock;
    private String weight;
    private Boolean isAvailable;
    // Instead of full objects, send only IDs or names
    private Long shopId;
    private String shopSubcategoryName;
    private String subcategoryName;
}
