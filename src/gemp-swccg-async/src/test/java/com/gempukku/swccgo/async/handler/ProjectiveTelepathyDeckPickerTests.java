package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.setup.*;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProjectiveTelepathyDeckPickerTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final SwccgoFormatLibrary FORMATS = new SwccgoFormatLibrary(LIBRARY);
    private GuestHandlerFixture f;
    private DeckDAO dao;
    private DeckRequestHandler handler;
    private String cookie;
    private SwccgDeck deck;

    @BeforeEach
    public void setup() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        cookie = f.member("Telepathy picker");
        dao = mock(DeckDAO.class);
        f.context.put(DeckDAO.class, dao);
        f.context.put(SwccgCardBlueprintLibrary.class, LIBRARY);
        f.context.put(SwccgoFormatLibrary.class, FORMATS);
        f.context.put(CollectionsManager.class, mock(CollectionsManager.class));
        handler = new DeckRequestHandler(f.context);
        deck = new SwccgDeck("Imported original");
        deck.addCard("5_149o");
        while (deck.getCards().size() < 60) deck.addCard("1_175");
        deck.setSourceCollection("default");
        when(dao.getAllDecksForPlayer(any(Player.class))).thenReturn(List.of(deck));
    }

    private RecordingResponseWriter list(String query) throws Exception {
        var writer = new RecordingResponseWriter();
        handler.handleRequest("/list", Requests.get("/gemp-swccg-server/deck/list?collection=default&" + query, cookie),
                f.context, writer, null);
        assertEquals(200, writer.status);
        return writer;
    }

    private String options(String value) {
        return "gameOptions=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Test
    public void ProjectiveTelepathyDeckPickerUsesChosenTextWithoutPoisoningNormalFormatIndex() throws Exception {
        assertEquals(0, list("format=open").xml.getElementsByTagName("darkDeck").getLength());
        String ordinaryIndex = deck.getFormatIndex();
        assertNotNull(ordinaryIndex);
        clearInvocations(dao);
        for (String choice : List.of("printed", "current")) {
            var response = list("format=open&" + options("{\"projectiveTelepathy\":\"" + choice + "\"}"));
            assertEquals(1, response.xml.getElementsByTagName("darkDeck").getLength());
            assertEquals(ordinaryIndex, deck.getFormatIndex());
            assertEquals("5_149o", deck.getCards().getFirst());
        }
        verify(dao, never()).updateDeckIndex(any(), anyString(), anyString(), any());
        assertEquals(0, list("format=open").xml.getElementsByTagName("darkDeck").getLength());
    }

    @Test
    public void ProjectiveTelepathyDeckPickerKeepsChoiceForOtherCasualDeckSizes() throws Exception {
        while (deck.getCards().size() < 80) deck.addCard("1_175");
        deck.setFormatIndex("unchanged");
        var response = list("format=open&anySize=true&" + options("{\"projectiveTelepathy\":\"current\"}"));
        var entry = (org.w3c.dom.Element) response.xml.getElementsByTagName("darkDeck").item(0);
        assertNotNull(entry);
        assertEquals("80", entry.getAttribute("deckSize"));
        assertEquals("unchanged", deck.getFormatIndex());
        verify(dao, never()).updateDeckIndex(any(), anyString(), anyString(), any());
    }

    @Test
    public void ProjectiveTelepathyDeckPickerHonorsAuthoritativeEventInsteadOfClientOverrides() throws Exception {
        var event = GameSetup.of(FORMATS.getFormat("open"), Map.of(GameOptions.PROJECTIVE_TELEPATHY, "printed"), GameOption.Scope.TOURNAMENT)
                .withPool(CardPool.of(null, null, List.of("5_149")));
        when(f.hall.getDeckPickerSetup(null, "event")).thenReturn(event);
        assertEquals(0, list("format=open&eventId=event&" + options("{\"projectiveTelepathy\":\"current\"}"))
                .xml.getElementsByTagName("darkDeck").getLength());
        var sizedEvent = GameSetup.of(FORMATS.getFormat("open"),
                Map.of(GameOptions.PROJECTIVE_TELEPATHY, "current", GameOptions.DECK_SIZE, "80"), GameOption.Scope.TOURNAMENT);
        when(f.hall.getDeckPickerSetup(null, "event")).thenReturn(sizedEvent);
        assertEquals(0, list("format=open&eventId=event&anySize=true&" + options("{\"deckSize\":\"60\"}"))
                .xml.getElementsByTagName("darkDeck").getLength());
        assertEquals(0, list("eventId=event&anySize=true").xml.getElementsByTagName("darkDeck").getLength());
        verify(dao, never()).updateDeckIndex(any(), anyString(), anyString(), any());
    }

    @Test
    public void ProjectiveTelepathyDeckPickerUsesTheTablesActualPoolAndChoice() throws Exception {
        var table = GameSetup.of(FORMATS.getFormat("open"), Map.of(GameOptions.PROJECTIVE_TELEPATHY, "current"), GameOption.Scope.CASUAL);
        when(f.hall.getDeckPickerSetup("table", null)).thenReturn(table);
        assertEquals(1, list("format=premiere&tableId=table").xml.getElementsByTagName("darkDeck").getLength());
        verify(dao, never()).updateDeckIndex(any(), anyString(), anyString(), any());
    }

    @Test
    public void ProjectiveTelepathyDeckPickerRejectsInvalidChoicesAndMissingEvents() {
        for (String query : List.of("format=open&" + options("{\"projectiveTelepathy\":\"random\"}"),
                "format=open&" + options("not-json"), "format=missing&" + options("{\"projectiveTelepathy\":\"printed\"}")))
            assertEquals(400, assertThrows(HttpProcessingException.class, () -> list(query)).getStatus());
        assertEquals(404, assertThrows(HttpProcessingException.class, () -> list("format=open&eventId=missing")).getStatus());
        assertEquals(400, assertThrows(HttpProcessingException.class, () -> list("format=open&eventId=x&tableId=y")).getStatus());
        assertEquals(0, assertDoesNotThrow(() -> list("format=premiere&" + options("{\"projectiveTelepathy\":\"printed\"}")))
                .xml.getElementsByTagName("darkDeck").getLength());
    }
}
