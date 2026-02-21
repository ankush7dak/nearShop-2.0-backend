package com.nearShop.java.auth.service;
import com.nearShop.java.auth.dto.LoginRequest;
import com.nearShop.java.entity.User;
import com.nearShop.java.repository.UserRepository;
import com.nearShop.java.security.jwt.JwtUtil;
import com.nearShop.java.utilities.NearShopUtility;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private NearShopUtility objNearShopUtility;

    /**
     * Login user and generate JWT token
     */
    public String login(LoginRequest request) {
        logger.info("Login attempt for mobile: {}", request.getMobile());

        Optional<User> optionalUser = userRepository.findByMobile(request.getMobile());
        if (optionalUser.isEmpty()) {
            logger.warn("Login failed for mobile {}: user not found", request.getMobile());
            throw new RuntimeException("User not found");
        }
        //user fetched
        User user = optionalUser.get();
        // Check password (replace with BCrypt check in production)
        if (!request.getPassword().equals(user.getPassword())) {
            logger.warn("Login failed for mobile {}: invalid password", request.getMobile());
            throw new RuntimeException("Invalid password");
        }

        List<String> userRoles = objNearShopUtility.getUserRoles(user.getMobile());
        String roleName = "";
        if(!userRoles.isEmpty() && userRoles.contains(request.getLoginRole())){
            roleName = request.getLoginRole();
        }
        else{
            logger.warn("Login failed for mobile {}: role not assigned", request.getMobile());
            throw new RuntimeException("Role not assigned");
        }
        //getting user id
        Long userId = user.getId();

        String token = jwtUtil.generateToken(user.getMobile(), roleName, userId);
        logger.info("Login successful for mobile {} with role {}", user.getMobile(), roleName);

        return token;
    }

    public Cookie createCookie(String token){
        Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            return cookie;
    }
}
