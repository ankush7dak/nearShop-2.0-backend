package com.nearShop.java.dto.RequestDTO;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String mobile;
    private String password;
    private String role;
    private String otp;
    private String name;
    private String email;
}
