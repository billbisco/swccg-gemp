package com.gempukku.swccgo.async.internal;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Netty glue for the internal listener: {@link WikiAuthService#PATH} (V1, the wiki) and
 * {@link SsoAuthService#PATHS} (V2, swccg-login). Either service may be off (null): its paths are 404.
 */
public class InternalAuthHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final WikiAuthService _service;
    private final SsoAuthService _sso;

    public InternalAuthHttpHandler(WikiAuthService service) {
        this(service, null);
    }

    public InternalAuthHttpHandler(WikiAuthService service, SsoAuthService sso) {
        _service = service;
        _sso = sso;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String path = request.uri();
        int q = path.indexOf('?');
        if (q >= 0)
            path = path.substring(0, q);
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, String> h : request.headers())
            headers.put(h.getKey().toLowerCase(java.util.Locale.ROOT), h.getValue());
        byte[] body = ByteBufUtil.getBytes(request.content());

        long now = System.currentTimeMillis();
        WikiAuthService.Result result;
        if (SsoAuthService.PATHS.contains(path))
            result = _sso != null ? _sso.handle(request.method().name(), path, headers, body, now) : notFound();
        else
            result = _service != null ? _service.handle(request.method().name(), path, headers, body, now) : notFound();

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(result.status), Unpooled.wrappedBuffer(result.body));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, result.body.length);
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-store");
        if (result.signature != null)
            response.headers().set("X-Gemp-Signature", result.signature);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static WikiAuthService.Result notFound() {
        return new WikiAuthService.Result(404, "{\"ok\":false,\"error\":\"not_found\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8), null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
