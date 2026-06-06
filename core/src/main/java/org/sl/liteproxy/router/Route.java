package org.sl.liteproxy.router;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Builder;
import org.sl.liteproxy.request.RequestContext;
@Builder
public record Route(
        HttpMethod method,
        PathMatcher matcher,
        String target,
        int priority
) {
    public boolean match(RequestContext context) {
        return (this.method==null || context.method().equals(this.method))
                && matcher.match(context.path());
    }
}