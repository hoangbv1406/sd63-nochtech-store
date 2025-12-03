package com.project.shopapp.domains.iam.filter;

import com.project.shopapp.domains.iam.components.JwtTokenUtils;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);

            if (Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST_" + token))) {
                log.warn("Attempt to use a revoked token.");
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenUtil.isTokenExpired(token)) {
                String subject = jwtTokenUtil.getSubject(token);
                Long userId = jwtTokenUtil.extractClaim(token, claims -> claims.get("userId", Long.class));
                String roleName = jwtTokenUtil.extractClaim(token, claims -> claims.get("role", String.class));

                User dummyUser = User.builder()
                        .id(userId)
                        .phoneNumber(subject)
                        .active(true)
                        .role(Role.builder().name(roleName).build())
                        .build();

                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase())
                );

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        dummyUser,
                        null,
                        authorities
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
