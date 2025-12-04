package com.project.shopapp.domains.iam.security;

import com.project.shopapp.domains.iam.security.JwtTokenFilter;
import com.project.shopapp.domains.iam.security.CustomAccessDeniedHandler;
import com.project.shopapp.domains.iam.security.CustomAuthenticationEntryPoint;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests
                            // 1. PUBLIC ENDPOINTS
                            .requestMatchers(
                                    "/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
                                    "/uploads/**",
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/auth/social-login", apiPrefix),
                                    String.format("%s/healthcheck/**", apiPrefix),
                                    String.format("%s/actuator/**", apiPrefix),
                                    String.format("%s/payment/**", apiPrefix)
                            ).permitAll()

                            // 2. PUBLIC GET
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/roles/**", apiPrefix),
                                    String.format("%s/categories/**", apiPrefix),
                                    String.format("%s/products/**", apiPrefix),
                                    String.format("%s/products/images/*", apiPrefix),
                                    String.format("%s/comments/**", apiPrefix)
                            ).permitAll()

                            // 3. USER & ADMIN
                            .requestMatchers(
                                    String.format("%s/orders/**", apiPrefix),
                                    String.format("%s/order_details/**", apiPrefix),
                                    String.format("%s/cart/**", apiPrefix),
                                    String.format("%s/user-addresses/**", apiPrefix)
                            ).hasAnyRole(Role.USER, Role.ADMIN)

                            // 4. ADMIN ONLY
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
                                    String.format("%s/products/**", apiPrefix)
                            ).hasRole(Role.ADMIN)

                            // 5. CÁC REQUEST CÒN LẠI BẮT BUỘC ĐĂNG NHẬP
                            .anyRequest().authenticated();
                });
        return http.build();
    }
}
