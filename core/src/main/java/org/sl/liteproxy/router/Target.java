package org.sl.liteproxy.router;

import java.util.List;

public interface Target {
    String host();

    static SingleTarget singleton(String target) {
        return new SingleTarget(target);
    }

    static LoadBalancer loadBalancer(List<String> targets, LoadBalancerStrategy strategy) {
        return new LoadBalancer(targets, strategy);
    }

    static LoadBalancer loadBalancer(List<String> targets) {
        return new LoadBalancer(targets, new RoundRobinStrategy());
    }

}
