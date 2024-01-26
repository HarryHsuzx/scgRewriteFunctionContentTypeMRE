package com.demo;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class SecureHttpPostSignRewriteFunction implements RewriteFunction<String, String> {

    private static final Logger logger = LoggerFactory.getLogger(SecureHttpPostSignGatewayFilterFactory.class);
    private final SecureHttpPostSignGatewayFilterFactory.Config config;

    public SecureHttpPostSignRewriteFunction(
            SecureHttpPostSignGatewayFilterFactory.Config config) {
        this.config = config;
    }

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        // Escape if not POST
        if (exchange.getRequest().getMethod() != HttpMethod.POST) {
            logger.warn("MONO.EMPTY returned!");
            return Mono.empty();
        }

        //Escape if no body
        if (!StringUtils.hasText(body))
            return Mono.empty();

        // ContentType==JSON
        if (Objects.equals(exchange.getRequest().getHeaders().getContentType(), MediaType.APPLICATION_JSON)) {
            return handleJSON(exchange, body);
        }
        return Mono.just(body);
    }

    private Publisher<String> handleJSON(ServerWebExchange exchange, String body) {
        // does some business logic with config and body
        return Mono.just(body + "edited");
    }
}
