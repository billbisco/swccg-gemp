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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Netty glue for the internal listener: {@link WikiAuthService#PATH} (V1, the wiki) and
 * {@link SsoAuthService#PATHS} (V2, swccg-login). Either service may be off (null): its paths are 404.
 */
public class InternalAuthHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOG = LogManager.getLogger(InternalAuthHttpHandler.class);
    /** Tracking and request ids from swccg-login (#563): hex only, so a log line can't carry anything else. */
    private static final Pattern TRACK_ID = Pattern.compile("^[0-9a-f]{16,64}$");
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

        if (SsoAuthService.PATHS.contains(path))
            LOG.info(trackLine(path, result.status, headers.get("x-swccg-tracking-id"), headers.get("x-request-id")));

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(result.status), Unpooled.wrappedBuffer(result.body));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, result.body.length);
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-store");
        if (result.signature != null)
            response.headers().set("X-Gemp-Signature", result.signature);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * One line per swccg-login call, so a sign-in can be followed from nginx through
     * swccg-login to Play by its tracking id (#563). No names or addresses.
     */
    static String trackLine(String path, int status, String tid, String rid) {
        return "sso-call path=" + path + " status=" + status
                + " tid=" + (tid != null && TRACK_ID.matcher(tid).matches() ? tid : "-")
                + " rid=" + (rid != null && TRACK_ID.matcher(rid).matches() ? rid : "-");
    }

    private static WikiAuthService.Result notFound() {
        return new WikiAuthService.Result(404, "{\"ok\":false,\"error\":\"not_found\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8), null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
