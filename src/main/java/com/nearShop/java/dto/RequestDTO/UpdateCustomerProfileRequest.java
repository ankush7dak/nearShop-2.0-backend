package com.nearShop.java.dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCustomerProfileRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Pattern(
            regexp = "^(Male|Female|Other)?$",
            message = "Gender must be Male, Female, Other or empty"
        )
        String gender,

        LocalDate dob,

        @Size(max = 255)
        String address,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @Pattern(regexp = "^$|^[0-9]{6}$",
                 message = "PIN code must contain six digits")
        String pincode
) {}