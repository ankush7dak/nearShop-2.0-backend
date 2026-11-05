
package com.nearShop.java.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nearShop.java.auth.controller.AuthController;
import com.nearShop.java.auth.service.AuthService;
import com.nearShop.java.entity.Role;
import com.nearShop.java.entity.User;
import com.nearShop.java.entity.UserRole;
import com.nearShop.java.repository.RoleRepository;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.repository.UserRoleRepository;
import com.nearShop.java.security.jwt.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private GoogleIdTokenVerifier verifier;
    
    @Autowired
    UserRepository objUserRepository;
    @Autowired 
    AuthService authService;
    @Autowired
    RoleRepository objRoleRepository;
    @Autowired
    UserRoleRepository objUserRoleRepository;
    @Autowired
    JwtUtil jwtUtil;

    @PostConstruct
    public void initialize() throws GeneralSecurityException, IOException {

        verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance()
        )
        .setAudience(Collections.singletonList(clientId))
        .build();
    }

    public GoogleIdToken.Payload verifyToken(String token,String loginRole,HttpServletResponse response)
            throws GeneralSecurityException, IOException {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Google token is missing");
        }

        GoogleIdToken idToken = verifier.verify(token);

        if (idToken == null) {
            throw new IllegalArgumentException(
                    "Invalid or expired Google token"
            );
        }

        GoogleIdToken.Payload payload = idToken.getPayload();



        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new IllegalArgumentException(
                    "Google email is not verified"
            );
        }
        User user = objUserRepository.findByEmail(payload.getEmail()).orElseGet(() -> {
            logger.info("Creating new user for email: {}", payload.getEmail());

            User newUser = User.builder()
                    .email(payload.getEmail())
                    //.isMobileVerified(true)
                    .status((loginRole == "shopkeeper") ? "PENDING" : "ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .authProvider("Google")
                    .googleId(payload.getSubject())
                    //.email(objSignUpRequestDTO.getEmail())
                    .name(payload.get("name").toString())
                    .build();

            newUser = objUserRepository.save(newUser);
            logger.info("User saved with id: {}", newUser.getId());

            // Assign role
            Role role = objRoleRepository.findByName(loginRole)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + loginRole));
            objUserRoleRepository.save(UserRole.builder().user(newUser).role(role).build());
            logger.info("Role '{}' assigned to user id: {}", loginRole, newUser.getId());

            return newUser;
        });
        // String token2 = jwtUtil.generateToken(payload.getEmail(), loginRole, user.getId());

        // ResponseCookie cookie = authService.createCookie(token2);
        //         response.addHeader(
        //                 HttpHeaders.SET_COOKIE,
        //                 cookie.toString());
        //         logger.info("Login successful for email in googleAuthService: {}", payload.getEmail());
        payload.setUserId(user.getId().toString());

        return payload;
    }
}
