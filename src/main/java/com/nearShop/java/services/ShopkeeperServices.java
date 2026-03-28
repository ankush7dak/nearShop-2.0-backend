package com.nearShop.java.services;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.dto.AddProductDTO;
import com.nearShop.java.dto.ShopDTO;
import com.nearShop.java.dto.ProductDTO;

import com.nearShop.java.dto.ShopSubCategoryDTO;
import com.nearShop.java.dto.RequestDTO.NavDTO;
import com.nearShop.java.dto.ResponseDTO.ShopInventoryDataDTO;
import com.nearShop.java.dto.ResponseDTO.ShopProfileDTO;
import com.nearShop.java.dto.ResponseDTO.ShopkeeperDashboardDTO;
import com.nearShop.java.dto.ResponseDTO.UserDTO;
import com.nearShop.java.entity.Category;
import com.nearShop.java.entity.Product;
import com.nearShop.java.entity.Shop;
import com.nearShop.java.entity.ShopSubcategory;
import com.nearShop.java.entity.SubCategory;
import com.nearShop.java.entity.User;
import com.nearShop.java.repository.CategoryRepository;
import com.nearShop.java.repository.ProductRepository;
import com.nearShop.java.repository.ShopRepository;
import com.nearShop.java.repository.ShopSubcategoryRepository;
import com.nearShop.java.repository.SubCategoryRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.utilities.NearShopUtility;
import java.util.Optional;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ShopkeeperServices {
    @Autowired
    NearShopUtility objNearShopUtility;
    @Autowired
    UserRepository objUserRepository;
    @Autowired
    CategoryRepository objCategoryRepository;
    @Autowired
    R2Service objR2Service;
    @Autowired
    ShopRepository objShopRepository;
    @Autowired
    SubCategoryRepository objSubCategoryRepository;
    @Autowired
    ShopSubcategoryRepository objShopSubcategoryRepository;
    @Autowired
    ProductRepository objProductRepository;
    @Autowired
    ModelMapper objModelMapper;

    public boolean isShopRegistered(Long userId) {
        // TODO Auto-generated method stub
        // String status = objUserRepository.findByUser_id(userId);
        int isShopEntryAvailable = objShopRepository.findShopByOwnerId(userId);

        if (isShopEntryAvailable == 0)
            return false;
        return true;

    }

    public List<String> getAllShopCategories() {
        // TODO Auto-generated method stub
        return objCategoryRepository.findAllCategories();
    }

    public boolean registerShop(ShopDTO shopDTO, MultipartFile logo, Long userId) throws IOException {

        // Convert userId to Long
        try {
            Long id = userId;

            // Fetch user properly (not Optional)
            User user = objUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch category by name
            Category category = objCategoryRepository.findByName(shopDTO.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Create Shop object
            Shop shop = new Shop();
            shop.setOwner(user);
            shop.setShopName(shopDTO.getShopName());
            shop.setAddress(shopDTO.getAddress());
            shop.setDescription(shopDTO.getDescription());
            shop.setClosingTime((shopDTO.getClosingTime()));
            shop.setOpeningTime((shopDTO.getOpeningTime()));
            shop.setCategory(category);
            shop.setStatus("open");
            shop.setLatitude(Double.parseDouble(shopDTO.getLatitude()));
            shop.setLongitude(Double.parseDouble(shopDTO.getLongitude()));
            // Handle logo (optional)
            if (logo != null && !logo.isEmpty()) {
                shop.setLogoUrl(objR2Service.uploadFile(logo));
            }

            // Save to database
            objShopRepository.save(shop);
        } catch (Exception e) {
            return false;
        }

        return true; // now returning true after saving
    }

    public List<String> getShopSubCategories(Long userId) {
        // TODO Auto-generated method stub
        Long categoryId = objShopRepository.getShopCategoryId(userId);
        Long shopId = objShopRepository.getShopId(userId);
        List<String> shopSubCategories = objShopSubcategoryRepository.findBy_Id(shopId);
        shopSubCategories.addAll(objSubCategoryRepository.getShopSubCategories(categoryId));
        return shopSubCategories;

    }

    public void addShopSubCategory(ShopSubCategoryDTO shopSubCategoryDTO, Long shopId) {
        // TODO Auto-generated method stub
        final Logger logger = LoggerFactory.getLogger(AuthController.class);

        logger.info("Request received to add subcategory for shopId: {}", shopId);

        try {

            ShopSubcategory objShopSubcategory = new ShopSubcategory();
            objShopSubcategory.setName(shopSubCategoryDTO.getName());
            objShopSubcategory.setIsActive(shopSubCategoryDTO.isActive());

            Optional<Shop> shop = objShopRepository.findById(shopId);

            if (shop.isEmpty()) {
                logger.error("Shop not found for shopId: {}", shopId);
                throw new RuntimeException("Shop not found with id: " + shopId);
            }
            objShopSubcategory.setCategory(shop.get().getCategory());
            objShopSubcategory.setShop(shop.get());

            objShopSubcategoryRepository.save(objShopSubcategory);

            logger.info("Subcategory '{}' saved successfully for shopId: {}",
                    shopSubCategoryDTO.getName(), shopId);

        } catch (Exception e) {

            logger.error("Error while adding subcategory for shopId: {}", shopId, e);
            throw new RuntimeException("Failed to add subcategory", e);
        }
    }

    public String addProduct(AddProductDTO addProductDTO, Long userId, String productImageLink) {
        // TODO Auto-generated method stub
        Product product = new Product();
        Long shopId = objShopRepository.getShopId(userId);
        if (objProductRepository.getProductCountForShop(shopId, addProductDTO.getName()) != 0) {
            return "This product is Already Present!!";
        }

        Optional<Shop> shop = objShopRepository.findById(shopId);
        product.setName(addProductDTO.getName());
        product.setPrice(addProductDTO.getPrice());
        product.setIsAvailable(addProductDTO.isAvailable());
        product.setShop(shop.get());
        product.setStock(addProductDTO.getStock());
        product.setWeight(addProductDTO.getWeight());
        product.setCost(addProductDTO.getCost());
        product.setDescription(addProductDTO.getDescription());
        product.setImageLink(productImageLink);

        Integer isSubCategory = objSubCategoryRepository.getSubCategoryCount(addProductDTO.getShopSubcategoryName());
        if (isSubCategory >= 1) {
            Optional<SubCategory> subCategory = objSubCategoryRepository
                    .findByName(addProductDTO.getShopSubcategoryName());
            if (subCategory.isPresent()) {
                product.setSubcategory(subCategory.get());
            }
        } else {
            Optional<ShopSubcategory> shopSubCategory = objShopSubcategoryRepository
                    .findByName(addProductDTO.getShopSubcategoryName());
            if (shopSubCategory.isPresent()) {
                product.setShopSubcategory(shopSubCategory.get());
            }

        }
        objProductRepository.save(product);
        return "Product Added Successfully!!";

    }

    public ShopkeeperDashboardDTO getDashboardData(Long userId) {
        // TODO Auto-generated method stub
        Long shopId = objShopRepository.getShopId(userId);
        ShopkeeperDashboardDTO objShopkeeperDashboardDTO = new ShopkeeperDashboardDTO();
        Integer productCount = objProductRepository.getProductCount(shopId);
        objShopkeeperDashboardDTO.setProductCount(productCount);
        return objShopkeeperDashboardDTO;
    }

    public ShopInventoryDataDTO getAllInvertoryData(ShopInventoryDataDTO objShopInventoryDataDTO, Long userId) {
        try {
            Long shopId = objShopRepository.getShopId(userId);
            List<Product> productList = objProductRepository.findByShop_Id(shopId);
            List<ProductDTO> productDTOList = productList.stream().map(product -> {
                ProductDTO dto = objModelMapper.map(product, ProductDTO.class);
                dto.setShopId(product.getShop().getId());
                if (product.getShopSubcategory() != null) {
                    dto.setShopSubcategoryName(product.getShopSubcategory().getName());
                } else if (product.getSubcategory() != null) {
                    dto.setSubcategoryName(product.getSubcategory().getName());
                }
                return dto;
            }).toList();
            objShopInventoryDataDTO.setProductDTOList(productDTOList);
            return objShopInventoryDataDTO;
        } catch (Exception e) {
            objShopInventoryDataDTO.setErrCode("ERROR");
            objShopInventoryDataDTO.setErrMsg(e.getMessage());
            return objShopInventoryDataDTO;
        }
    }

    public String updateProduct(ProductDTO productDTO, Long userId, MultipartFile productimage) {
        // TODO Auto-generated method stub
        Optional<Product> objProduct = objProductRepository.findById(productDTO.getProductId());
        String link = null;
        if (null != objProduct && objProduct.isPresent()) {
            Product product = objProduct.get();
            product.setCost(productDTO.getCost());
            product.setDescription(productDTO.getDescription());
            product.setIsAvailable(productDTO.getIsAvailable());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            if (productimage != null) {
                link = objR2Service.uploadFile(productimage);
                product.setImageLink(link);
            }
            objProductRepository.save(product);
            return link;
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    public ShopProfileDTO getShopProfile(ShopProfileDTO objShopProfileDTO, Long userId) {
        // TODO Auto-generated method stub
        Optional<User> user = objUserRepository.findById(userId);
        Long shopId = objShopRepository.getShopId(userId);
        Optional<Shop> shop = objShopRepository.findById(shopId);
        UserDTO userDTO = new UserDTO();
        ShopDTO shopDTO = new ShopDTO();
        if (user.isPresent()) {
            userDTO = objModelMapper.map(user.get(), UserDTO.class);
            userDTO.setId(user.get().getId());
        }
        if (shop.isPresent()) {
            Shop shop_ = shop.get();
            shopDTO = objModelMapper.map(shop_, ShopDTO.class);
            if (null != shop_ && shop_.getCategory() != null) {
                shopDTO.setCategoryName(shop_.getCategory().getName());
                shopDTO.setId(shop_.getId());
            }
        }
        objShopProfileDTO.setShopDTO(shopDTO);
        objShopProfileDTO.setUserDTO(userDTO);
        return objShopProfileDTO;
    }

    public String updateShopProfile(HttpServletRequest req, ShopProfileDTO objShopProfileDTO) {
        // TODO Auto-generated method stub
        try {
            Long userId = objNearShopUtility.getUserIdUsingRequest(req);
            Long shopId = objShopRepository.getShopId(userId);

            Optional<User> objUser = objUserRepository.findById(userId);
            Optional<Shop> objShop = objShopRepository.findById(shopId);

            if (objUser.isPresent()) {
                User user = objUser.get();
                user.setEmail(objShopProfileDTO.getUserDTO().getEmail());
                user.setName(objShopProfileDTO.getUserDTO().getName());
                objUserRepository.save((user));
            }

            if (objShop.isPresent()) {
                Shop shop = objShop.get();
                shop.setAddress(objShopProfileDTO.getShopDTO().getAddress());
                Optional<Category> category = objCategoryRepository
                        .findByName(objShopProfileDTO.getShopDTO().getCategoryName());
                if (category.isPresent()) {
                    shop.setCategory(category.get());
                }
                shop.setClosingTime(objShopProfileDTO.getShopDTO().getClosingTime());
                shop.setOpeningTime(objShopProfileDTO.getShopDTO().getOpeningTime());
                shop.setDeliveryRange(objShopProfileDTO.getShopDTO().getDeliveryRange());
                shop.setDescription(objShopProfileDTO.getShopDTO().getDescription());
                shop.setShopName(objShopProfileDTO.getShopDTO().getShopName());
                shop.setProvidesDelivery(objShopProfileDTO.getShopDTO().getProvidesDelivery());
                shop.setIsActive(objShopProfileDTO.getShopDTO().getIsActive());
                objShopRepository.save(shop);
            }
            return "Updated Successfully!!";
        } catch (Exception e) {
            return "Cannot update";
        }
    }

    public NavDTO getNavData(HttpServletRequest req) {
        // TODO Auto-generated method stub
        Long userId = objNearShopUtility.getUserIdUsingRequest(req);
        Long shopId = objShopRepository.getShopId(userId);
        Optional<Shop> shop = objShopRepository.findById(shopId);
        NavDTO objNavDTO = new NavDTO();
        if (shop.isPresent()) {
            objNavDTO.setIsActive(shop.get().getIsActive());
            objNavDTO.setShopName(shop.get().getShopName());
        }
        return objNavDTO;
    }

    public Page<Product> getInventory(Integer page, Integer size, String search, String category ,Long userId) {
        // TODO Auto-generated method stub
        Long shopId = objShopRepository.getShopId(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Optional<ShopSubcategory> shopSubCat = objShopSubcategoryRepository.findByName(category);
        Long cat_id = null;
        if(shopSubCat.isPresent()){
            cat_id = shopSubCat.get().getShopSubCategoryId();
        }
        else{
            Optional<SubCategory> subcat = objSubCategoryRepository.findByName(category);
            if(subcat.isPresent()){
                cat_id = subcat.get().getSubCategoryId();
            }
        }

        return objProductRepository.searchProducts(search,cat_id,shopId, pageable);
    }

     

}
