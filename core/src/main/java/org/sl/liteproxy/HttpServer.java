package org.sl.liteproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.RequiredArgsConstructor;
import org.sl.liteproxy.handler.ClientHttp;
import org.sl.liteproxy.handler.ClientRequestHandler;
import org.sl.liteproxy.handler.ProxyHandlerImpl;
import org.sl.liteproxy.router.RouteEngine;

@RequiredArgsConstructor
public class HttpServer {
    private final int nbThreads;
    private final int port;
    private final RouteEngine routeEngine ;
    private final ClientHttp nettyBackendClient ;

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup(nbThreads);
        final ProxyHandlerImpl proxyHandlerImpl = new ProxyHandlerImpl(routeEngine);
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.channel(NioServerSocketChannel.class)
                    .group(group)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new ClientRequestHandler(proxyHandlerImpl,nettyBackendClient));

                        }
                    });

            ChannelFuture f = sb.bind(port).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
