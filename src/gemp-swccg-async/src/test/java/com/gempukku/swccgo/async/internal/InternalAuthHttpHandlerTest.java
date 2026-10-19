package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class InternalAuthHttpHandlerTest {
    private final InternalAuthSigner signer = new InternalAuthSigner(InternalAuthSignerTest.SECRET, null);

    private FullHttpResponse send(EmbeddedChannel ch, String uri, String body, String nonce, boolean sign) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, Unpooled.wrappedBuffer(bytes));
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        req.headers().set("X-Gemp-Timestamp", ts);
        req.headers().set("X-Gemp-Nonce", nonce);
        if (sign)
            req.headers().set("X-Gemp-Signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, bytes)));
        ch.writeInbound(req);
        return ch.readOutbound();
    }

    @Test
    void endToEndThroughNetty() throws Exception {
        PlayerDAO dao = mock(PlayerDAO.class);
        when(dao.loginPlayer("bill", "pw-123456")).thenReturn(new Player(7, "bill", "h", "u", null, null, null, null));
        WikiAuthService service = new WikiAuthService(dao, signer);

        FullHttpResponse ok = send(new EmbeddedChannel(new InternalAuthHttpHandler(service)),
                "/internal/v1/verify", "{\"username\":\"bill\",\"password\":\"pw-123456\",\"client_ip\":\"203.0.113.7\"}", "netty-nonce-000001", true);
        assertEquals(200, ok.status().code());
        assertEquals("no-store", ok.headers().get(HttpHeaderNames.CACHE_CONTROL));
        byte[] body = io.netty.buffer.ByteBufUtil.getBytes(ok.content());
        assertTrue(new String(body, StandardCharsets.UTF_8).contains("\"ok\":true"));
        assertTrue(signer.verify(InternalAuthSigner.responseCanonical("netty-nonce-000001", 200, body), ok.headers().get("X-Gemp-Signature")));

        FullHttpResponse other = send(new EmbeddedChannel(new InternalAuthHttpHandler(service)),
                "/gemp-swccg-server/admin/user", "{}", "netty-nonce-000002", true);
        assertEquals(404, other.status().code());

        FullHttpResponse unsigned = send(new EmbeddedChannel(new InternalAuthHttpHandler(service)),
                "/internal/v1/verify", "{\"username\":\"bill\",\"password\":\"pw-123456\"}", "netty-nonce-000003", false);
        assertEquals(401, unsigned.status().code());
        verify(dao, times(1)).loginPlayer(anyString(), anyString());
    }

    @Test
    void portParsing() {
        assertEquals(0, InternalAuthServer.parsePort(null));
        assertEquals(0, InternalAuthServer.parsePort("nope"));
        assertEquals(8090, InternalAuthServer.parsePort(" 8090 "));
    }
}
