package com.nearShop.java.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nearShop.java.dto.OrderItemDTO;
import com.nearShop.java.dto.ProductDTO;
import com.nearShop.java.dto.RequestDTO.OrderRequestDTO;
import com.nearShop.java.entity.Cart;
import com.nearShop.java.entity.CartItem;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Order;
import com.nearShop.java.entity.OrderItem;
import com.nearShop.java.entity.Product;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.ShopSubcategory;
import com.nearShop.java.entity.SubCategory;
import com.nearShop.java.repository.CartItemRepository;
import com.nearShop.java.repository.CartRepository;
import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.repository.OrderRepository;
import com.nearShop.java.repository.ProductRepository;
import com.nearShop.java.repository.ShopRepository;
import com.nearShop.java.repository.ShopSubcategoryRepository;
import com.nearShop.java.repository.SubCategoryRepository;

@Service
public class CustomerServices {

    @Autowired
    ShopRepository objShopRepository;
    @Autowired
    SubCategoryRepository objSubCategoryRepository;
    @Autowired
    ProductRepository objProductRepository;
    @Autowired
    ShopSubcategoryRepository objShopSubcategoryRepository;
    @Autowired
    CategoryRepository objcCategoryRepository;
    @Autowired
    CartRepository objCartRepository;
    @Autowired
    CartItemRepository objCartItemRepository;
    @Autowired
    OrderRepository objOrderRepository;

    public Page<Product> getProductsForShopId(Integer page, Integer size, String search, String category, Long shopId) {
        // TODO Auto-generated method stub

        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Optional<ShopSubcategory> shopSubCat = objShopSubcategoryRepository.findByName(category);
        Long cat_id = null;
        if (shopSubCat.isPresent()) {
            cat_id = shopSubCat.get().getShopSubCategoryId();
        } else {
            Optional<SubCategory> subcat = objSubCategoryRepository.findByName(category);
            if (subcat.isPresent()) {
                cat_id = subcat.get().getSubCategoryId();
            }
        }

        return objProductRepository.searchProducts(search, cat_id, shopId, pageable);
    }

    public List<String> getSelectedShopSubCategories(Long shopId) {
        // TODO Auto-generated method stub
        Long categoryId = objShopRepository.getShopCategoryIdByShopId(shopId);
        List<String> shopSubCategories = objShopSubcategoryRepository.findBy_Id(shopId);
        shopSubCategories.addAll(objSubCategoryRepository.getShopSubCategories(categoryId));
        return shopSubCategories;

    }

    public Page<Shop> getShopData(String shopSearch, String shopCategory, Long shopDistanceRange, Integer shopPage,
            Integer shopSize, Double userLatitude, Double userLongitude) {
        // TODO Auto-generated method stub
        Pageable pageable = PageRequest.of(shopPage, shopSize);
        Optional<Category> cat = objcCategoryRepository.findByName(shopCategory);
        Long catId = null;
        if (cat.isPresent()) {
            catId = cat.get().getId();
        }

        return objShopRepository.fetchShopData(shopSearch, catId, shopDistanceRange, userLatitude, userLongitude,
                pageable);
    }

    public Map<Long, Integer> getCartQuantities(Long userId) {
        Optional<Cart> cart = objCartRepository.getCartData( userId);

        if (!cart.isPresent()) {
            return new HashMap<>();
        }

        List<CartItem> items = objCartItemRepository.getAllCartData(cart.get().getCartId());

        Map<Long, Integer> result = new HashMap<>();

        for (CartItem item : items) {
            result.put(item.getProductId(), item.getQuantity());
        }

        return result;
    }
    @Transactional
    public void placeOrder(Long userId, OrderRequestDTO orderRequest, Cart cart) {
        // TODO Auto-generated method stub
        
       try{
            Order order = new Order();

            order.setUserId(userId);
            order.setShopId(orderRequest.getShopId());

            order.setSubtotal(orderRequest.getSubtotal());
            order.setDiscount(orderRequest.getDiscount());
            order.setDiscountAmount(orderRequest.getDiscountAmount());
            order.setDeliveryFee(orderRequest.getDeliveryFee());
            order.setTotalAmount(orderRequest.getTotalAmount());
            order.setCouponCode(orderRequest.getCouponCode());
            order.setTotalItems(orderRequest.getTotalItems());

        // order.setOrderDate(
        // orderRequest.getOrderDate() != null
        //     ? orderRequest.getOrderDate()
        //     : OffsetDateTime.now()
        // );

    order.setOrderStatus("PLACED");

    // Create Order Items
    ArrayList<OrderItem> orderItems = new ArrayList<>();

    for (OrderItemDTO itemDTO : orderRequest.getItems()) {

        OrderItem orderItem = new OrderItem();

        orderItem.setProductId(itemDTO.getProductId());
        orderItem.setProductName(itemDTO.getProductName());
        orderItem.setPrice(itemDTO.getPrice());
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setItemTotal(itemDTO.getItemTotal());

        // Important: set parent order
        orderItem.setOrder(order);

        orderItems.add(orderItem);
    }

    order.setItems(orderItems);

    // Save order + order items
    objOrderRepository.save(order);

    // Clear cart after successful order
        cart.getItems().clear();
    objCartRepository.save(cart);
       }catch(Exception e){
        throw new RuntimeException("Error occurred while placing the order: " + e.getMessage());
       }
    }
}
