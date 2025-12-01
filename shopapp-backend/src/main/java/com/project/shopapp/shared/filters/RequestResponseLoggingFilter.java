package com.project.shopapp.shared.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (isIgnoredUri(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String remoteAddr = getClientIp(request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (duration > 2000) {
                log.warn("Slow Request: method={}, uri={}, status={}, ip={}, duration={}ms", method, requestUri, status, remoteAddr, duration);
            } else {
                log.info("Request Completed: method={}, uri={}, status={}, ip={}, duration={}ms", method, requestUri, status, remoteAddr, duration);
            }
        }
    }

    private boolean isIgnoredUri(String uri) {
        return uri.startsWith("/api-docs") || uri.startsWith("/swagger-ui") || uri.startsWith("/uploads") || uri.startsWith("/healthcheck") || uri.startsWith("/actuator");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
