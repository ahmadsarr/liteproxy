package org.sl.liteproxy.handler;

import org.sl.liteproxy.request.RequestContext;
import org.sl.liteproxy.router.Route;

import java.util.Optional;

public interface ProxyHandler {
    Optional<Route> handle(RequestContext context);
}
