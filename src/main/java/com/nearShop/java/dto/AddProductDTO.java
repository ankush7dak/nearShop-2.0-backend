package com.nearShop.java.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddProductDTO {
    private String name;
    private String shopSubcategoryName;
    private String description;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stock;
    private String weight;
    private boolean isAvailable;
}
