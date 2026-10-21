package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The CSRF rule for the guest endpoints, through the real router: a state-changing guest request must come from one of
 * our pages (Origin, else Referer). One with neither header is refused, unlike the rest of the API.
 */
public class GuestOriginTests {
    private static final String START = "/gemp-swccg-server/guest/start";
    private GuestHandlerFixture f;
    private RootUriRequestHandler root;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        root = new RootUriRequestHandler(f.context, null);
    }

    private RecordingResponseWriter postStart(String origin, String referer) throws Exception {
        FullHttpRequest request = Requests.post(START, "side=light&bot=ozzel", null);
        if (origin != null)
            request.headers().set("Origin", origin);
        if (referer != null)
            request.headers().set("Referer", referer);
        RecordingResponseWriter rw = new RecordingResponseWriter();
        root.handleRequest(START, request, f.context, rw, "1.2.3.4");
        return rw;
    }

    private static int refusedStatus(ThrowingRequest call) {
        return assertThrows(HttpProcessingException.class, call::run).getStatus();
    }

    private interface ThrowingRequest {
        void run() throws Exception;
    }

    @Test
    public void GuestStartFromOurPageIsAccepted() throws Exception {
        //test1: our own Origin
        assertEquals(200, postStart("https://test.swccg.com", null).status);
        //test2: no Origin, but a Referer from our own page
        assertEquals(200, postStart(null, "https://test.swccg.com/gemp-swccg/guest.html?side=light&bot=ozzel").status);
    }

    @Test
    public void GuestStartFromAnywhereElseIsRefused() {
        //test1: no Origin and a foreign Referer
        assertEquals(403, refusedStatus(() -> postStart(null, "https://evil.example/page.html")));
        //test2: neither header (a script, or a stripped request): refused for guest endpoints
        assertEquals(403, refusedStatus(() -> postStart(null, null)));
        //test3: a foreign Origin is still refused, whatever the Referer says
        assertEquals(403, refusedStatus(() -> postStart("https://evil.example", "https://test.swccg.com/gemp-swccg/guest.html")));
        //test4: a sandboxed page's "null" Origin is refused
        assertEquals(403, refusedStatus(() -> postStart("null", null)));
    }

    @Test
    public void TheRestOfTheApiStillLetsNonBrowserClientsThrough() throws Exception {
        //test1: a POST with neither header to a non-guest endpoint gets past the origin check, as before (here to a 404)
        FullHttpRequest request = Requests.post("/gemp-swccg-server/nosuchendpoint", "", null);
        RecordingResponseWriter rw = new RecordingResponseWriter();
        try {
            root.handleRequest("/gemp-swccg-server/nosuchendpoint", request, f.context, rw, "1.2.3.4");
            assertEquals(404, rw.status);
        } catch (HttpProcessingException e) {
            assertEquals(404, e.getStatus());
        }
    }
}
