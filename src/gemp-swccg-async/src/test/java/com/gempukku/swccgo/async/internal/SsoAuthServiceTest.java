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
    void playersKeepsSpacesInFullNames() throws Exception {
        // t122u: a full name with a space reaches Play as typed, not as "Luke_Sky"
        when(auth.createPlayerWithoutPassword("Luke Sky", IP)).thenReturn(player(9, "Luke Sky", "u"));
        JSONObject r = json(call(KEY, SsoAuthService.PLAYERS, "{\"username\":\"Luke Sky\",\"client_ip\":\"" + IP + "\"}", NOW));
        assertEquals(true, r.get("ok"));
        assertEquals("Luke Sky", ((JSONObject) r.get("user")).get("name"));
        verify(auth).createPlayerWithoutPassword("Luke Sky", IP);
    }

    @Test
    void registerErrors() throws Exception {
        when(auth.registerAndGet(eq("Taken"), anyString(), anyString())).thenThrow(new LoginInvalidException());
        assertEquals("name_taken", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"Taken\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"a  b\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"~Bot\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        // t127u: a 4-byte character (above U+FFFF) never reaches the utf8mb3 player table
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"Luke\\ud835\\udc00\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.PLAYERS, "{\"username\":\"Han \\ud840\\udc00\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
        verify(auth, never()).registerAndGet(eq("Luke\uD835\uDC00"), anyString(), anyString());
        verify(auth, never()).createPlayerWithoutPassword(eq("Han \uD840\uDC00"), anyString());
        when(settings.newAccountRegistrationEnabled()).thenReturn(false);
        assertEquals("signup_closed", json(call(KEY, SsoAuthService.REGISTER, "{\"username\":\"New1\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW)).get("error"));
    }

    @Test
    void v1PathNotServedHere() {
        assertEquals(404, call(KEY, "/internal/v1/verify", "{}", NOW).status);
    }

    private static String pw(long id, String password) {
        return "{\"id\":" + id + ",\"password\":\"" + password + "\",\"client_ip\":\"" + IP + "\"}";
    }

    @Test
    void setPasswordIsOffUnlessSwitchedOn() throws Exception {
        WikiAuthService.Result r = call(KEY, SsoAuthService.PASSWORD, pw(5, "new-pass"), NOW);
        assertEquals(404, r.status);
        verify(auth, never()).setPassword(anyInt(), anyString());
    }

    @Test
    void setPasswordById() throws Exception {
        service = new SsoAuthService(auth, settings, keys, true);
        when(auth.setPassword(5, "new-pass")).thenReturn(player(5, "Bill", "au"));
        JSONObject r = json(call(KEY, SsoAuthService.PASSWORD, pw(5, "new-pass"), NOW));
        assertEquals(true, r.get("ok"));
        assertEquals(5L, ((JSONObject) r.get("user")).get("id"));
        assertFalse(r.toJSONString().contains("admin"));
        assertFalse(r.toJSONString().contains("new-pass"));
    }

    @Test
    void setPasswordErrorsAndLimit() throws Exception {
        service = new SsoAuthService(auth, settings, keys, true);
        when(auth.setPassword(6, "x")).thenReturn(null);
        when(auth.setPassword(7, "")).thenThrow(new AuthService.WeakPasswordException());
        assertEquals("not_found", json(call(KEY, SsoAuthService.PASSWORD, pw(6, "x"), NOW)).get("error"));
        assertEquals("weak_password", json(call(KEY, SsoAuthService.PASSWORD, pw(7, ""), NOW)).get("error"));
        assertEquals(400, call(KEY, SsoAuthService.PASSWORD, "{\"id\":\"5\",\"password\":\"p\",\"client_ip\":\"" + IP + "\"}", NOW).status);
        assertEquals(400, call(KEY, SsoAuthService.PASSWORD, "{\"id\":5,\"client_ip\":\"" + IP + "\"}", NOW).status);
        when(auth.setPassword(eq(8), anyString())).thenReturn(player(8, "Rey", "u"));
        for (int i = 0; i < 5; i++)
            assertEquals(200, call(KEY, SsoAuthService.PASSWORD, pw(8, "p" + i), NOW).status);
        assertEquals(429, call(KEY, SsoAuthService.PASSWORD, pw(8, "p9"), NOW).status, "five password sets per account per hour");
    }

    @Test
    void setPasswordEndsGempSessionsOnlyWhenAsked() throws Exception {
        service = new SsoAuthService(auth, settings, keys, true);
        java.util.List<String> ended = new java.util.ArrayList<>();
        service.setSessionEnder(ended::add);
        when(auth.setPassword(eq(9), anyString())).thenReturn(player(9, "Han", "u"));
        JSONObject r = json(call(KEY, SsoAuthService.PASSWORD, pw(9, "first"), NOW));
        assertEquals(false, r.get("sessions_ended"));
        assertTrue(ended.isEmpty(), "no end_sessions: nobody logged out");
        String body = "{\"id\":9,\"password\":\"second\",\"client_ip\":\"" + IP + "\",\"end_sessions\":true}";
        r = json(call(KEY, SsoAuthService.PASSWORD, body, NOW));
        assertEquals(true, r.get("sessions_ended"));
        assertEquals(java.util.List.of("Han"), ended);
        when(auth.setPassword(10, "weak")).thenThrow(new AuthService.WeakPasswordException());
        call(KEY, SsoAuthService.PASSWORD, "{\"id\":10,\"password\":\"weak\",\"client_ip\":\"" + IP + "\",\"end_sessions\":true}", NOW);
        assertEquals(1, ended.size(), "a refused password ends nothing");
    }

    @Test
    void closeDeactivatesAndEndsSessions() throws Exception {
        java.util.List<String> ended = new java.util.ArrayList<>();
        service.setSessionEnder(ended::add);
        when(auth.closeAccount(11)).thenReturn(player(11, "Finn", "u"));
        JSONObject r = json(call(KEY, SsoAuthService.CLOSE, "{\"id\":11,\"client_ip\":\"" + IP + "\"}", NOW));
        assertEquals(true, r.get("ok"));
        assertEquals(11L, ((JSONObject) r.get("user")).get("id"));
        assertEquals(java.util.List.of("Finn"), ended);
        when(auth.closeAccount(12)).thenReturn(null);
        assertEquals("not_found", json(call(KEY, SsoAuthService.CLOSE, "{\"id\":12}", NOW)).get("error"));
        assertEquals(1, ended.size(), "nothing ended for an unknown player");
    }

    @Test
    void closeEmptyBodyIsSignedBadRequest() throws Exception {
        // swccg-login probes for the call with {}: a signed 400 means "supported".
        WikiAuthService.Result r = call(KEY, SsoAuthService.CLOSE, "{}", NOW);
        assertEquals(400, r.status);
        assertTrue(r.signature != null);
        verify(auth, never()).closeAccount(anyInt());
        // Unknown paths stay unsigned 404s.
        assertEquals(404, call(KEY, "/internal/v2/nope", "{}", NOW).status);
    }

    @Test
    void renameRenamesEndsTheOldSessionsAndClearsCaches() throws Exception {
        java.util.List<String> ended = new java.util.ArrayList<>();
        int[] cleared = {0};
        service.setSessionEnder(ended::add);
        service.setRenameHooks(name -> false, () -> cleared[0]++);
        when(auth.getPlayer(11)).thenReturn(player(11, "Finn", "u"));
        when(auth.renameAccount(11, "Finn2")).thenReturn(player(11, "Finn2", "u"));
        JSONObject r = json(call(KEY, SsoAuthService.RENAME, "{\"id\":11,\"username\":\"Finn2\",\"client_ip\":\"" + IP + "\"}", NOW));
        assertEquals(true, r.get("ok"));
        assertEquals("Finn2", ((JSONObject) r.get("user")).get("name"));
        assertEquals(java.util.List.of("Finn"), ended, "sessions of the old name end");
        assertEquals(1, cleared[0]);
    }

    @Test
    void renameRefusals() throws Exception {
        //test1: a bad name never reaches Play's player table
        //test2: refused while the player is at a table
        //test3: Play's own refusals pass through with their code
        //test4: an empty body is a signed 400, so swccg-login shows the feature
        service.setRenameHooks(name -> name.equals("Rey"), null);
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.RENAME, "{\"id\":11,\"username\":\"has  two spaces\"}", NOW)).get("error")); //test1
        verify(auth, never()).renameAccount(anyInt(), org.mockito.ArgumentMatchers.anyString()); //test1
        assertEquals("name_invalid", json(call(KEY, SsoAuthService.RENAME, "{\"id\":11,\"username\":\"Rey\\ud835\\udc00\"}", NOW)).get("error")); //test1: 4-byte (t127u)
        verify(auth, never()).renameAccount(anyInt(), org.mockito.ArgumentMatchers.anyString()); //test1
        when(auth.getPlayer(12)).thenReturn(player(12, "Rey", "u"));
        assertEquals("busy", json(call(KEY, SsoAuthService.RENAME, "{\"id\":12,\"username\":\"Rey2\"}", NOW)).get("error")); //test2
        verify(auth, never()).renameAccount(anyInt(), org.mockito.ArgumentMatchers.anyString()); //test2
        when(auth.getPlayer(13)).thenReturn(player(13, "Poe", "u"));
        when(auth.renameAccount(13, "Rey")).thenThrow(new AuthService.RenameRefused("name_taken"));
        assertEquals("name_taken", json(call(KEY, SsoAuthService.RENAME, "{\"id\":13,\"username\":\"Rey\"}", NOW)).get("error")); //test3
        WikiAuthService.Result probe = call(KEY, SsoAuthService.RENAME, "{}", NOW);
        assertEquals(400, probe.status); //test4
        assertTrue(probe.signature != null); //test4
    }
}
