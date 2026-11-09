package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.service.AuthService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Optional second listener for wiki -> GEMP calls. It is a separate port so the public
 * nginx vhosts (which proxy only port 80) can never reach it. Publish it on NO host port;
 * only containers on the GEMP docker network (the wiki) connect to it.
 *
 * Enabled only when INTERNAL_AUTH_PORT > 0 and GEMP_INTERNAL_AUTH_SECRET (V1, the wiki) and/or
 * GEMP_INTERNAL_AUTH_V2_KEYS (V2, swccg-login) is set. Each contract is served only if its own
 * secret is set.
 */
public final class InternalAuthServer {
    private static final Logger LOG = LogManager.getLogger(InternalAuthServer.class);

    private InternalAuthServer() {
    }

    /** @return the bound channel, or null when disabled. */
    public static Channel startIfConfigured(EventLoopGroup boss, EventLoopGroup workers, AuthService authService) throws InterruptedException {
        return startIfConfigured(boss, workers, authService, null);
    }

    public static Channel startIfConfigured(EventLoopGroup boss, EventLoopGroup workers, AuthService authService,
                                            GempSettingDAO settings) throws InterruptedException {
        int port = parsePort(System.getenv("INTERNAL_AUTH_PORT"));
        String secret = System.getenv("GEMP_INTERNAL_AUTH_SECRET");
        InternalAuthKeysV2 v2 = InternalAuthKeysV2.parse(System.getenv("GEMP_INTERNAL_AUTH_V2_KEYS"));
        boolean v1 = secret != null && !secret.isEmpty();
        if (port <= 0 || (!v1 && v2 == null) || authService == null) {
            LOG.info("internal auth listener disabled (INTERNAL_AUTH_PORT and GEMP_INTERNAL_AUTH_SECRET / GEMP_INTERNAL_AUTH_V2_KEYS not set)");
            return null;
        }
        WikiAuthService service = v1 ? new WikiAuthService(authService,
                new InternalAuthSigner(secret, System.getenv("GEMP_INTERNAL_AUTH_SECRET_PREVIOUS"))) : null;
        boolean setPassword = "true".equalsIgnoreCase(System.getenv("GEMP_INTERNAL_AUTH_V2_SET_PASSWORD"));
        SsoAuthService sso = v2 != null ? new SsoAuthService(authService, settings, v2, setPassword) : null;
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, workers)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ReadTimeoutHandler(10));
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(WikiAuthService.MAX_BODY_BYTES + 1024));
                        ch.pipeline().addLast(new InternalAuthHttpHandler(service, sso));
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true);
        Channel ch = b.bind(port).sync().channel();
        LOG.info("internal auth listener on port " + port + " (v1 " + (v1 ? "on" : "off") + ", v2 " + (v2 != null ? "on" : "off") + ", v2 set-password " + (v2 != null && setPassword ? "on" : "off") + ")");
        return ch;
    }

    static int parsePort(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
