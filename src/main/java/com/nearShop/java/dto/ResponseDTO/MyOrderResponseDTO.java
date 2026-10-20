package com.nearShop.java.dto.ResponseDTO;


import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class MyOrderResponseDTO {

    private Long orderId;
    private Long userId;
    private Long shopId;
    private String shopName;

    private Integer subtotal;
    private BigDecimal discount;
    private Integer discountAmount;
    private BigDecimal deliveryFee;
    private Integer totalAmount;

    private String couponCode;
    private Integer totalItems;

    private OffsetDateTime orderDate;
    private String orderStatus;

    private List<OrderItemResponseDTO> items;
}