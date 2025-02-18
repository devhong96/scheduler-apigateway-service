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

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

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
                log.info("Global Filter URL (Incoming Request): {}", request.getURI().getPath());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                URI routedUrl = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
                log.info("Gateway Routed To: {}", routedUrl);

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
