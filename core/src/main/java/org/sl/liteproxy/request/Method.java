package org.sl.liteproxy.request;

import java.util.HashMap;
import java.util.Map;

public enum Method {
    GET, POST, PUT, DELETE;

    private static final Map<String, Method> CACHE = new HashMap<>();

    static {
        for (Method m : values()) {
            CACHE.put(m.name(), m);
        }
    }

    public static Method fromString(String method) {
        Method m = CACHE.get(method.toUpperCase());
        if (m == null) {
            throw new IllegalArgumentException("Unknown method: " + method);
        }
        return m;
    }
}