package com.nearShop.java.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional email
    @Column(unique = true, nullable = true, length = 100)
    private String email;

    // Login field
    @Column(unique = true, nullable = false, length = 15)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_mobile_verified", nullable = false)
    private Boolean isMobileVerified;

    @Column(nullable = false, length = 20)
    private String status;   // ACTIVE / BLOCKED / PENDING

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = "ACTIVE";
        }

        if (this.isMobileVerified == null) {
            this.isMobileVerified = false;
        }
    }

    public User orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
