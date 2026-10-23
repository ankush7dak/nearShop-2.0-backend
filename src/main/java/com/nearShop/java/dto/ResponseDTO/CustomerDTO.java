package com.nearShop.java.dto.ResponseDTO;



import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private Long customerId;
    private String name;
    private String mobile;
    private String email;
    private LocalDateTime linkedAt;
}
