package com.nearShop.java.dto.ResponseDTO;

import java.util.List;

import com.nearShop.java.dto.ProductDTO;

import lombok.Data;

@Data 
public class CartResponseDTO {
    List<ProductDTO> lstProductDTO;
    String shopName;
    String shopProfileUrl;
}
