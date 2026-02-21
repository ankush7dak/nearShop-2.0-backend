package com.nearShop.java.utilities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nearShop.java.repository.RoleRepository;

@Component
public class NearShopUtility {
    @Autowired
    RoleRepository objRoleRepository;

    public List<String> getUserRoles(String mobile) {
        // TODO Auto-generated method stub
        return objRoleRepository.findRoleNamesByMobile(mobile);

    }
}
