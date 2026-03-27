package com.nearShop.java.dto.ResponseDTO;

import java.util.List;
import java.util.Map;

import com.nearShop.java.dto.ProductDTO;
import com.nearShop.java.entity.Product;

import lombok.Data;

@Data
public class ShopInventoryDataDTO {
    private List<ProductDTO> productDTOList;
    private Map<String, Object> response;
    private String errMsg;
    private String errCode;
    private Boolean isLastPage;
}
