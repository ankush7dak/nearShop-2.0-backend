package com.nearShop.java.dto.ResponseDTO;

import java.time.LocalDate;

public record CustomerProfileDTO(
        Long customerId,
        String name,
        String email,
        String phone,
        String gender,
        LocalDate dob,
        String address,
        String city,
        String state,
        String pincode
) {}