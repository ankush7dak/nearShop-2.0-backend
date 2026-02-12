package com.nearShop.java.auth.service;

import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.security.jwt.JwtUtil;
import com.nearShop.java.user.entity.User;
// import com.nearShop.java.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // User user;

    public String login(LoginRequest request) {

        // User user = userRepository.findByMobile(request.getMobile())
        //         .orElseThrow(() -> new RuntimeException("User not found"));

        // if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        //     throw new RuntimeException("Invalid password");
        // }

        // return jwtUtil.generateToken(user.getMobile(), user.getRole());

        return jwtUtil.generateToken("7700856845", "shopkeeper");

    }
}
