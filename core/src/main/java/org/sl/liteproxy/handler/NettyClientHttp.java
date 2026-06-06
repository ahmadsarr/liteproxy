package org.sl.liteproxy.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import lombok.*;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
@Setter
public class NettyClientHttp implements ClientHttp {
    private Duration readTimeout = Duration.ofSeconds(2);
    private Duration connectionTimeout = Duration.ofSeconds(1);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap;
    public final AttributeKey<CompletableFuture<FullHttpResponse>> KEY = AttributeKey.valueOf("response");
    public NettyClientHttp() {
        this.bootstrap = init();
    }

    private Bootstrap init() {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {

                        ChannelPipeline p = ch.pipeline();

                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(1_000_000));
                        p.addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
                                CompletableFuture<FullHttpResponse> future =
                                        ctx.channel().attr(KEY).get();

                                if (future != null && !future.isDone()) {
                                    FullHttpResponse copy = new DefaultFullHttpResponse(
                                            msg.protocolVersion(),
                                            msg.status(),
                                            msg.content().retain()
                                    );
                                    copy.headers().set(msg.headers());
                                    future.complete(copy);
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

                                CompletableFuture<FullHttpResponse> future =
                                        ctx.channel().attr(KEY).get();

                                if (future != null && !future.isDone()) {
                                    future.completeExceptionally(cause);
                                }
                            }
                        });
                    }
                });

        return b;
    }

    @Override
    public CompletableFuture<FullHttpResponse> send(FullHttpRequest request, URI uri) {
        CompletableFuture<FullHttpResponse> future = new CompletableFuture<>();
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 80 : uri.getPort();
        bootstrap.connect(host, port).addListener((ChannelFutureListener) cf -> {
            if (!cf.isSuccess()) {
                future.completeExceptionally(cf.cause());
                return;
            }
            Channel channel = cf.channel();
            channel.attr(KEY).set(future);
            String path = uri.getRawPath() +
                    (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
            FullHttpRequest safeRequest = new DefaultFullHttpRequest(
                    request.protocolVersion(),
                    request.method(),
                    path,
                    request.content().retain()
            );
            safeRequest.headers().set(HttpHeaderNames.HOST, host);
            safeRequest.headers().set(request.headers());
            channel.writeAndFlush(safeRequest).addListener(f -> {
                if (!f.isSuccess()) {
                    future.completeExceptionally(f.cause());
                }
            });
        });

        return future;
    }



}
