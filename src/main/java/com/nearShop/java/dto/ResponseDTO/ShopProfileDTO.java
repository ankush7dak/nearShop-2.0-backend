package com.nearShop.java.dto.ResponseDTO;

import java.util.List;
import com.nearShop.java.dto.ShopDTO;

import lombok.Data;

@Data
public class ShopProfileDTO {
    UserDTO userDTO;
    ShopDTO shopDTO;
}
