package com.gempukku.swccgo.async;

import com.gempukku.swccgo.db.IpBanDAO;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.EXPIRES;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwccgoHttpRequestHandlerTest {
    @TempDir
    Path assets;

    @Test
    void servesSvgAsACacheableImage() throws Exception {
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"/>";
        Path icon = Files.writeString(assets.resolve("bug-solid.svg"), svg);
        IpBanDAO ipBans = mock(IpBanDAO.class);
        when(ipBans.getIpBans()).thenReturn(Set.of());
        when(ipBans.getIpPrefixBans()).thenReturn(Set.of());
        var handler = new SwccgoHttpRequestHandler(Map.of(IpBanDAO.class, ipBans),
                (uri, request, context, writer, remoteIp) -> writer.writeFile(icon.toFile(), Map.of()));
        EmbeddedChannel channel = new EmbeddedChannel(handler) {
            @Override
            protected SocketAddress remoteAddress0() {
                return new InetSocketAddress(InetAddress.getLoopbackAddress(), 12345);
            }
        };

        try {
            channel.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/images/icons/bug-solid.svg"));
            FullHttpResponse response = channel.readOutbound();
            assertNotNull(response);
            try {
                assertEquals(OK, response.status());
                assertEquals("image/svg+xml", response.headers().get(CONTENT_TYPE));
                assertNotNull(response.headers().get(EXPIRES));
                assertEquals(svg, response.content().toString(CharsetUtil.UTF_8));
            } finally {
                response.release();
            }
        } finally {
            channel.finishAndReleaseAll();
        }
    }
}
