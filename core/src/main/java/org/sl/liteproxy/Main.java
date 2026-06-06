package org.sl.liteproxy;

import org.sl.liteproxy.handler.NettyClientHttp;
import org.sl.liteproxy.router.*;

import static org.sl.liteproxy.router.PathMatcher.*;

public class Main {
    public static void main(String[] args) {
        final RouteEngine routeEngine = new RouteEngineImpl();
        final NettyClientHttp client = new NettyClientHttp();
        routeEngine.add(Route.builder().matcher(exact("/user/12")).target("http://localhost:8000").build())
                .add(Route.builder().matcher(regex("/.*")).target("http://localhost:8000").build());
        HttpServer httpServer = new HttpServer(1, 8080, routeEngine, client);
        httpServer.start();
    }
}
