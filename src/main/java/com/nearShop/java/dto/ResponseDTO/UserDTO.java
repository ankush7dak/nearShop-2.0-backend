package com.nearShop.java.dto.ResponseDTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String mobile;
    private Boolean isMobileVerified;
    private String status;
    private LocalDateTime createdAt;
}