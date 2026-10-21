package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.GuestSeatPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** What a guest cookie lets you do in a game and its chat, and what it doesn't. */
public class GuestGameAccessTests {
    private GuestHandlerFixture f;
    private GuestRequestHandler guestHandler;
    private GameRequestHandler gameHandler;
    private ChatRequestHandler chatHandler;
    private String guestCookie;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        guestHandler = new GuestRequestHandler(f.context);
        gameHandler = new GameRequestHandler(f.context, null);
        chatHandler = new ChatRequestHandler(f.context, null);
        guestCookie = GuestIdentity.COOKIE + "=" + Requests.cookieValue(f.guestStart(guestHandler, null, "1.2.3.4").headers.get("set-cookie"));
    }

    private SwccgGameMediator mediator(String gameId) {
        SwccgGameMediator mediator = f.swccgoServer.getGameById(gameId);
        when(mediator.produceCardInfo(any(Player.class), anyInt())).thenAnswer(inv -> "info for " + ((Player) inv.getArgument(0)).getName());
        return mediator;
    }

    private RecordingResponseWriter cardInfo(String gameId, String cookie) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        gameHandler.handleRequest("/" + gameId + "/cardInfo",
                Requests.get("/gemp-swccg-server/game/" + gameId + "/cardInfo?cardId=5&participantId=whoever", cookie), f.context, rw, "1.2.3.4");
        return rw;
    }

    @Test
    public void GuestActsAsTheirSeatInTheirOwnGame() throws Exception {
        //test1: whatever participantId says, the guest acts as "Light Side" and nobody else.
        mediator("g1");
        RecordingResponseWriter rw = cardInfo("g1", guestCookie);
        assertEquals("info for Light Side", rw.body);
    }

    @Test
    public void GuestCookieOpensNoOtherGame() throws Exception {
        //test2: another guest's game, and a request with no cookie at all, are 401.
        f.guestStart(guestHandler, null, "5.6.7.8");
        mediator("g2");
        HttpProcessingException other = assertThrows(HttpProcessingException.class, () -> cardInfo("g2", guestCookie));
        assertEquals(401, other.getStatus());
        HttpProcessingException none = assertThrows(HttpProcessingException.class, () -> cardInfo("g1", null));
        assertEquals(401, none.getStatus());
    }

    @Test
    public void GuestCannotCancelOrUseTimerControls() throws Exception {
        //test3: cancel is for members only.
        SwccgGameMediator mediator = mediator("g1");
        HttpProcessingException cancel = assertThrows(HttpProcessingException.class, () -> gameHandler.handleRequest("/g1/cancel",
                Requests.post("/x", "participantId=Light%20Side", guestCookie), f.context, new RecordingResponseWriter(), "1.2.3.4"));
        assertEquals(401, cancel.getStatus());
        verify(mediator, never()).cancel(any());
    }

    @Test
    public void GuestCanConcede() throws Exception {
        //test4
        SwccgGameMediator mediator = mediator("g1");
        RecordingResponseWriter rw = new RecordingResponseWriter();
        gameHandler.handleRequest("/g1/concede", Requests.post("/x", "participantId=x", guestCookie), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        verify(mediator).concede(any(GuestSeatPlayer.class));
    }

    @Test
    public void AMemberIsStillThemselvesWithAGuestCookieToo() throws Exception {
        //test5: a login wins over a guest cookie (a member never acts as a guest seat).
        mediator("g1");
        RecordingResponseWriter rw = cardInfo("g1", f.member("luke") + "; " + guestCookie);
        assertEquals("info for luke", rw.body);
    }

    @Test
    public void GuestCannotUseTheHallChat() throws Exception {
        //test6: the hall's room is not a game room; a guest cookie is not a login.
        f.chatServer.createChatRoom("Game Hall", true, 15, null, true, false);
        HttpProcessingException exp = assertThrows(HttpProcessingException.class, () -> chatHandler.handleRequest("/Game Hall",
                Requests.get("/x", guestCookie), f.context, new RecordingResponseWriter(), "1.2.3.4"));
        assertEquals(401, exp.getStatus());
    }

    private List<String> readChat(String cookie) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        chatHandler.handleRequest("/Gameg1", Requests.get("/gemp-swccg-server/chat/Gameg1?participantId=x", cookie), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        List<String> lines = new ArrayList<>();
        NodeList messages = rw.xml.getDocumentElement().getElementsByTagName("message");
        for (int i = 0; i < messages.getLength(); i++) {
            Element m = (Element) messages.item(i);
            lines.add(m.getAttribute("from") + ": " + m.getTextContent());
        }
        return lines;
    }

    @Test
    public void GuestReadsChatButCannotPost() throws Exception {
        //test7: a spectator's coaching reaches the guest.
        ChatRoomMediator room = f.chatServer.getChatRoom(SwccgoServer.getChatRoomName("g1"));
        room.sendMessage("obiwan", "Deploy to the Cantina", false);
        assertTrue(readChat(guestCookie).contains("obiwan: Deploy to the Cantina"));

        //test8: posting is 403.
        HttpProcessingException exp = assertThrows(HttpProcessingException.class, () -> chatHandler.handleRequest("/Gameg1",
                Requests.post("/x", "participantId=x&message=hi", guestCookie), f.context, new RecordingResponseWriter(), "1.2.3.4"));
        assertEquals(403, exp.getStatus());
    }

    @Test
    public void MuteHidesSpectatorsFromThePlayerOnly() throws Exception {
        ChatRoomMediator room = f.chatServer.getChatRoom(SwccgoServer.getChatRoomName("g1"));

        //test9: muting posts a System message and is reported back.
        RecordingResponseWriter rw = new RecordingResponseWriter();
        guestHandler.handleRequest("/mute", Requests.post("/x", "gameId=g1&muted=true", guestCookie), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"chatMuted\":true"), rw.body);

        room.sendMessage("obiwan", "Deploy to the Cantina", false);
        room.sendMessage(Player.AI_BEGINNER_ID, "Good game", false);

        //test10: the guest sees System and the bot, not the spectator.
        List<String> guestView = readChat(guestCookie);
        assertTrue(guestView.stream().anyMatch(l -> l.startsWith("System: Light Side muted the chat")), guestView.toString());
        assertTrue(guestView.contains(Player.AI_BEGINNER_ID + ": Good game"), guestView.toString());
        assertFalse(guestView.contains("obiwan: Deploy to the Cantina"), guestView.toString());

        //test11: spectators still see everything.
        assertTrue(readChat(f.member("han")).contains("obiwan: Deploy to the Cantina"));

        //test12: unmuting brings spectators back.
        guestHandler.handleRequest("/mute", Requests.post("/x", "gameId=g1&muted=false", guestCookie), f.context, new RecordingResponseWriter(), "1.2.3.4");
        assertTrue(readChat(guestCookie).contains("obiwan: Deploy to the Cantina"));
    }

    @Test
    public void OnlyTheOwnerCanMute() throws Exception {
        //test13
        RecordingResponseWriter rw = new RecordingResponseWriter();
        guestHandler.handleRequest("/mute", Requests.post("/x", "gameId=g1&muted=true", f.member("han")), f.context, rw, "1.2.3.4");
        assertEquals(404, rw.status);
        assertFalse(f.games.get("g1").isChatMuted());
    }
}
