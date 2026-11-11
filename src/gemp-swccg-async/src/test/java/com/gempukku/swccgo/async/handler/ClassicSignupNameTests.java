package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.internal.InternalAuthKeysV2;
import com.gempukku.swccgo.async.oidc.SiteNameCheck;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Play's classic signup honours the guest seat names and the sign-in service's holds (swccg-guild/work#261). */
public class ClassicSignupNameTests {
    private static final String SECRET = "test-secret-0123456789-abcdefghijklmnopqrstuvwxyz";
    private static final URI URL = URI.create("http://login.test/internal/v1/name-check");
    private final InternalAuthKeysV2 _keys = new InternalAuthKeysV2(Map.of("gemp", List.of(SECRET)));
    private final List<String> _asked = new ArrayList<>();

    /** A sign-in service that answers with this JSON body, signed with the shared key. */
    private SiteNameCheck service(int status, String json, boolean sign) {
        return new SiteNameCheck(URL, SECRET, (uri, headers, body) -> {
            _asked.add(new String(body, StandardCharsets.UTF_8));
            String nonce = headers.get("X-Gemp-Nonce");
            String canonical = InternalAuthKeysV2.requestCanonical("gemp", headers.get("X-Gemp-Timestamp"), nonce,
                    "POST", uri.getPath(), body);
            assertTrue(_keys.verify("gemp", canonical, headers.get("X-Gemp-Signature")), "request is signed");
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            String sig = sign ? _keys.sign("gemp", InternalAuthKeysV2.responseCanonical("gemp", nonce, status, out)) : null;
            return new SiteNameCheck.Response(status, sig, out);
        });
    }

    @Test
    public void seatNamesAndTheirSpellingsAreReserved() {
        for (String n : new String[]{"Light Side", "Dark Side", "Light_Side", "dark_side", "LIGHT SIDE", "light  side", " Dark Side "})
            assertTrue(PlayNowSide.isSeatName(n), n);
        for (String n : new String[]{"Light Side 2", "Dark_Side2", "Light", "Dark", "Lightside", "DarkSider", "LS", "", null})
            assertFalse(PlayNowSide.isSeatName(n), String.valueOf(n));
    }

    @Test
    public void seatNamesRefusedEvenWithoutSignInService() {
        assertEquals(RegisterRequestHandler.NAME_RESERVED, RegisterRequestHandler.nameRefusal("Light_Side", null));
        assertEquals(RegisterRequestHandler.NAME_RESERVED, RegisterRequestHandler.nameRefusal("dark_side", null));
        assertNull(RegisterRequestHandler.nameRefusal("Light_Sid2", null));
        assertNull(RegisterRequestHandler.nameRefusal("Brandnew", null));
    }

    @Test
    public void seatNamesRefusedWithoutAskingTheService() {
        SiteNameCheck svc = service(200, "{\"ok\":true,\"available\":true}", true);
        assertEquals(RegisterRequestHandler.NAME_RESERVED, RegisterRequestHandler.nameRefusal("Light_Side", svc));
        assertTrue(_asked.isEmpty());
    }

    @Test
    public void heldNameRefused() {
        SiteNameCheck svc = service(200, "{\"ok\":true,\"available\":false,\"reason\":\"held\"}", true);
        assertEquals(RegisterRequestHandler.NAME_HELD, RegisterRequestHandler.nameRefusal("Oldname", svc));
        assertEquals(List.of("{\"name\":\"Oldname\"}"), _asked);
    }

    @Test
    public void takenAndReservedRefused() {
        assertEquals(RegisterRequestHandler.NAME_HELD, RegisterRequestHandler.nameRefusal("Luke",
                service(200, "{\"ok\":true,\"available\":false,\"reason\":\"taken\"}", true)));
        assertEquals(RegisterRequestHandler.NAME_RESERVED, RegisterRequestHandler.nameRefusal("Admin7",
                service(200, "{\"ok\":true,\"available\":false,\"reason\":\"reserved\"}", true)));
    }

    @Test
    public void availableNameAllowed() {
        assertNull(RegisterRequestHandler.nameRefusal("Light_Sid2",
                service(200, "{\"ok\":true,\"available\":true}", true)));
    }

    @Test
    public void failsClosed() {
        // unsigned answer, bad status, odd body, transport error
        assertEquals(RegisterRequestHandler.NAME_UNCHECKED, RegisterRequestHandler.nameRefusal("Brandnew",
                service(200, "{\"ok\":true,\"available\":true}", false)));
        assertEquals(RegisterRequestHandler.NAME_UNCHECKED, RegisterRequestHandler.nameRefusal("Brandnew",
                service(503, "{\"ok\":false}", true)));
        assertEquals(RegisterRequestHandler.NAME_UNCHECKED, RegisterRequestHandler.nameRefusal("Brandnew",
                service(200, "{\"ok\":true,\"available\":false,\"reason\":\"other\"}", true)));
        SiteNameCheck broken = new SiteNameCheck(URL, SECRET, (u, h, b) -> { throw new java.io.IOException("down"); });
        assertEquals(RegisterRequestHandler.NAME_UNCHECKED, RegisterRequestHandler.nameRefusal("Brandnew", broken));
        // a response signed with another key is not trusted
        SiteNameCheck forged = new SiteNameCheck(URL, SECRET, (u, h, b) -> {
            InternalAuthKeysV2 other = new InternalAuthKeysV2(Map.of("gemp", List.of("another-secret-0123456789-abcdefghijklmnop")));
            byte[] out = "{\"ok\":true,\"available\":true}".getBytes(StandardCharsets.UTF_8);
            return new SiteNameCheck.Response(200, other.sign("gemp", InternalAuthKeysV2.responseCanonical("gemp", h.get("X-Gemp-Nonce"), 200, out)), out);
        });
        assertEquals(RegisterRequestHandler.NAME_UNCHECKED, RegisterRequestHandler.nameRefusal("Brandnew", forged));
    }
}
