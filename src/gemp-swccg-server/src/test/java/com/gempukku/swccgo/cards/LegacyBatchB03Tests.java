package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch B03 (Virtual Block 1 CardMaker images, ai-team-skills shared/gemp-legacy-cards/versions/batches.json)
 * plus Through The Force Things You Will See (V) reopened from B01: every current card carries its latest version's text
 * (its image is the CardMaker recreation of that text, pinned in cardImageUrlTests.js), every older version is its own
 * 22xx card in date order.
 */
public class LegacyBatchB03Tests {
    /** Latest version text of each current card (PC-E1 for the ten Virtual Block 1 cards, PC-E2 for 1220_156). */
    private static final Map<String, String> LATEST_TEXT = Map.ofEntries(
            Map.entry("1210_4", "Adds 2 to power of anything he pilots. Once during your control phase (twice if on Devastator) may use 1 Force to take any Imperial pilot of ability < 3 into hand from Reserve Deck; reshuffle."),
            Map.entry("1210_6", "Adds 3 to power of anything he pilots. May add 1 to Force drains at same Nal Hutta location. Danz is a matching pilot for any [Independent Starship] starship and, while piloting one, may not draw more than one battle destiny here (your battle destinies here may not be canceled or modified by opponent)."),
            Map.entry("1210_15", "During battle, may add 1 to your attrition against opponent for each opponent's character present. May not deploy to or board starfighters or enclosed vehicles."),
            Map.entry("1210_17", "Deploy -1 and forfeit +3 at a site. Once per turn, may peek at top card of opponent's Reserve Deck or Used Pile; may then shuffle that Reserve Deck or Used Pile. Immune to attrition < 3."),
            Map.entry("1210_26", "Deploys only on Tatooine. During your deploy phase, if with URoRRuR'R'R, may deploy a weapon with 'Tusken' in lore here from Reserve Deck; reshuffle; your weapon destiny draws for such weapons are +2. During your deploy phase, may move Ur'Ru'r as a regular move."),
            Map.entry("1210_30", "Deploy on table. Whever you verify opponent's Reserve Deck, may place a card there with 'podracer' in game text out of play or retrieve 1 Force."),
            Map.entry("1210_43", "Deploy on table. We Have A Prisoner is a Used Interrupt. TIEs are deploy +1 to Launch Bay. During your deploy phase, may deploy from Reserve Deck (or retrieve) one Besieged or a Launch Bay; reshuffle. Once per turn, when your Imperial captures (seizes) a character, opponent loses 1 Force. (Immune to Alter.)"),
            Map.entry("1210_44", "Deploy on your non-droid character present at a battleground site; character may not be carried from here or move. Target a Jedi at a site. You lose no more than 1 Force to Force drains at target's location. This Utinni Effect cancelled when reached by target."),
            Map.entry("1210_45", "Deploy on table. Once per game, if Alderaan 'blown away,' may target a system; unless opponent has a capital (or [Episode I]) starship there, Objectives treat that system as a Subjugated planet. Starships may not move the turn they are deployed (or carried) to Death Star. (Immune to Alter.)"),
            Map.entry("1210_56", "USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, may place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn."),
            Map.entry("1220_156", "Once per game, choose: if you are about to recirculate at the end of your turn, and you have fewer cards in Used Pile than Force Pile, place your Used Pile under your Force Pile instead. OR Place the top card of your Used Pile on top of your Force Pile."));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static SwccgCardBlueprint card(String id) {
        SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
        assertNotNull(id, card);
        return card;
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        LATEST_TEXT.forEach((id, text) -> {
            var card = card(id);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void EveryOlderVersionIsItsOwnCardAndSupersededByTheNext() {
        // original (sheet) -> latest for the ten Virtual Block 1 cards
        for (String id : new String[]{"1210_4", "1210_6", "1210_15", "1210_17", "1210_26", "1210_30", "1210_43", "1210_44", "1210_45", "1210_56"}) {
            var version = card("2" + id.substring(1)).getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, id, version.currentPrintingId());
            assertEquals(id, "", version.label());
            assertEquals(id, LocalDate.of(2009, 12, 7), version.printedOn());
        }
        // Through The Force Things You Will See (V): original 2009 -> PC-E1 card list 2012 -> PC-E2 PC forum errata 2012-11-19 (current)
        var original = card("2220_156").getLegacyVersion();
        var pcE1 = card("2220_1156").getLegacyVersion();
        assertEquals("", original.label());
        assertEquals("PC-E1", pcE1.label());
        assertEquals(original.supersededOn(), pcE1.printedOn());
        assertEquals(LocalDate.of(2012, 11, 19), pcE1.supersededOn()); // PC forum errata 2012-11-19 (Batch B36)
        assertEquals(CardSubtype.OUT_OF_PLAY, card("2220_1156").getCardSubtype());
        assertEquals(CardSubtype.LOST, card("1220_156").getCardSubtype());
    }
}
