package com.nearShop.java.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
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

    // Constructors
    public Product() {
    }

    public Product(Shop shop, ShopSubcategory shopSubcategory, String name,
                   BigDecimal price, Integer stock, Boolean isAvailable) {
        this.shop = shop;
        this.shopSubcategory = shopSubcategory;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ShopSubcategory getShopSubcategory() {
        return shopSubcategory;
    }

    public void setShopSubcategory(ShopSubcategory shopSubcategory) {
        this.shopSubcategory = shopSubcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}
