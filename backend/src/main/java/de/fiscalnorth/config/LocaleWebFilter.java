package de.fiscalnorth.config;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.i18n.LocaleContextResolver;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleWebFilter implements WebFilter {

    private final LocaleContextResolver localeContextResolver;

    public LocaleWebFilter(LocaleContextResolver localeContextResolver) {
        this.localeContextResolver = localeContextResolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var localeContext = localeContextResolver.resolveLocaleContext(exchange);
        LocaleContextHolder.setLocaleContext(localeContext);
        return chain.filter(exchange)
                .doFinally(signalType -> LocaleContextHolder.resetLocaleContext());
    }
}
