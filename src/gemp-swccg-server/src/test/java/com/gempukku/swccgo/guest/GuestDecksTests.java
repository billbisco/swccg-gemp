package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuestDecksTests {

    @Test
    public void GuestDecksAreTheTwoLegalPrecons() throws Exception {
        //test1: each precon is 60 cards of its own side
        //test2: both are legal in Premiere-A New Hope
        //test3: each call gives a fresh copy, so one game can't change another's deck
        for (PlayNowSide side : PlayNowSide.values()) {
            SwccgDeck deck = GuestDecks.deck(side);
            assertEquals(60, deck.getCards().size()); //test1
            assertEquals(side.getSide(), deck.getSide(VirtualTableScenario._cardLibrary)); //test1
            VirtualTableScenario._formatLibrary.getFormat("premiere_anh").validateDeck(deck); //test2
            assertNotSame(deck, GuestDecks.deck(side)); //test3
        }
        assertEquals(Side.LIGHT, PlayNowSide.DARK.opposite().getSide());
        assertTrue(GuestDecks.deck(PlayNowSide.LIGHT).getCards().contains("103_2")); // Red Leader In Red 1
    }

    @Test
    public void PlayNowChoicesComeFromShortCodes() {
        //test1: "light"/"dark" and "ozzel"/"yoda" are understood, in any case
        //test2: anything else (including Rando, which isn't deterministic) is refused
        assertEquals(PlayNowSide.LIGHT, PlayNowSide.fromCode("Light")); //test1
        assertEquals(PlayNowSide.DARK, PlayNowSide.fromCode("dark")); //test1
        assertEquals(PlayNowBot.OZZEL, PlayNowBot.fromCode("OZZEL")); //test1
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromCode(" yoda ")); //test1
        assertEquals(null, PlayNowSide.fromCode("grey")); //test2
        assertEquals(null, PlayNowBot.fromCode("rando")); //test2
        assertEquals(null, PlayNowBot.fromCode(null)); //test2
    }
}
