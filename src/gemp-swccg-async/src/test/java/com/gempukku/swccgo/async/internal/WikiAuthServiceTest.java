package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.service.AuthService;
import com.gempukku.swccgo.service.AuthService.Status;
import com.gempukku.swccgo.service.LoginThrottle;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WikiAuthServiceTest {
    private static final String SECRET = InternalAuthSignerTest.SECRET;
    private static final long NOW = 1_760_000_000_000L;
    private static final String PASSWORD = "S3cret-Passw0rd!";
    private static final String IP = "203.0.113.7";

    private AuthService auth;
    private InternalAuthSigner signer;
    private WikiAuthService service;
    private final List<String> logLines = new ArrayList<>();
    private AbstractAppender appender;

    @BeforeEach
    void setUp() {
        auth = mock(AuthService.class);
        signer = new InternalAuthSigner(SECRET, null);
        service = new WikiAuthService(auth, signer);
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

    private static Player player(String name, String type, Date bannedUntil) {
        return new Player(42, name, "pbkdf2-sha256$x", type, null, bannedUntil, "1.1.1.1", "1.1.1.1");
    }

    private void answer(Status status, Player p) throws SQLException {
        when(auth.verifyCredentials(anyString(), anyString(), anyString())).thenReturn(AuthResults.of(status, p));
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
        return signedCall(o.toJSONString().getBytes(StandardCharsets.UTF_8), now, now, nonce);
    }

    private WikiAuthService.Result signedCall(byte[] body, long signedAt, long serverNow, String nonce) {
        String ts = Long.toString(signedAt / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", nonce);
        h.put("x-gemp-signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, body)));
        return service.handle("POST", WikiAuthService.PATH, h, body, serverNow);
    }

    private static JSONObject json(WikiAuthService.Result r) throws Exception {
        return (JSONObject) new JSONParser().parse(r.bodyAsString());
    }

    @Test
    void okUsesCanonicalNameAndForwardedClientIp() throws Exception {
        // typed "BILL", stored "Bill": the wiki must get the stored name
        when(auth.verifyCredentials("BILL", PASSWORD, IP)).thenReturn(AuthResults.of(Status.OK, player("Bill", "au", null)));
        String nonce = "nonce-valid-login-0001";
        WikiAuthService.Result r = callWithNonce("BILL", PASSWORD, IP, NOW, nonce);
        assertEquals(200, r.status);
        JSONObject j = json(r);
        assertEquals(true, j.get("ok"));
        JSONObject user = (JSONObject) j.get("user");
        assertEquals("Bill", user.get("name"));
        assertEquals(42L, ((Number) user.get("id")).longValue());
        assertEquals(true, user.get("admin"));
        assertTrue(signer.verify(InternalAuthSigner.responseCanonical(nonce, 200, r.body), r.signature));
        verify(auth).verifyCredentials("BILL", PASSWORD, IP);
    }

    /** Every status other than OK is not-OK and maps to its own reason. */
    @ParameterizedTest
    @EnumSource(value = Status.class, names = "OK", mode = EnumSource.Mode.EXCLUDE)
    void everyOtherStatusIsNotOk(Status status) throws Exception {
        Date until = new Date(NOW + 3_600_000);
        answer(status, status == Status.INVALID || status == Status.THROTTLED ? null : player("Bill", "", until));
        WikiAuthService.Result r = call("Bill", PASSWORD, IP, NOW);
        assertEquals(200, r.status);
        JSONObject j = json(r);
        assertEquals(false, j.get("ok"));
        assertNull(j.get("user"), "no user on " + status);
        String expected = switch (status) {
            case INVALID -> "invalid_credentials";
            case RESET_REQUIRED -> "reset_required";
            case BANNED -> "banned";
            case TEMP_BANNED -> "temp_banned";
            case DEACTIVATED -> "deactivated";
            case THROTTLED -> "throttled";
            default -> fail("unmapped " + status);
        };
        assertEquals(expected, j.get("error"));
        if (status == Status.TEMP_BANNED)
            assertEquals(until.getTime() / 1000, ((Number) j.get("until")).longValue());
        else
            assertNull(j.get("until"));
    }

    @Test
    void resetRequiredNeverLogsIn() throws Exception {
        // The password was a valid one-time reset code: the player must finish the reset on GEMP.
        answer(Status.RESET_REQUIRED, player("Bill", "u", null));
        JSONObject j = json(call("Bill", "ABCD-EFGH-JKLM", IP, NOW));
        assertEquals(false, j.get("ok"));
        assertEquals("reset_required", j.get("error"));
    }

    @Test
    void okWithoutPlayerIsNotOk() throws Exception {
        answer(Status.OK, null);
        JSONObject j = json(call("Bill", PASSWORD, IP, NOW));
        assertEquals(false, j.get("ok"));
        assertEquals("invalid_credentials", j.get("error"));
    }

    /** A blank stored hash (old admin-reset state) never verifies in Alpha3's AuthService. */
    @Test
    void blankStoredHashIsInvalidWithRealAuthService() throws Exception {
        PlayerDAO dao = mock(PlayerDAO.class);
        Player blank = new Player(7, "Bill", "", "u", null, null, null, null);
        when(dao.findPlayerForLogin(anyString())).thenReturn(blank);
        AuthService real = new AuthService(dao, new PasswordHasher(1000), new LoginThrottle(), () -> NOW);
        WikiAuthService svc = new WikiAuthService(real, signer);
        for (String attempt : new String[]{"anything-at-all", "", " ", PASSWORD}) {
            JSONObject o = new JSONObject();
            o.put("username", "Bill");
            o.put("password", attempt.isEmpty() ? "x" : attempt);
            o.put("client_ip", IP);
            byte[] body = o.toJSONString().getBytes(StandardCharsets.UTF_8);
            String nonce = UUID.randomUUID().toString().replace("-", "");
            String ts = Long.toString(NOW / 1000);
            Map<String, String> h = new HashMap<>();
            h.put("x-gemp-timestamp", ts);
            h.put("x-gemp-nonce", nonce);
            h.put("x-gemp-signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, body)));
            JSONObject j = json(svc.handle("POST", WikiAuthService.PATH, h, body, NOW));
            assertEquals(false, j.get("ok"), "blank hash must not verify: " + attempt);
            assertEquals("invalid_credentials", j.get("error"));
        }
        verify(dao, never()).updatePasswordHash(anyInt(), anyString(), anyString());
    }

    @Test
    void clientIpIsRequiredAndValidated() throws Exception {
        answer(Status.OK, player("Bill", "u", null));
        assertEquals(400, call("Bill", PASSWORD, null, NOW).status);
        assertEquals(400, call("Bill", PASSWORD, "not an ip<script>", NOW).status);
        verify(auth, never()).verifyCredentials(anyString(), anyString(), anyString());
    }

    @Test
    void clientIpIsCoveredBySignature() throws Exception {
        answer(Status.OK, player("Bill", "u", null));
        String nonce = "tamper-nonce-000001";
        byte[] signedBody = "{\"username\":\"Bill\",\"password\":\"pw-12345678\",\"client_ip\":\"203.0.113.7\"}".getBytes(StandardCharsets.UTF_8);
        byte[] tampered = "{\"username\":\"Bill\",\"password\":\"pw-12345678\",\"client_ip\":\"198.51.100.1\"}".getBytes(StandardCharsets.UTF_8);
        String ts = Long.toString(NOW / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", nonce);
        h.put("x-gemp-signature", signer.sign(InternalAuthSigner.requestCanonical(ts, nonce, "POST", WikiAuthService.PATH, signedBody)));
        WikiAuthService.Result r = service.handle("POST", WikiAuthService.PATH, h, tampered, NOW);
        assertEquals(401, r.status);
        assertEquals("bad_signature", json(r).get("error"));
        verify(auth, never()).verifyCredentials(anyString(), anyString(), anyString());
    }

    @Test
    void botNamesAreNotLookedUp() throws Exception {
        assertEquals("invalid_credentials", json(call("~YodaBot", PASSWORD, IP, NOW)).get("error"));
        verify(auth, never()).verifyCredentials(anyString(), anyString(), anyString());
    }

    @Test
    void badSignatureStaleTimestampAndReplayAreRejected() throws Exception {
        byte[] body = "{\"username\":\"bill\",\"password\":\"x\",\"client_ip\":\"203.0.113.7\"}".getBytes(StandardCharsets.UTF_8);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-timestamp", Long.toString(NOW / 1000));
        h.put("x-gemp-nonce", "abcdefghijklmnop");
        h.put("x-gemp-signature", new InternalAuthSigner("ffffffffffffffffffffffffffffffff", null)
                .sign(InternalAuthSigner.requestCanonical(Long.toString(NOW / 1000), "abcdefghijklmnop", "POST", WikiAuthService.PATH, body)));
        WikiAuthService.Result bad = service.handle("POST", WikiAuthService.PATH, h, body, NOW);
        assertEquals(401, bad.status);
        assertEquals("bad_signature", json(bad).get("error"));
        assertNull(bad.signature);

        WikiAuthService.Result stale = signedCall(body, NOW - 61_000, NOW, "stale-nonce-000001");
        assertEquals(401, stale.status);
        assertEquals("stale_timestamp", json(stale).get("error"));

        answer(Status.INVALID, null);
        assertEquals(200, callWithNonce("bill", PASSWORD, IP, NOW, "replay-nonce-00001").status);
        WikiAuthService.Result replay = callWithNonce("bill", PASSWORD, IP, NOW, "replay-nonce-00001");
        assertEquals(401, replay.status);
        assertEquals("replayed_nonce", json(replay).get("error"));
        verify(auth, times(1)).verifyCredentials(anyString(), anyString(), anyString());
    }

    @Test
    void secondLayerPerAccountLimit() throws Exception {
        answer(Status.INVALID, null);
        for (int i = 0; i < 10; i++)
            assertEquals(200, call("Victim", "wrong" + i, "198.51.100." + i, NOW + i).status);
        WikiAuthService.Result limited = call("victim", PASSWORD, "198.51.100.200", NOW + 20);
        assertEquals(429, limited.status);
        assertEquals("throttled", json(limited).get("error"));
        assertTrue(((Number) json(limited).get("retry_after")).longValue() > 0);
        verify(auth, times(10)).verifyCredentials(anyString(), anyString(), anyString());
        when(auth.verifyCredentials(eq("victim"), eq(PASSWORD), anyString())).thenReturn(AuthResults.of(Status.OK, player("Victim", "u", null)));
        assertEquals(true, json(call("victim", PASSWORD, "198.51.100.201", NOW + 15 * 60_000L + 100)).get("ok"));
    }

    @Test
    void secondLayerPerIpLimit() throws Exception {
        answer(Status.INVALID, null);
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
        assertEquals(400, call("", PASSWORD, IP, NOW).status);
        verify(auth, never()).verifyCredentials(anyString(), anyString(), anyString());
    }

    @Test
    void databaseErrorIs503() throws Exception {
        when(auth.verifyCredentials(anyString(), anyString(), anyString())).thenThrow(new SQLException("db down"));
        WikiAuthService.Result r = call("bill", PASSWORD, IP, NOW);
        assertEquals(503, r.status);
        assertEquals("unavailable", json(r).get("error"));
    }
}
