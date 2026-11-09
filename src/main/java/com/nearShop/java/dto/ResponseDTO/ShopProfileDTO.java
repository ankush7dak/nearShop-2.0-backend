package com.nearShop.java.dto.ResponseDTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nearShop.java.dto.ShopDTO;

import lombok.Data;

@Data
public class ShopProfileDTO {
    UserDTO userDTO;
    ShopDTO shopDTO;
    MultipartFile logo;
    String logoUrl;
}
