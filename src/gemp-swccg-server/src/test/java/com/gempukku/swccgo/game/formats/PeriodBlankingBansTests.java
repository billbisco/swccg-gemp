package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Legality on championship snapshots on either side of the published blankings. */
public class PeriodBlankingBansTests {
    private static SwccgoFormatLibrary formats;
    private static final String[] PERIODS = {
            "premiere_block_6_1", "premiere_block_7_1", "premiere_block_7_3",
            "premiere_block_8_2", "premiere_block_8_3", "premiere_block_9_1"};

    @BeforeClass
    public static void setUpClass() {
        formats = new SwccgoFormatLibrary(new SwccgCardBlueprintLibrary());
    }

    @Test
    public void MediumTransportBecomesBannedAtThe2011Snapshot() throws Exception {
        checkSnapshots("1230_119", 2);
    }

    @Test
    public void CaptainVerrackBlock3BecomesBannedAtThe2012Snapshot() throws Exception {
        checkSnapshots("1230_120", 3);
        // The later Block 8.2 card is a different printing, not the blanked Block 3 card.
        accepts("premiere_block_8_2", "1282_15");
        accepts("legacy", "1282_15");
    }

    @Test
    public void MercSunletBecomesBannedAtThe2012Snapshot() throws Exception {
        checkSnapshots("1210_175", 3);
    }

    @Test
    public void WipeThemOutBothHostsBecomeBannedAtThe2013Snapshot() throws Exception {
        checkSnapshots("1261_115", 4);
        checkSnapshots("1261_116", 4);
    }

    @Test
    public void AnotherPatheticLifeformBothHostsBecomeBannedAtThe2013Snapshot() throws Exception {
        checkSnapshots("1261_117", 4);
        checkSnapshots("1261_118", 4);
    }

    @Test
    public void HidingInTheGarbageIsNotBannedBeforeThe2014Snapshot() throws Exception {
        checkSnapshots("1220_124", 5);
    }

    @Test
    public void YouWillGoToTheDagobahSystemIsNotBannedBeforeThe2014Snapshot() throws Exception {
        checkSnapshots("1220_164", 5);
    }

    private static void checkSnapshots(String card, int firstBanned) throws Exception {
        for (int i = 0; i < PERIODS.length; i++) {
            if (i < firstBanned)
                accepts(PERIODS[i], card);
            else
                rejects(PERIODS[i], card, "banned card");
        }
        rejects("legacy", card, "banned card");
        accepts("anything_goes", card);
        rejects("open", card, "from set banned");
    }

    private static void accepts(String format, String card) throws DeckInvalidException {
        formats.getFormat(format).validateDeck(deck(card));
    }

    private static void rejects(String format, String card, String reason) throws Exception {
        try {
            formats.getFormat(format).validateDeck(deck(card));
            fail(format + " accepted " + card);
        } catch (DeckInvalidException expected) {
            assertTrue(format + ": " + expected.getMessage(), expected.getMessage().contains(reason));
        }
    }

    private static SwccgDeck deck(String card) {
        boolean shield = card.startsWith("1261_");
        boolean dark = card.equals("1261_115") || card.equals("1261_116");
        SwccgDeck deck = new SwccgDeck("period blanking boundary");
        if (shield)
            deck.addCardOutsideDeck(card);
        else
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(dark ? "1_175" : "1_28");
        return deck;
    }
}
