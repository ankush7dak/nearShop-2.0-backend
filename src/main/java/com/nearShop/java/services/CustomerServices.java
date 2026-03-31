package com.nearShop.java.services;

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

import com.nearShop.java.dto.ProductDTO;
import com.nearShop.java.entity.Cart;
import com.nearShop.java.entity.CartItem;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Product;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.ShopSubcategory;
import com.nearShop.java.entity.SubCategory;
import com.nearShop.java.repository.CartItemRepository;
import com.nearShop.java.repository.CartRepository;
import com.nearShop.java.repository.CategoryRepository;
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

    public Map<Long, Integer> getCartQuantities(Long userId, Long shopId) {
        Optional<Cart> cart = objCartRepository.getCartData(shopId, userId);

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
}
