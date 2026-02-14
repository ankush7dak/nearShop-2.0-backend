package com.nearShop.java.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nearShop.java.entity.OtpVerification;
import com.nearShop.java.entity.Role;
import com.nearShop.java.entity.User;
import com.nearShop.java.entity.UserRole;
import com.nearShop.java.repository.OtpRepository;
import com.nearShop.java.repository.RoleRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.repository.UserRoleRepository;
import com.nearShop.java.security.jwt.JwtUtil;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public void sendOtp(String mobile) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
        OtpVerification otpEntity = OtpVerification.builder()
                .mobile(mobile)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();
        otpRepository.save(otpEntity);

        // TODO: Integrate SMS here; for dev, just print
        System.out.println("OTP for " + mobile + " : " + otp);
    }

    public String verifyOtp(String mobile, String otp,String password,String role_selected) {
        OtpVerification otpEntity = otpRepository.findTopByMobileAndOtpOrderByIdDesc(mobile, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpEntity.isVerified()) throw new RuntimeException("OTP already used");
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        // Create user if not exists
        User user = userRepository.findByMobile(mobile).orElseGet(() -> {
            User newUser = User.builder()
                    .mobile(mobile)
                    .isMobileVerified(true)
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .password(password)
                    .build();
            newUser = userRepository.save(newUser);

            // Assign default role (customer)
            Role role = roleRepository.findByName(role_selected)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            userRoleRepository.save(UserRole.builder().user(newUser).role(role).build());

            return newUser;
        });

        // Generate JWT
        return jwtUtil.generateToken(user.getMobile(), "CUSTOMER");
    }
}
