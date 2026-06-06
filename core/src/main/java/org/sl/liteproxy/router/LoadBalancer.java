package org.sl.liteproxy.router;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LoadBalancer implements Target {
    private final List<String> target;
    private final LoadBalancerStrategy strategy;

    @Override
    public String host() {
        return strategy.select(target);
    }
}
