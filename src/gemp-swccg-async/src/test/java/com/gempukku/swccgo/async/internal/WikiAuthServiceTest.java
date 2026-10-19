package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WikiAuthServiceTest {
    private static final String SECRET = InternalAuthSignerTest.SECRET;
    private static final long NOW = 1_760_000_000_000L;
    private static final String PASSWORD = "S3cret-Passw0rd!";

    private PlayerDAO dao;
    private InternalAuthSigner signer;
    private WikiAuthService service;
    private final List<String> logLines = new ArrayList<>();
    private AbstractAppender appender;

    @BeforeEach
    void setUp() {
        dao = mock(PlayerDAO.class);
        signer = new InternalAuthSigner(SECRET, null);
        service = new WikiAuthService(dao, signer);
        appender = new AbstractAppender("capture", null, null, true, Property.EMPTY_ARRAY) {
            @Override
            public void append(LogEvent event) {
                logLines.add(event.getMessage().getFormattedMessage());
            }
        };
        appender.start();
        Logger logger = (Logger) LogManager.getLogger(WikiAuthService.class);
        logger.addAppender(appender);
        logger.setLevel(org.apache.logging.log4j.Level.ALL);
    }

    @AfterEach
    void tearDown() {
        ((Logger) LogManager.getLogger(WikiAuthService.class)).removeAppender(appender);
        for (String line : logLines)
            assertFalse(line.contains(PASSWORD), "password leaked to log: " + line);
    }

    private static Player player(String name, String password, String type, Date bannedUntil) {
        return new Player(42, name, password, type, null, bannedUntil, "1.1.1.1", "1.1.1.1");
    }

    private WikiAuthService.Result call(String user, String pass, String ip, long now) {
        return callWithNonce(user, pass, ip, now, UUID.randomUUID().toString().replace("-", ""));
    }

    private WikiAuthService.Result callWithNonce(String user, String pass, String ip, long now, String nonce) {
        JSONObject o = new JSONObject();
        o.put("username", user);
        o.put("password", pass);
        if (ip != null)
            o.put("client_ip", ip);
        byte[] body = o.toJSONString().getBytes(StandardCharsets.UTF_8);
        String ts = Long.toString(now / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", nonce);
        h.put("x-gemp-signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, body)));
        return service.handle("POST", WikiAuthService.PATH, h, body, now);
    }

    private static JSONObject json(WikiAuthService.Result r) throws Exception {
        return (JSONObject) new JSONParser().parse(r.bodyAsString());
    }

    @Test
    void validLoginReturnsCanonicalNameAndSignedResponse() throws Exception {
        when(dao.loginPlayer("bill", PASSWORD)).thenReturn(player("bill", "hash", "au", null));
        String nonce = "nonce-valid-login-0001";
        WikiAuthService.Result r = callWithNonce("bill", PASSWORD, "203.0.113.7", NOW, nonce);
        assertEquals(200, r.status);
        JSONObject j = json(r);
        assertEquals(true, j.get("ok"));
        JSONObject user = (JSONObject) j.get("user");
        assertEquals("bill", user.get("name"));
        assertEquals(42L, ((Number) user.get("id")).longValue());
        assertEquals(true, user.get("admin"));
        assertTrue(signer.verify(InternalAuthSigner.responseCanonical(nonce, 200, r.body), r.signature));
    }

    @Test
    void wrongPasswordAndUnknownUserLookTheSame() throws Exception {
        when(dao.loginPlayer(anyString(), anyString())).thenReturn(null);
        assertEquals("invalid_credentials", json(call("bill", PASSWORD, null, NOW)).get("error"));
        assertEquals("invalid_credentials", json(call("nobody", PASSWORD, null, NOW)).get("error"));
    }

    @Test
    void blankPasswordResetAccountIsNeverAccepted() throws Exception {
        // PlayerDAO.loginPlayer matches any password when the stored password is '' (admin reset).
        when(dao.loginPlayer(anyString(), anyString())).thenReturn(player("bill", "", "u", null));
        WikiAuthService.Result r = call("bill", "anything-at-all", null, NOW);
        assertEquals(200, r.status);
        assertEquals(false, json(r).get("ok"));
        assertEquals("password_reset_required", json(r).get("error"));
    }

    @Test
    void bans() throws Exception {
        when(dao.loginPlayer("perma", PASSWORD)).thenReturn(player("perma", "h", "", null));
        assertEquals("banned", json(call("perma", PASSWORD, null, NOW)).get("error"));

        Date future = new Date(NOW + 3_600_000);
        when(dao.loginPlayer("temp", PASSWORD)).thenReturn(player("temp", "h", "", future));
        JSONObject j = json(call("temp", PASSWORD, null, NOW));
        assertEquals("temp_banned", j.get("error"));
        assertEquals(future.getTime() / 1000, ((Number) j.get("until")).longValue());

        when(dao.loginPlayer("expired", PASSWORD)).thenReturn(player("expired", "h", "", new Date(NOW - 1000)));
        assertEquals(true, json(call("expired", PASSWORD, null, NOW)).get("ok"));
    }

    @Test
    void botNamesAreNotLookedUp() throws Exception {
        assertEquals("invalid_credentials", json(call("~YodaBot", PASSWORD, null, NOW)).get("error"));
        verify(dao, never()).loginPlayer(anyString(), anyString());
    }

    @Test
    void badSignatureStaleTimestampAndReplayAreRejected() throws Exception {
        byte[] body = "{\"username\":\"bill\",\"password\":\"x\"}".getBytes(StandardCharsets.UTF_8);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", Long.toString(NOW / 1000));
        h.put("x-gemp-nonce", "abcdefghijklmnop");
        h.put("x-gemp-signature", new InternalAuthSigner("ffffffffffffffffffffffffffffffff", null)
                .sign(InternalAuthSigner.requestCanonical(Long.toString(NOW / 1000), "abcdefghijklmnop", "POST", WikiAuthService.PATH, body)));
        WikiAuthService.Result bad = service.handle("POST", WikiAuthService.PATH, h, body, NOW);
        assertEquals(401, bad.status);
        assertEquals("bad_signature", json(bad).get("error"));
        assertNull(bad.signature);

        // signed 61 s before the server clock
        WikiAuthService.Result stale = callWithNonceAt("bill", NOW - 61_000, NOW, "stale-nonce-000001");
        assertEquals(401, stale.status);
        assertEquals("stale_timestamp", json(stale).get("error"));

        when(dao.loginPlayer(anyString(), anyString())).thenReturn(null);
        assertEquals(200, callWithNonce("bill", PASSWORD, null, NOW, "replay-nonce-00001").status);
        WikiAuthService.Result replay = callWithNonce("bill", PASSWORD, null, NOW, "replay-nonce-00001");
        assertEquals(401, replay.status);
        assertEquals("replayed_nonce", json(replay).get("error"));
        verify(dao, times(1)).loginPlayer(anyString(), anyString());
    }

    private WikiAuthService.Result callWithNonceAt(String user, long signedAt, long serverNow, String nonce) {
        byte[] body = ("{\"username\":\"" + user + "\",\"password\":\"x\"}").getBytes(StandardCharsets.UTF_8);
        String ts = Long.toString(signedAt / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", nonce);
        h.put("x-gemp-signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, body)));
        return service.handle("POST", WikiAuthService.PATH, h, body, serverNow);
    }

    @Test
    void perAccountFailureLimitLocksAndSuccessResets() throws Exception {
        when(dao.loginPlayer(anyString(), anyString())).thenReturn(null);
        for (int i = 0; i < 10; i++)
            assertEquals(200, call("Victim", "wrong" + i, "198.51.100." + i, NOW + i).status);
        // 11th attempt, even from a fresh IP and with different case, is limited
        WikiAuthService.Result limited = call("victim", PASSWORD, "198.51.100.200", NOW + 20);
        assertEquals(429, limited.status);
        assertTrue(((Number) json(limited).get("retry_after")).longValue() > 0);
        // window passes
        when(dao.loginPlayer("victim", PASSWORD)).thenReturn(player("victim", "h", "u", null));
        assertEquals(true, json(call("victim", PASSWORD, "198.51.100.201", NOW + 15 * 60_000L + 100)).get("ok"));
    }

    @Test
    void perIpLimit() throws Exception {
        when(dao.loginPlayer(anyString(), anyString())).thenReturn(null);
        for (int i = 0; i < 30; i++)
            assertEquals(200, call("user" + i, "x", "203.0.113.9", NOW + i).status);
        assertEquals(429, call("someoneelse", "x", "203.0.113.9", NOW + 40).status);
        assertEquals(200, call("someoneelse", "x", "203.0.113.10", NOW + 41).status);
    }

    @Test
    void malformedRequests() throws Exception {
        Map<String, String> none = new HashMap<>();
        assertEquals(404, service.handle("POST", "/gemp-swccg-server/login", none, new byte[0], NOW).status);
        assertEquals(405, service.handle("GET", WikiAuthService.PATH, none, new byte[0], NOW).status);
        assertEquals(413, service.handle("POST", WikiAuthService.PATH, none, new byte[WikiAuthService.MAX_BODY_BYTES + 1], NOW).status);
        assertEquals(401, service.handle("POST", WikiAuthService.PATH, none, new byte[2], NOW).status);
        assertEquals(400, call("", PASSWORD, null, NOW).status);
        assertEquals(400, call("bill", PASSWORD, "not an ip<script>", NOW).status);
        verify(dao, never()).loginPlayer(anyString(), anyString());
    }

    @Test
    void databaseErrorIs503() throws Exception {
        when(dao.loginPlayer(anyString(), anyString())).thenThrow(new SQLException("db down"));
        WikiAuthService.Result r = call("bill", PASSWORD, null, NOW);
        assertEquals(503, r.status);
        assertEquals("unavailable", json(r).get("error"));
    }
}
