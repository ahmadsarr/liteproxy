package org.sl.liteproxy.router;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LeastConnectionsStrategy implements LoadBalancerStrategy {
    private final Map<String, AtomicInteger> connections = new ConcurrentHashMap<>();

    @Override
    public String select(List<String> targets) {
        String target = targets.stream()
                .min(Comparator.comparingInt(t ->
                        connections.getOrDefault(t, new AtomicInteger(0)).get()))
                .orElse(null);
        if (target == null)
            return null;
        connections
                .computeIfAbsent(target, t -> new AtomicInteger(0))
                .incrementAndGet();
        return target;
    }
}
