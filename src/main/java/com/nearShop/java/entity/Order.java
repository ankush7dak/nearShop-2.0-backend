package com.nearShop.java.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;

=======
>>>>>>> 85d808296e6e5c943ba3e73690bffd70071d6d37
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    private Long shopId;

    private Integer subtotal;

    private BigDecimal discount;

    private Integer discountAmount;

    private BigDecimal deliveryFee;

    private Integer totalAmount;

    private String couponCode;

    private Integer totalItems;

    private OffsetDateTime orderDate;

    private String orderStatus;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items;
}