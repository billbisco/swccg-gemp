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

/** Netty glue for the internal listener. Only {@link WikiAuthService#PATH} exists on this port. */
public class InternalAuthHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final WikiAuthService _service;

    public InternalAuthHttpHandler(WikiAuthService service) {
        _service = service;
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

        WikiAuthService.Result result = _service.handle(request.method().name(), path, headers, body, System.currentTimeMillis());

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(result.status), Unpooled.wrappedBuffer(result.body));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, result.body.length);
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-store");
        if (result.signature != null)
            response.headers().set("X-Gemp-Signature", result.signature);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
