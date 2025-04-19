package com.virtual.herbal.garden.Virtual_Herbs_Garden.config;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtAuthenticationFilter;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.oauth.CustomOAuth2SuccessHandler;
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


@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserService userService;

    public SecurityConfig(JwtUtil jwtUtil,
                          CustomOAuth2SuccessHandler oAuth2SuccessHandler,
                          UserService userService) {
        this.jwtUtil = jwtUtil;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**").permitAll()
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
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil, userService),
                UsernamePasswordAuthenticationFilter.class
        );

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


}

