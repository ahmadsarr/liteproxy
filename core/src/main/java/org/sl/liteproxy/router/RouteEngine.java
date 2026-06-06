package org.sl.liteproxy.router;

import org.sl.liteproxy.request.RequestContext;

import java.util.Optional;

public interface RouteEngine {
    Optional<Route> match(RequestContext context);
    RouteEngine add(Route route);
}
