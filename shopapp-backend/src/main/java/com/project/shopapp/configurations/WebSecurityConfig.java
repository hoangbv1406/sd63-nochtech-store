package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests
                            // --- PUBLIC ENDPOINTS (Không cần token) ---
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/users/login/social", apiPrefix),
                                    String.format("%s/health-check/**", apiPrefix),
                                    String.format("%s/actuator/**", apiPrefix),
                                    String.format("%s/policies/**", apiPrefix)
                            ).permitAll()

                            // Các API GET public (Sản phẩm, Danh mục, Thương hiệu, Địa chỉ)
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/roles**", apiPrefix),
                                    String.format("%s/categories/**", apiPrefix),
                                    String.format("%s/products/**", apiPrefix),
                                    String.format("%s/products/images/*", apiPrefix),
                                    String.format("%s/brands/**", apiPrefix),
                                    String.format("%s/comments/**", apiPrefix),
                                    String.format("%s/reviews/**", apiPrefix),
                                    String.format("%s/address/**", apiPrefix),
                                    String.format("%s/coupons/calculate", apiPrefix)
                            ).permitAll()

                            // --- USER & ADMIN (Cần đăng nhập) ---
                            .requestMatchers(
                                    String.format("%s/users/details/**", apiPrefix),
                                    String.format("%s/users/profile-images/**", apiPrefix),
                                    String.format("%s/cart/**", apiPrefix),            // Giỏ hàng
                                    String.format("%s/orders/**", apiPrefix),          // Đơn hàng
                                    String.format("%s/order-details/**", apiPrefix),   // Chi tiết đơn
                                    String.format("%s/user-addresses/**", apiPrefix),  // Sổ địa chỉ
                                    String.format("%s/warranties/**", apiPrefix),      // Bảo hành
                                    String.format("%s/payments/**", apiPrefix),        // Thanh toán
                                    String.format("%s/comments/**", apiPrefix),        // Comment (POST/PUT)
                                    String.format("%s/reviews/**", apiPrefix)          // Review (POST/PUT)
                            ).hasAnyRole(Role.USER, Role.ADMIN)

                            // --- ADMIN ONLY (Chỉ Admin mới được tác động dữ liệu hệ thống) ---
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/categories/**", apiPrefix),
                                    String.format("%s/brands/**", apiPrefix),
                                    String.format("%s/suppliers/**", apiPrefix),
                                    String.format("%s/products/**", apiPrefix)
                            ).hasRole(Role.ADMIN)

                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/categories/**", apiPrefix),
                                    String.format("%s/brands/**", apiPrefix),
                                    String.format("%s/suppliers/**", apiPrefix),
                                    String.format("%s/products/**", apiPrefix)
                            ).hasRole(Role.ADMIN)

                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/categories/**", apiPrefix),
                                    String.format("%s/brands/**", apiPrefix),
                                    String.format("%s/suppliers/**", apiPrefix),
                                    String.format("%s/products/**", apiPrefix),
                                    String.format("%s/product-images/**", apiPrefix)
                            ).hasRole(Role.ADMIN)

                            // Các request khác bắt buộc phải xác thực
                            .anyRequest().authenticated();
                });
        return http.build();
    }

}
