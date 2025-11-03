package com.epam.crm.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionLoggingFilter implements Filter {
    public static final String TX = "X-Transaction-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        String tx = Optional.ofNullable(req.getHeader(TX)).filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());
        MDC.put("transactionId", tx);
        try {
            log.info("rest_in tx={} {} {}", tx, req.getMethod(), req.getRequestURI());
            chain.doFilter(request, response);
            log.info("rest_out tx={} status={}", tx, res.getStatus());
        } finally {
            res.setHeader(TX, tx);
            MDC.remove("transactionId");
        }
    }
}
