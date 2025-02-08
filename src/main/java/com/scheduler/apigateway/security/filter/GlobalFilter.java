package com.scheduler.apigateway.security.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            if (config.preLogger) {
                log.info("Global Filter URL: {}", request.getURI().getPath());
            }

            // 커스텀 필터가 여기서 작동
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.postLogger) {
                    log.info("Global Filter End: requestId -> {}", response.getStatusCode());
                }
                log.info("Custom Post filter: response code -> {}", response.getStatusCode());
            }));
        });
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
        private Boolean preLogger;
        private Boolean postLogger;
    }
}
