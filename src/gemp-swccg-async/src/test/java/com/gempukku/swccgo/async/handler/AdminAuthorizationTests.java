package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.service.LoggedUserHolder;
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Every action behind /play/admin.html refuses callers without the right role, before it does anything
 * (swccg-guild/work#1262). Signed out: 401. Signed-in member, or a role that does not cover the action: 403.
 */
class AdminAuthorizationTests {
    /** Every route in AdminRequestHandler.handleRequest, with the roles allowed past its check. */
    private static final String[][] ROUTES = {
            {"POST", "/clearcache", "a"}, {"POST", "/shutdown", "a"}, {"GET", "/motd/get", "a"}, {"POST", "/motd/update", "a"},
            {"POST", "/league/sealed/preview", "al"}, {"POST", "/league/sealed/create", "al"},
            {"POST", "/league/constructed/preview", "al"}, {"POST", "/league/constructed/create", "al"},
            {"POST", "/league/solodraft/preview", "al"}, {"POST", "/league/solodraft/create", "al"},
            {"GET", "/league/schedules", "al"}, {"POST", "/league/schedule/save", "al"}, {"POST", "/league/schedule/preview", "al"},
            {"POST", "/league/schedule/enable", "al"}, {"POST", "/league/schedule/delete", "al"}, {"POST", "/league/schedule/run", "al"},
            {"POST", "/league/addplayers", "al"},
            {"POST", "/collections/additems", "a"}, {"POST", "/collections/addcurrency", "a"}, {"POST", "/collections/additemstoall", "a"},
            {"POST", "/user/addflag", "a"}, {"POST", "/users/removeflag", "a"}, {"POST", "/users/findwithflag", "a"},
            {"POST", "/user/passwordreset", "a"}, {"POST", "/users/deactivate", "a"},
            {"POST", "/user/ban/permanent", "a"}, {"POST", "/users/ban/permanent", "a"}, {"POST", "/user/ban/temporary", "a"},
            {"POST", "/user/ban/acquit", "a"}, {"POST", "/users/detailedsearch", "a"},
            {"POST", "/settings/privategames", "a"}, {"POST", "/settings/aitables", "a"}, {"POST", "/settings/bonusabilities", "a"},
            {"POST", "/settings/stattracking", "a"}, {"POST", "/settings/newaccounts", "a"}, {"POST", "/settings/purgestattrackers", "a"},
            {"POST", "/league/deckcheck", "a"},
    };
    /** Role letters (Player.Type): a admin, l league admin, p playtesting admin, m commentary admin; u = ordinary member. */
    private static final String[] ROLES = {"a", "l", "p", "m"};

    private final LoggedUserHolder loggedUsers = new LoggedUserHolder();
    private final PlayerDAO playerDao = mock(PlayerDAO.class);
    private final HallServer hall = mock(HallServer.class);
    private final AdminRequestHandler handler;

    AdminAuthorizationTests() {
        Map<Type, Object> context = new HashMap<>();
        context.put(LoggedUserHolder.class, loggedUsers);
        context.put(PlayerDAO.class, playerDao);
        context.put(HallServer.class, hall);
        handler = new AdminRequestHandler(context);
        when(hall.getMOTD()).thenReturn("motd");
    }

    private String login(String name, String type) {
        when(playerDao.getPlayer(name)).thenReturn(new Player(7, name, "x", type, null, null, null, null));
        return "loggedUser=" + loggedUsers.logUser(name);
    }

    private FullHttpRequest request(String method, String uri, String cookie, String flag) {
        String form = flag == null ? "" : "flag=" + flag + "&login=nobody&logins=nobody";
        return method.equals("GET") ? Requests.get("/gemp-swccg-server/admin" + uri, cookie)
                : Requests.post("/gemp-swccg-server/admin" + uri, form, cookie);
    }

    /** The status the authorization check gives: 401 or 403 if refused, 0 if the call got past it. */
    private int status(String method, String uri, String cookie, String flag) {
        try {
            handler.handleRequest(uri, request(method, uri, cookie, flag), null, new RecordingResponseWriter(), "203.0.113.9");
            return 0;
        } catch (HttpProcessingException e) {
            return e.getStatus() == 401 || e.getStatus() == 403 ? e.getStatus() : 0;
        } catch (Exception e) {
            return 0; // later failures (no league service in this test, bad form) mean the check already let the caller through
        }
    }

    private static String flagFor(String uri) {
        return uri.contains("flag") ? "ADMIN" : null;
    }

    @Test
    void signedOutIsRefusedEverywhere() {
        List<String> wrong = new ArrayList<>();
        for (String[] r : ROUTES)
            if (status(r[0], r[1], null, flagFor(r[1])) != 401) wrong.add(r[1]);
        assertTrue(wrong.isEmpty(), "not 401 when signed out: " + wrong);
    }

    @Test
    void ordinaryMemberIsRefusedEverywhere() {
        String cookie = login("member1262", "u");
        List<String> wrong = new ArrayList<>();
        for (String[] r : ROUTES)
            if (status(r[0], r[1], cookie, flagFor(r[1])) != 403) wrong.add(r[1]);
        assertTrue(wrong.isEmpty(), "not 403 for an ordinary member: " + wrong);
    }

    @Test
    void eachRoleGetsOnlyItsOwnActions() {
        List<String> wrong = new ArrayList<>();
        for (String role : ROLES) {
            String cookie = login("role-" + role, role + "u");
            for (String[] r : ROUTES) {
                boolean allowed = r[2].contains(role);
                int s = status(r[0], r[1], cookie, flagFor(r[1]));
                if (allowed ? s != 0 : s != 403) wrong.add(role + " " + r[1] + " -> " + s);
            }
        }
        assertTrue(wrong.isEmpty(), "role boundary broken: " + wrong);
    }

    @Test
    void flagRoutesLetSubAdminsHandleOnlyTheirOwnFlag() {
        String playtestAdmin = login("pt-admin", "pu");
        String commentaryAdmin = login("cm-admin", "mu");
        String member = login("member-flags", "u");
        for (String uri : new String[]{"/user/addflag", "/users/removeflag", "/users/findwithflag"}) {
            assertEquals(0, status("POST", uri, playtestAdmin, "PLAYTESTER"), uri);
            assertEquals(403, status("POST", uri, playtestAdmin, "COMMENTATOR"), uri);
            assertEquals(403, status("POST", uri, playtestAdmin, "ADMIN"), uri);
            assertEquals(0, status("POST", uri, commentaryAdmin, "COMMENTATOR"), uri);
            assertEquals(403, status("POST", uri, commentaryAdmin, "PLAYTESTER"), uri);
            assertEquals(403, status("POST", uri, member, "PLAYTESTER"), uri);
            assertEquals(403, status("POST", uri, member, "COMMENTATOR"), uri);
        }
    }

    @Test
    void adminCanReadTheMessageOfTheDay() throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/motd/get", request("GET", "/motd/get", login("admin1262", "au"), null), null, rw, "203.0.113.9");
        assertEquals("motd", rw.body);
    }

    @Test
    void unknownAdminPathIs404() throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/addTables", request("POST", "/addTables", null, null), null, rw, "203.0.113.9");
        assertEquals(404, rw.status);
    }
}
