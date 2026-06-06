package org.sl.liteproxy.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public interface ClientHttp {
    CompletableFuture<FullHttpResponse> send(FullHttpRequest request, URI uri);
}
