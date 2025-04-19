package com.virtual.herbal.garden.Virtual_Herbs_Garden.security.oauth;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CustomOAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");
            String picture = oauthUser.getAttribute("picture");

            if (email == null || name == null) {
                throw new RuntimeException("Missing email or name from OAuth provider");
            }

            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                // ✅ Existing user – generate JWT and return it
                String jwt = jwtUtil.generateToken(email);

                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Existing user logged in\", \"token\": \"" + jwt + "\"}");

                // For production:
                // String redirectUrl = "http://your-frontend/home?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);
                // response.sendRedirect(redirectUrl);
            } else {
                // ✅ New user – generate temp token with extra claims (email, name, picture)
                String tempToken = jwtUtil.generateTokenWithClaims(email, name, picture);

                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"New user, complete signup\", \"tempToken\": \"" + tempToken + "\"}");

                // For production:
                // String redirectUrl = "http://your-frontend/complete-signup?token=" + URLEncoder.encode(tempToken, StandardCharsets.UTF_8);
                // response.sendRedirect(redirectUrl);
            }

        } catch (Exception e) {
            // ⚠️ Fallback handling in case something fails
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"OAuth2 authentication failed: " + e.getMessage() + "\"}");

            // Optional: log the error
            e.printStackTrace();
        }
    }
}
