package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
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
 * Legacy batch B28 (Virtual Blocks 7-9, single-version cards with an open defect; ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json): the strict audit found PC website card-list versions (PC-E1) of
 * Temporary Foothold, Death Mark &amp; Hutt Bounty, Het Nkik (V), Scoundrel's Charm and You May Start Your Landing (V).
 * The current card carries the card-list text (typos as published) and the release-sheet original is its own 22xx card.
 * Supreme Chancellor Valorum (V) (AI): its card-list row shows the base card's PC-E1 text, which the current AI printing
 * (an alias of 1281_30) already plays; 2281_30ai keeps the original. Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB28VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1274_20", "Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to two cards from Reserve Deck."),
            Map.entry("1281_10", "If on the Hunt on Table, deploy on a 'mark' (opponent's non-droid character at a battleground site). Target an assassin at another site. During any move phase, when reached by target, draw destiny and add target's power; opponent draws two destiny (choosing one) and adds 'mark's defense value. If your total is higher, the 'mark' is 'assassinated': opponent loses 2 Force (4 if a Jedi), the 'mark' is lost, and relocate Death Mark to your side of table. Otherwise, place Death Mark (and may place assassin it targets) in Used Pile. May not be canceled. (Immune to Alter.)"),
            Map.entry("1281_29", "Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = number of deserts you control with a Jawa."),
            Map.entry("1281_35", "Deploy from under Scoundrel's Luck on a scoundrel. Once per turn, may peek at the top three cards of your Force Pile and take one into hand; reshuffle. Once per turn, may use 1 Force to add or subtract 1 from opponent's just-drawn destiny."),
            Map.entry("1282_3", "If 5th Marker on table, deploy on table. During your control phase, opponent loses 1 Force for each marker site you control with an AT-AT (limit 1 unless [Virtual Block 2] Imperial Control on table). Once per game, may take Walker Garrison and/or 3rd Marker into hand from Reserve Deck; reshuffle. (Immune to Alter.)"),
            Map.entry("1281_30ai", "Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to ▲ a card with 'governor,' 'political,' or 'Valorum' in lore."));
    /** version id -> {current printing id, game text, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2274_20", new String[]{"1274_20", "Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to two cards from Reserve Deck.", "2011-12-16", "2012-07-21"}),
            Map.entry("2281_10", new String[]{"1281_10", "If On The Hunt on table, deploy on a 'mark' (opponent's non-droid character at a battleground site). Target an assassin at another site. During any move phase, when reached by target, draw destiny and add target's power; opponent draws two destiny (choosing one) and adds 'mark's defense value. If your total is higher, 'mark' is 'assassinated:' opponent loses 2 Force (4 if a Jedi), 'mark' is lost, and relocate Death Mark to your side of table. Otherwise, place Death Mark (and may place assassin it targets) in Used Pile. May not be canceled. (Immune to Alter.)", "2012-05-11", "2012-07-21"}),
            Map.entry("2281_29", new String[]{"1281_29", "Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = number of deserts you control with a Jawa.", "2012-05-11", "2012-07-21"}),
            Map.entry("2281_35", new String[]{"1281_35", "Deploy from under Scoundrel's Luck on a scoundrel. Once per turn, may peek at the top three cards of your Force Pile and take one into hand; reshuffle. Once per turn, may use 1 Force to add or subtract 1 from opponent's just-drawn destiny.", "2012-05-11", "2012-07-21"}),
            Map.entry("2282_3", new String[]{"1282_3", "If 5th Marker on table, deploy on table. During your control phase, opponent loses 1 Force for each marker site you control with an AT-AT (limit 1 unless [Virtual Block 2] Imperial Control on table). Once per game, may take Walker Garrison and/or 3rd Marker into hand from Reserve Deck; reshuffle. (Immune to Alter.)", "2012-09-14", "2012-12-12"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(6, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(5, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, "", version.label());
            assertEquals(id, v[1], card.getGameText());
            assertEquals(id, LocalDate.parse(v[2]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[3]), version.supersededOn());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getTitle(), card.getTitle());
        });
    }

    /** Lore as printed: Death Mark &amp; Hutt Bounty has no lore box (the Decipher lore was removed); Scoundrel's Charm "Sarcastic. Sardonic. Irreverent." */
    @Test
    public void LoreAsPrinted() {
        assertNull(library.getSwccgoCardBlueprint("1281_10").getLore());
        assertNull(library.getSwccgoCardBlueprint("2281_10").getLore());
        assertEquals("Sarcastic. Sardonic. Irreverent. Impertinent. Exasperating. Disrespectful. Outrageous and charming. Han was a scoundrel but Leia began to realize that she loved him.",
                library.getSwccgoCardBlueprint("1281_35").getLore());
    }
}
