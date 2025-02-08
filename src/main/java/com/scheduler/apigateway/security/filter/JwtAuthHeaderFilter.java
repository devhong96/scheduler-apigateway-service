package com.scheduler.apigateway.security.filter;

import com.scheduler.apigateway.security.component.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class JwtAuthHeaderFilter extends AbstractGatewayFilterFactory<JwtAuthHeaderFilter.Config> {

    private final JwtUtils jwtUtils;

    public JwtAuthHeaderFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String accessToken = request.getHeaders().getFirst(AUTHORIZATION);

            if (accessToken == null) {
                return chain.filter(exchange);
            }

            if (!accessToken.startsWith("Bearer ")) {
                log.info("start with Bearer");
                response.setStatusCode(UNAUTHORIZED);
                return response.setComplete();
            }

            accessToken = accessToken.replace("Bearer ", "").trim();

            try {
                jwtUtils.verifyToken(accessToken);
            } catch (JwtException e) {
                log.info("verify");
                response.setStatusCode(UNAUTHORIZED);
                return writeErrorResponse(response, UNAUTHORIZED, "Invalid token: " + e.getMessage());
            }

            try {
                jwtUtils.isExpired(accessToken);
            } catch (Exception e) {
                log.info("isExpired");
                response.setStatusCode(UNAUTHORIZED);
                return writeErrorResponse(response, UNAUTHORIZED, "Expired token: " + e.getMessage());
            }

            String category = jwtUtils.getCategory(accessToken);
            if (category == null || !category.equals("access")) {
                log.info("category");
                response.setStatusCode(BAD_REQUEST);
                return writeErrorResponse(response, BAD_REQUEST, "Invalid category: not access or category is null");
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> writeErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.getHeaders().setContentType(APPLICATION_JSON);
        response.setStatusCode(status);

        DataBuffer buffer = response.bufferFactory().wrap(
                ("{\"error\":\"" + message + "\"}").getBytes(UTF_8)
        );
        return response.writeWith(Mono.just(buffer));
    }
}