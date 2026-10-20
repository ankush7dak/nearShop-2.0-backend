package com.nearShop.java.dto.ResponseDTO;

import lombok.Data;

@Data
public class ShopkeeperDashboardDTO {
    private Integer productCount;
    private Double totalRevenue;
    private Long errCode;
    private Long errMessage;
    private Integer newOrderCount;
    private Integer lowStockCount;
}
