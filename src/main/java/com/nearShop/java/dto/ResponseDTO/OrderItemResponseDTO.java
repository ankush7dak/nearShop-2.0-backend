package com.nearShop.java.dto.ResponseDTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponseDTO {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal itemTotal;
}
