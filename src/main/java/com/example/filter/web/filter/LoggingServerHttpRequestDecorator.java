package com.example.filter.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;

import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

class LoggingServerHttpRequestDecorator extends ServerHttpRequestDecorator implements WithMemoizingFunction {

    private final Logger log = LoggerFactory.getLogger(LoggingServerHttpRequestDecorator.class);
    private final Flux<DataBuffer> decoratedBody;
    private final ServerHttpResponse response;
    private ByteArrayOutputStream baos;

    LoggingServerHttpRequestDecorator(ServerHttpRequest delegate, ServerHttpResponse response) {
        super(delegate);
        this.decoratedBody = decorateBody(delegate.getBody());
        this.response = response;
        if(delegate.getMethod().toString().equals("GET"))
            flushLog(EMPTY_BYTE_ARRAY_OUTPUT_STREAM, true);
    }

    private Flux<DataBuffer> decorateBody(Flux<DataBuffer> body) {
        baos = new ByteArrayOutputStream();
        return body.map(memoizingFunction(baos)).doOnComplete(() -> flushLog(baos, false));
    }

    @PreDestroy
    public void destroy(){
        if(baos==null)
            flushLog(EMPTY_BYTE_ARRAY_OUTPUT_STREAM, true);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.decoratedBody;
    }

    private void flushLog(ByteArrayOutputStream baos, boolean onCreate) {
        log.debug(format(getDelegate(), this.response, baos.toByteArray()));
     }

    @Override
    public Logger getLogger() {
        return this.log;
    }

    public String format(ServerHttpRequest request, ServerHttpResponse response, byte[] payload) {
        StringBuilder data = new StringBuilder();
        data.append("Request [").append(request.getMethodValue())
                .append("] '").append(String.valueOf(request.getURI()))
                .append("' from ")
                .append(
                        Optional.ofNullable(request.getRemoteAddress())
                                .map(addr -> addr.getHostString())
                                .orElse("null")
                );

        request.getHeaders().forEach((key, value) -> data.append('\n').append(key).append('=').append(String.valueOf(value)));
        data.append("\n[\n");
        if(payload!=null)
            data.append(new String(payload));
        else
            data.append("No body");
        data.append("\n]");

        return data.toString();
    }

}