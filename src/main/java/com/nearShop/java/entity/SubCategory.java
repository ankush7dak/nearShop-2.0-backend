package com.nearShop.java.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subcategories")
@Data
public class SubCategory {

    @Id
    @Column(name = "sub_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subCategoryId;

    // Many subcategories belong to one shop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // Auto set createdAt
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }






    // Getters and Setters
}