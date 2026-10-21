package com.gempukku.swccgo.async.handler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

/** Builds requests for handler tests. */
final class Requests {
    private Requests() {
    }

    static FullHttpRequest get(String uri, String cookie) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
        if (cookie != null)
            request.headers().set(HttpHeaderNames.COOKIE, cookie);
        return request;
    }

    static FullHttpRequest post(String uri, String form, String cookie) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri,
                Unpooled.copiedBuffer(form, StandardCharsets.UTF_8));
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, form.length());
        if (cookie != null)
            request.headers().set(HttpHeaderNames.COOKIE, cookie);
        return request;
    }

    /** The value a Set-Cookie header gives, e.g. "abc" for "guestSession=abc; Max-Age=...". */
    static String cookieValue(String setCookie) {
        String first = setCookie.split(";")[0];
        return first.substring(first.indexOf('=') + 1);
    }
}
