package org.sl.liteproxy.request;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Builder;
import org.sl.liteproxy.router.Route;

import java.util.List;
import java.util.Map;

@Builder
public record RequestContext(
        String host,
        HttpMethod method,
        String path,
        Map<String,String> headers,
        Map<String, List<String>> queryParams,
        byte[] body,
        Route route
) {}