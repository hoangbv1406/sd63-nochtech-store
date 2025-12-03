package com.project.shopapp.shared.filters;

import com.project.shopapp.shared.utils.WebUtils;
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

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String clientIp = WebUtils.getClientIp(request);

            if (duration > 2000) {
                log.warn("Slow Request: method={}, uri={}, status={}, ip={}, duration={}ms", method, requestUri, status, clientIp, duration);
            } else if (status >= 400) {
                log.error("Failed Request: method={}, uri={}, status={}, ip={}, duration={}ms", method, requestUri, status, clientIp, duration);
            }
        }
    }

    private boolean isIgnoredUri(String uri) {
        return uri.startsWith("/api-docs") || uri.startsWith("/swagger-ui") || uri.startsWith("/uploads") || uri.startsWith("/healthcheck") || uri.startsWith("/actuator");
    }

}
