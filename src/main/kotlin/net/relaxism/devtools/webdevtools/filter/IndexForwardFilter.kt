package net.relaxism.devtools.webdevtools.filter

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class IndexForwardFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.uri.path == "/") {
            val serverHttpRequest = exchange.request.mutate().path("/index.html").build()
            val mutatedExchange = exchange.mutate().request(serverHttpRequest).build()
            return chain.filter(mutatedExchange)
        }

        return chain.filter(exchange)
    }

}
