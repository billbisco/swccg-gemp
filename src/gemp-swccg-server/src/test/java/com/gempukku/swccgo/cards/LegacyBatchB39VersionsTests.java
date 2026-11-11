package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B39 (Bill's rulings of 2026-11-10, ai-team-skills shared/gemp-legacy-cards/RULINGS.md): rescinded errata
 * are versions (Tarfful, Wookiee Insurgent PC-E2 2272_1018 and Wookiee Guide (V) PC-E1 2250_68, posted 2013-04-13 and
 * rescinded 2013-04-15); Communing's original is reconstructed (2273_33 = PC-E1 without "I Can't Believe He's Gone is
 * canceled.") and so is Field Dressing's (2271_2038 = PC-E1 without "and, if 'hit,' is restored to normal"); He Is Not Ready / Eject! Eject!: the "&" titles are the originals (2240_26 / 2240_81) and the short titles
 * the errata of 2013-06-14 (current cards); one Galen Marek, Starkiller AI version (2240_8ai retired behind an alias);
 * Wookiee Slaving Operation's 7 side errata of 2014-07-23 (originals 2282_10 / 2282_10_BACK). Images are pinned in
 * cardImageUrlTests.js.
 */
public class LegacyBatchB39VersionsTests {
    /** version id -> {current printing id, label, game text, printed on, superseded on, printed title or null} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2272_1018", new String[]{"1272_18", "PC-E2", "\"Occupy\" on Forest Depths is treated as \"control.\" Non-unique Wookiees here are Heroic. Once per game, when deployed, may 'roar' to take one card into hand from Used Pile (reshuffle) and/or stack a Wookiee (as if just lost) from hand on Grrrghrrrgh!.", "2013-04-13", "2013-04-15", null}),
            Map.entry("2250_68", new String[]{"1250_68", "PC-E1", "Stack a Wookiee (as if just lost) from your Lost Pile on Grrrghrrrgh!. OR Deploy a Wookiee stacked on Grrrghrrrgh! to Kashyyyk (as if from hand). OR Once per game, draw X cards from top of Reserve deck, where X = number of cards stacked on Grrrghrrrgh!.", "2013-04-13", "2013-04-15", null}),
            Map.entry("2273_33", new String[]{"1273_33", "", "If your starting location has exactly two [Light Side], play instead of a Starting Interrupt. [download] two always [Immune to Alter] Effects. Deploy on table; [upload] and stack a Jedi here. Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are 'communing' and considered out of play. You generate +1 Force for each card stacked here. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.", "2011-06-03", "2011-06-22", null}),
            Map.entry("2240_26", new String[]{"1240_26", "", null, "2009-12-07", "2013-06-14", "He Is Not Ready & Imperial Propaganda"}),
            Map.entry("2240_81", new String[]{"1240_81", "", null, "2009-12-07", "2013-06-14", "Eject! Eject! & Imperial Atrocity"}),
            Map.entry("2271_2038", new String[]{"1271_38", "", "If you just forfeited a character of ability < 5 present at a site, deploy on that character (who remains at same site). Character is forfeit = 0 (even if forfeit may not normally be reduced). May play First Aid to place this card in Used Pile. Immune to your Control.", "2010-06-18", "2010-07-26", null}),
            Map.entry("2282_10", new String[]{"1282_10", "", null, "2012-09-14", "2014-07-23", null}),
            Map.entry("2282_10_BACK", new String[]{"1282_10_BACK", "", "While this side up, whenever you 'enslave' a character, opponent must choose to use 2 Force or lose 1 Force. Once per turn, during battle may add or subtract up to X from your just drawn destiny, where X = the number of Kashyyyk locations you control with a slaver. Once per turn, if you just retrieved Force during battle, may take a slaver, starship, or vehicle retrieved into hand.\nFlip this card if opponent controls two Kashyyyk battlegrounds.", "2012-09-14", "2014-07-23", null}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(8, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertEquals(id, v[2] != null ? v[2] : current.getGameText(), card.getGameText());
            assertEquals(id, LocalDate.parse(v[3]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[4]), version.supersededOn());
            assertEquals(id, v[5], card.getPrintedTitle());
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getUniqueness(), card.getUniqueness());
        });
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertNull(library.getSwccgoCardBlueprint("1272_18").getLegacyVersion());
        assertFalse(library.getSwccgoCardBlueprint("1272_18").getGameText().contains("as if just lost"));
        assertFalse(library.getSwccgoCardBlueprint("1250_68").getGameText().contains("as if just lost"));
        assertEquals("He Is Not Ready", library.getSwccgoCardBlueprint("1240_26").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1240_26").hasVirtualSuffix());
        assertTrue(library.getSwccgoCardBlueprint("1240_26").getGameText().contains("That [Maintenance] card is power -3, may not apply its ability"));
        assertTrue(library.getSwccgoCardBlueprint("1240_81").getGameText().contains("That [Maintenance] card is power -3, opponent must use 1 Force"));
        assertTrue(library.getSwccgoCardBlueprint("1282_10_BACK").getGameText().endsWith("Flip this card if opponent controls two Kashyyyk locations."));
        assertEquals(library.getSwccgoCardBlueprint("601_29_BACK").getGameText(), library.getSwccgoCardBlueprint("1282_10_BACK").getGameText());
    }

    @Test
    public void TarffulRenameIsDatedBeforeTheRescindedErrata() {
        assertEquals(LocalDate.of(2013, 1, 29), library.getSwccgoCardBlueprint("2272_18").getLegacyVersion().supersededOn());
    }

    @Test
    public void GalenAiHasOneVersion() {
        SwccgCardBlueprint retired = library.getSwccgoCardBlueprint("2240_8ai");
        assertNotNull(retired);
        assertNull(retired.getLegacyVersion());
        assertEquals(library.getSwccgoCardBlueprint("1240_8ai").getGameText(), retired.getGameText());
        assertTrue(library.getSwccgoCardBlueprint("1240_8ai").getGameText().contains("while with Maris Brood"));
    }

    @Test
    public void BoostersStarDestroyerPrintedTitleFollowsTheSheetHeading() {
        // Legacy T1 (Bill, 2026-11-10 late): the 8.1 sheet prints "Booster's Star Destroyer (V)", so the "(V)" is part of the title.
        assertEquals("Booster's Star Destroyer (V)", library.getSwccgoCardBlueprint("2281_45").getPrintedTitle());
    }
}
