package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.service.AuthService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SsoAuthServiceTest {
    private static final String KEY = "swccg-login-test";
    private static final String SECRET = InternalAuthKeysV2Test.SECRET;
    private static final long NOW = 1_760_000_000_000L;
    private static final String IP = "203.0.113.7";

    private AuthService auth;
    private GempSettingDAO settings;
    private InternalAuthKeysV2 keys;
    private SsoAuthService service;

    @BeforeEach
    void setUp() {
        auth = mock(AuthService.class);
        settings = mock(GempSettingDAO.class);
        when(settings.newAccountRegistrationEnabled()).thenReturn(true);
        keys = InternalAuthKeysV2.parse(KEY + "=" + SECRET + ";other-site=" + "y".repeat(40));
        service = new SsoAuthService(auth, settings, keys);
    }

    static Player player(int id, String name, String type) {
        return new Player(id, name, "hash", type, null, null, IP, IP);
    }

    static AuthService.AuthResult result(AuthService.Status s, Player p) throws Exception {
        Constructor<AuthService.AuthResult> c = AuthService.AuthResult.class.getDeclaredConstructor(AuthService.Status.class, Player.class);
        c.setAccessible(true);
        return c.newInstance(s, p);
    }

    private WikiAuthService.Result call(String keyId, String path, String body, long now) {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String ts = Long.toString(now / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-key-id", keyId);
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", nonce);
        h.put("x-gemp-signature", keys.sign(keyId, InternalAuthKeysV2.requestCanonical(keyId, ts, nonce, "POST", path, b)));
        WikiAuthService.Result r = service.handle("POST", path, h, b, NOW);
        if (r.signature != null)
            assertTrue(keys.verify(keyId, InternalAuthKeysV2.responseCanonical(keyId, nonce, r.status, r.body), r.signature));
        return r;
    }

    private static JSONObject json(WikiAuthService.Result r) throws Exception {
        return (JSONObject) new JSONParser().parse(r.bodyAsString());
    }

    @Test
    void verifyNeverSendsAdminFlag() throws Exception {
        Player admin = player(5, "Bill", "au");
        when(auth.verifyCredentials("bill", "pw", IP)).thenReturn(result(AuthService.Status.OK, admin));
        WikiAuthService.Result r = call(KEY, SsoAuthService.VERIFY, "{\"username\":\"bill\",\"password\":\"pw\",\"client_ip\":\"" + IP + "\"}", NOW);
        assertEquals(200, r.status);
        JSONObject user = (JSONObject) json(r).get("user");
        assertEquals(5L, user.get("id"));
        assertEquals("Bill", user.get("name"));
        assertFalse(r.bodyAsString().contains("admin"));
    }

    @Test
    void badSignatureUnsignedAnswer() {
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-key-id", KEY);
        h.put("x-gemp-timestamp", Long.toString(NOW / 1000));
        h.put("x-gemp-nonce", "abcdefghijklmnopqrst");
        h.put("x-gemp-signature", "v2=00");
        WikiAuthService.Result r = service.handle("POST", SsoAuthService.VERIFY, h, "{}".getBytes(), NOW);
        assertEquals(401, r.status);
        assertNull(r.signature);
    }

    @Test
    void unknownKeyIdRefused() {
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-key-id", "nope");
        h.put("x-gemp-timestamp", Long.toString(NOW / 1000));
        h.put("x-gemp-nonce", "abcdefghijklmnopqrst");
        h.put("x-gemp-signature", "v2=00");
        assertEquals(401, service.handle("POST", SsoAuthService.STATUS, h, "{}".getBytes(), NOW).status);
    }

    @Test
    void staleTimestampAndReplay() throws Exception {
        assertEquals(401, call(KEY, SsoAuthService.STATUS, "{\"id\":5}", NOW - 120_000).status);
        when(auth.accountStatus(5)).thenReturn(result(AuthService.Status.OK, player(5, "Bill", "u")));
        byte[] b = "{\"id\":5}".getBytes();
        String ts = Long.toString(NOW / 1000);
        Map<String, String> h = new HashMap<>();
        h.put("x-gemp-key-id", KEY);
        h.put("x-gemp-timestamp", ts);
        h.put("x-gemp-nonce", "abcdefghijklmnopqrst");
        h.put("x-gemp-signature", keys.sign(KEY, InternalAuthKeysV2.requestCanonical(KEY, ts, "abcdefghijklmnopqrst", "POST", SsoAuthService.STATUS, b)));
        assertEquals(200, service.handle("POST", SsoAuthService.STATUS, h, b, NOW).status);
        assertEquals(401, service.handle("POST", SsoAuthService.STATUS, h, b, NOW).status);
    }

    @Test
    void statusMapsBans() throws Exception {
        when(auth.accountStatus(5)).thenReturn(result(AuthService.Status.BANNED, player(5, "Bill", "")));
        when(auth.accountStatus(6)).thenReturn(result(AuthService.Status.INVALID, null));
        assertEquals("banned", json(call(KEY, SsoAuthService.STATUS, "{\"id\":5}", NOW)).get("error"));
        assertEquals("not_found", json(call(KEY, SsoAuthService.STATUS, "{\"id\":6}", NOW)).get("error"));
        assertEquals(400, call(KEY, SsoAuthService.STATUS, "{\"id\":\"5\"}", NOW).status);
    }

    @Test
    void playersCreatesPasswordlessAccount() throws Exception {
        when(auth.createPlayerWithoutPassword("Rey", IP)).thenReturn(player(9, "Rey", "u"));
        JSONObject r = json(call(KEY, SsoAuthService.PLAYERS, "{\"username\":\"Rey\",\"client_ip\":\"" + IP + "\"}", NOW));
        assertEquals(true, r.get("ok"));
        verify(auth).createPlayerWithoutPassword("Rey", IP);
        verify(auth, never()).registerAndGet(anyString(), anyString(), anyString());
    }

    @Test
    void registerErrors() throws Exception {
        when(auth.registerAndGet(eq("Taken"), anyString(), anyString())).thenThrow(new LoginInvalidException());
        assertEquals("name_taken", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"Taken\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"a b\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"~Bot\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        when(settings.newAccountRegistrationEnabled()).thenReturn(false);
        assertEquals("signup_closed", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"New1\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
    }

    @Test
    void v1PathNotServedHere() {
        assertEquals(404, call(KEY, "/internal/v1/verify", "{}", NOW).status);
    }
}
