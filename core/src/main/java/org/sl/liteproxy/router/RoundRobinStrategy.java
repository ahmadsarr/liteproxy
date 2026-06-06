package org.sl.liteproxy.router;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancerStrategy {
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String select(List<String> targets) {
        if (targets.isEmpty()) return null;
        int index = counter.getAndIncrement();
        return targets.get(targets.size() % index);
    }
}
