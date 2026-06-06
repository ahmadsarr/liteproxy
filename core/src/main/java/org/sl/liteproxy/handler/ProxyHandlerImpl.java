package org.sl.liteproxy.handler;

import lombok.RequiredArgsConstructor;
import org.sl.liteproxy.request.RequestContext;
import org.sl.liteproxy.router.Route;
import org.sl.liteproxy.router.RouteEngine;

import java.util.Optional;
@RequiredArgsConstructor
public class ProxyHandlerImpl implements ProxyHandler{
    private final RouteEngine routeEngine;
    @Override
    public Optional<Route> handle(RequestContext context) {
        return routeEngine.match(context);
    }
}
