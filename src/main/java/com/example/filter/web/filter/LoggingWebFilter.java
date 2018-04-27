package com.example.filter.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Created by ADM on 15/04/2018.
 */
public class LoggingWebFilter implements WebFilter {
    private final Logger log = LoggerFactory.getLogger(LoggingWebFilter.class);

    public LoggingWebFilter() {
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(decorate(exchange));
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {

        final ServerHttpRequest decoratedRequest = new LoggingServerHttpRequestDecorator(
                exchange.getRequest(),
                exchange.getResponse()
        );

        final ServerHttpResponse decoratedResponse = new LoggingServerHttpResponseDecorator(
                exchange.getResponse(),
                exchange.getRequest()
        );

        return new ServerWebExchangeDecorator(exchange) {

            @Override
            public ServerHttpRequest getRequest() {
                return decoratedRequest;
            }

            @Override
            public ServerHttpResponse getResponse() {
                return decoratedResponse;
            }

        };
    }
}
