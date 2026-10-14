package com.nearShop.java.dto.RequestDTO;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.nearShop.java.dto.OrderItemDTO;

import lombok.Data;

@Data
public class OrderRequestDTO {

    private Long shopId;

    private List<OrderItemDTO> items;

    private Integer subtotal;

    private BigDecimal discount;

    private Integer discountAmount;

    private BigDecimal deliveryFee;

    private Integer totalAmount;

    private String couponCode;

    private Integer totalItems;

    private OffsetDateTime orderDate;
     }