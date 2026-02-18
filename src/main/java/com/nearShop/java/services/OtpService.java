package com.nearShop.java.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nearShop.java.auth.dto.response.LoginResponse;
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

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

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

    // ---------- Send OTP ----------
    public String sendOtp(String mobile, String roleSelected) {
        logger.info("Sending OTP for mobile: {}, role: {}", mobile, roleSelected);

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit OTP
        Optional<User> user = userRepository.findByMobile(mobile);

        if (user.isPresent()) {
            logger.warn("User already exists for mobile: {}", mobile);
            return "User Already Exists";
        }

        OtpVerification otpEntity = OtpVerification.builder()
                .mobile(mobile)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        otpRepository.save(otpEntity);

        logger.info("OTP saved to DB for mobile: {} - OTP: {}", mobile, otp);
        System.out.println("OTP for " + mobile + " : " + otp); // temporary dev print

        return "OTP Sent Successfully";
    }

    // ---------- Verify OTP ----------
    public boolean verifyOtp(String mobile, String otp, String password, String roleSelected) {
        logger.info("Verifying OTP for mobile: {}, role: {}", mobile, roleSelected);

        OtpVerification otpEntity = otpRepository.findTopByMobileAndOtpOrderByIdDesc(mobile, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpEntity.isVerified()) {
            logger.warn("OTP already used for mobile: {}", mobile);
            throw new RuntimeException("OTP already used");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("OTP expired for mobile: {}", mobile);
            return false;
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
        logger.info("OTP marked as verified for mobile: {}", mobile);

        // ---------- Create or fetch user ----------
        User user = userRepository.findByMobile(mobile).orElseGet(() -> {
            logger.info("Creating new user for mobile: {}", mobile);

            User newUser = User.builder()
                    .mobile(mobile)
                    .isMobileVerified(true)
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .password(password)
                    .build();

            newUser = userRepository.save(newUser);
            logger.info("User saved with id: {}", newUser.getId());

            // Assign role
            Role role = roleRepository.findByName(roleSelected)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleSelected));
            userRoleRepository.save(UserRole.builder().user(newUser).role(role).build());
            logger.info("Role '{}' assigned to user id: {}", roleSelected, newUser.getId());

            return newUser;
        });

        // ---------- Generate JWT ----------
        return true;
    }
}
