package org.sl.liteproxy.router;

import java.util.List;

public interface LoadBalancerStrategy {

    String select(List<String> targets);
}
