package org.sl.liteproxy.router;

import org.sl.liteproxy.request.RequestContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RouteEngineImpl implements RouteEngine {
    private final List<Route> routes;

    public RouteEngineImpl() {
        this.routes = new ArrayList<>();
    }

    public RouteEngineImpl(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public Optional<Route> match(RequestContext context) {
        return this.routes.stream()
                .filter(p -> p.match(context))
                .min(Comparator.comparingInt(Route::priority));
    }

    @Override
    public RouteEngine add(Route route) {
        this.routes.add(route);
        return this;
    }
}
