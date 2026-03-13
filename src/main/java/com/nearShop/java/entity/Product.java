package com.nearShop.java.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many products belong to one shop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // Many products belong to one shop subcategory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_subcategory_id", nullable = false)
    private ShopSubcategory shopSubcategory;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    // One product has many inventory logs
    // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    // private List<InventoryLog> inventoryLogs;

    // // One product can be in many order items
    // @OneToMany(mappedBy = "product")
    // private List<OrderItem> orderItems;
}