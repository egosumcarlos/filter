package com.example.filter.web.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

class LoggingServerHttpResponseDecorator extends ServerHttpResponseDecorator implements WithMemoizingFunction {

    private final Logger log = LoggerFactory.getLogger(LoggingServerHttpResponseDecorator.class);
    private final ByteArrayOutputStream baos;
    private final ServerHttpRequest request;

    LoggingServerHttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request) {
        super(delegate);
        this.request = request;
        baos = new ByteArrayOutputStream();
/*        delegate.beforeCommit(() -> {
            flushLog(baos);
            return Mono.empty();
        });*/
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.from(body).map(memoizingFunction(baos))).doOnTerminate(()->{flushLog(baos);});
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return super.writeAndFlushWith(Flux.from(body).map( x -> {
                return Flux.from(x).map(memoizingFunction(baos));
            } )).doOnTerminate(()->{flushLog(baos);});
    }

    private void flushLog(ByteArrayOutputStream baos) {
        log.debug(format(this.request, getDelegate(), baos.toByteArray()));
    }

    @Override
    public Logger getLogger() {
        return this.log;
    }

    public String format(ServerHttpRequest request, ServerHttpResponse response, byte[] payload) {
        final StringBuilder data = new StringBuilder();
        data.append("Response [")
                .append(Optional.ofNullable(response.getStatusCode()).orElse(HttpStatus.OK))
                .append("] for [").append(request.getMethodValue())
                .append("] '").append(String.valueOf(request.getURI()))
                .append("' from ")
                .append(
                        Optional.ofNullable(request.getRemoteAddress())
                                .map( addr -> addr.getHostString() )
                                .orElse("null")
                );
        if (payload != null) {
            response.getHeaders().forEach((key, value) -> data.append('\n').append(key).append('=').append(String.valueOf(value)));
            data.append("\n[\n");
            data.append(new String(payload));
            data.append("\n]");
        }
        return data.toString();
    }
}