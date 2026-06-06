package org.sl.liteproxy.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.sl.liteproxy.request.RequestContext;
import org.sl.liteproxy.router.Route;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class ClientRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ProxyHandlerImpl proxyHandlerImpl;
    private final ClientHttp clientHttp;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        HttpMethod method = req.method();
        String uri = req.uri();
        String host = req.headers().get(HttpHeaderNames.HOST);

        QueryStringDecoder decoder = new QueryStringDecoder(uri);

        String path = decoder.path();
        Map<String, List<String>> params = decoder.parameters();

        RequestContext context = RequestContext.builder()
                .queryParams(params)
                .method(method)
                .host(host)
                .path(path)
                .build();

        Optional<Route> maybeRoute = proxyHandlerImpl.handle(context);

        if (maybeRoute.isEmpty()) {
            send404(ctx);
            return;
        }

        Route route = maybeRoute.get();

        String query = uri.contains("?") ? uri.substring(uri.indexOf("?")) : "";

        URI url = URI.create(route.target() + path + query);

        FullHttpRequest safeRequest = new DefaultFullHttpRequest(
                req.protocolVersion(),
                req.method(),
                url.toString(),
                req.content().retain()
        );

        safeRequest.headers().set(req.headers());

        clientHttp.send(safeRequest, url)
                .thenAccept(resp -> ctx.writeAndFlush(resp)
                        .addListener(ChannelFutureListener.CLOSE))
                .exceptionally(ex -> {
                    send500(ctx);
                    return null;
                });
    }
    private void send404(ChannelHandlerContext ctx) {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes());

        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
    private void send500(ChannelHandlerContext ctx) {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes());

        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer("Internal Server Error", CharsetUtil.UTF_8)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes());

        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

}
