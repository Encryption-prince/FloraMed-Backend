package com.virtual.herbal.garden.Virtual_Herbs_Garden.config;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtAuthenticationFilter;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.oauth.CustomOAuth2SuccessHandler;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.TokenBacklistService;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserService userService;
    private final TokenBacklistService tokenBacklistService;


//    public SecurityConfig(JwtUtil jwtUtil,
//                          CustomOAuth2SuccessHandler oAuth2SuccessHandler,
//                          UserService userService) {
//        this.jwtUtil = jwtUtil;
//        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
//        this.userService = userService;
//    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenBacklistService tokenBacklistService) {
        return new JwtAuthenticationFilter(jwtUtil, userService, tokenBacklistService);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**","/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html","/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/plants/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/plants/filter").permitAll()
                        .requestMatchers(HttpMethod.POST, "/plants/add")
                        .hasAuthority("ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.POST, "/bookmarks/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.DELETE, "/bookmarks/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.GET, "/bookmarks/my")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.GET, "/blogs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/blogs/add")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.PUT, "/blogs/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/blogs/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/profile")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")
                        .requestMatchers(HttpMethod.GET,  "/comments/by-blog/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/comments/add")
                        .hasAnyAuthority("ROLE_USER", "ROLE_HERBALIST")                               // ← must be logged in
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/feedback/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/feedback/submit").permitAll()
                        .requestMatchers(HttpMethod.POST, "/purchases/**/internal-buy").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, ex2) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Inject both JwtUtil and UserService into the filter
//        http.addFilterBefore(
//                new JwtAuthenticationFilter(jwtUtil, userService),
//                UsernamePasswordAuthenticationFilter.class
//        );
        http.addFilterBefore(jwtAuthenticationFilter(tokenBacklistService), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Specify allowed origins explicitly
//        configuration.setAllowedOriginPatterns(List.of(
//                "https://virtualherbalgarden.com",
//                "https://app.virtualherbalgarden.com",
//                "http://localhost:3000"
//        ));
        // Specify allowed origins explicitly
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173", "https://virtual-herbal-garden-frontend.vercel.app"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition", "X-Total-Count", "Location", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

