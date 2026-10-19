package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.PlayerDAO;
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
 * Enabled only when INTERNAL_AUTH_PORT > 0 and GEMP_INTERNAL_AUTH_SECRET is set.
 */
public final class InternalAuthServer {
    private static final Logger LOG = LogManager.getLogger(InternalAuthServer.class);

    private InternalAuthServer() {
    }

    /** @return the bound channel, or null when disabled. */
    public static Channel startIfConfigured(EventLoopGroup boss, EventLoopGroup workers, PlayerDAO playerDao) throws InterruptedException {
        int port = parsePort(System.getenv("INTERNAL_AUTH_PORT"));
        String secret = System.getenv("GEMP_INTERNAL_AUTH_SECRET");
        if (port <= 0 || secret == null || secret.isEmpty()) {
            LOG.info("wiki-auth internal listener disabled (INTERNAL_AUTH_PORT / GEMP_INTERNAL_AUTH_SECRET not set)");
            return null;
        }
        InternalAuthSigner signer = new InternalAuthSigner(secret, System.getenv("GEMP_INTERNAL_AUTH_SECRET_PREVIOUS"));
        WikiAuthService service = new WikiAuthService(playerDao, signer);
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, workers)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ReadTimeoutHandler(10));
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(WikiAuthService.MAX_BODY_BYTES + 1024));
                        ch.pipeline().addLast(new InternalAuthHttpHandler(service));
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true);
        Channel ch = b.bind(port).sync().channel();
        LOG.info("wiki-auth internal listener on port " + port);
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
