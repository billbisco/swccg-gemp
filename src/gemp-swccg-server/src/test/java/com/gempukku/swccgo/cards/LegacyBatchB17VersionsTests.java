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
 * Legacy batch B17 (Virtual Block 6, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest text and each older version is its own 22xx card with its text, version label,
 * current printing and dates. Images are pinned in cardImageUrlTests.js. 601_92 / 601_51 are the current classes of
 * 1261_46 / 1261_97. Grotto Werribee (V), Storm Clouds and Phylo Gandish (V) have one published text (no 22xx card):
 * the AR 2.0 "(except battle destiny)" errata of Grotto / Phylo has no published earlier text (held for Bill), and the
 * Storm Clouds "PC-E1" of the record was a Master List text-layer misread of the same slip.
 */
public class LegacyBatchB17VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(            Map.entry("601_92", "Lose 1 Force to ▲ (or retrieve) Juno Eclipse. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0)."),
            Map.entry("1261_56", "May add Juno Eclipse as pilot. TIEs are immune to Don't Get Cocky. Once per turn, may use 1 Force to ▲ a Black Squadron TIE. Draws one battle destiny if not able to otherwise. Immune to attrition < 5."),
            Map.entry("1261_63", "If in hand, may place her under Reserve Deck. May be targeted instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). May ▼ Reflection. Immune to attrition < 4."),
            Map.entry("1261_67", "Jabba's Prize is a Light Side card and does not count towards your deck limit (but subtracts one from the number of cards you may place under your Starting Effect). Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1."),
            Map.entry("1261_81", "Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)"),
            Map.entry("1261_93", "USED: Cancel Disarmed or Stunning Leader. OR If a battle was just initiated, ▼ (or deploy from hand) a character weapon on your participating character. LOST: Use 1 Force to ▼ (or retrieve) non-[Reflections 3] Obi-Wan. OR If opponent just initiated a battle against non-[Reflections 3] Obi-Wan alone at a site, his forfeit cannot be reduced."),
            Map.entry("601_51", "USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand."));
    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2261_46", new String[]{"1261_46", "", "Lose 1 Force to ▲ (or retrieve) Black Leader. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0)."}),
            Map.entry("2261_1046", new String[]{"1261_46", "PC-E1", "Lose 1 Force to take Black Leader into hand from Reserve Deck (or retrieve); reshuffle. OR During battle where you have no Dark Jedi or droids and only two participating cards with ability (a male and a female), add one destiny to total power (and attrition against you = 0)."}),
            Map.entry("2261_56", new String[]{"1261_56", "", "May add Black Leader as pilot. TIEs are immune to Don't Get Cocky. Once per turn, may use 1 Force to ▲ a Black Squadron TIE. Draws one battle destiny if not able to otherwise. Immune to attrition < 5."}),
            Map.entry("2261_63", new String[]{"1261_63", "", "If in hand, may place her under Reserve Deck. May be deployed instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). May ▼ Reflection. Immune to attrition < 4."}),
            Map.entry("2261_67", new String[]{"1261_67", "", "Jabba's Prize is a Light Side card and does not count towards your deck limit (but subtracts one from the number of cards you may place under your Starting Effect). Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1."}),
            Map.entry("2261_81", new String[]{"1261_81", "", "Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)"}),
            Map.entry("2261_93", new String[]{"1261_93", "", "USED: Cancel Disarmed or Stunning Leader. OR If a battle was just initiated, ▼ (or deploy from hand) a character weapon on your participating character. LOST: Use 1 Force to ▼ (or retrieve) non-[Reflections 3] Obi-Wan. OR If opponent just initiated a battle against non-[Reflections 3] Obi-Wan alone at a site, his forfeit cannot be reduced."}),
            Map.entry("2261_97", new String[]{"1261_97", "", "USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(7, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
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
            assertEquals(id, v[2], card.getGameText());
        });
    }

    @Test
    public void DatesFollowTheRecord() {
        // every original is on the first Virtual Block 6.1 sheet (official text 2009-09-04), Incom included
        for (String id : new String[]{"2261_46", "2261_56", "2261_63", "2261_67", "2261_81", "2261_93", "2261_97"})
            assertEquals(id, LocalDate.of(2009, 9, 4), library.getSwccgoCardBlueprint(id).getLegacyVersion().printedOn());
        // PC website card list by 2012-07-21 (Daughter Of Skywalker: also AR 2.0 and the Master List)
        for (String id : new String[]{"2261_46", "2261_67", "2261_81", "2261_97"})
            assertEquals(id, LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn());
        // Daughter Of Skywalker: PC forum errata of 2010-10-08 (Batch B37)
        assertEquals(LocalDate.of(2011, 7, 14), library.getSwccgoCardBlueprint("2261_93").getLegacyVersion().supersededOn()); // retitle (B41)
        assertEquals(LocalDate.of(2010, 10, 8), library.getSwccgoCardBlueprint("2261_63").getLegacyVersion().supersededOn());
        // Master List 2013-04-27 (Juno Eclipse)
        assertEquals(LocalDate.of(2013, 4, 27), library.getSwccgoCardBlueprint("2261_56").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2261_1046").getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), library.getSwccgoCardBlueprint("2261_1046").getLegacyVersion().supersededOn());
    }

    @Test
    public void TitlesAndLoreAsPrinted() {
        assertEquals("Krayt Dragon Howl & Armed And Dangerous", library.getSwccgoCardBlueprint("2261_93").getDisplayTitle());
        assertEquals("\"I don't know how we're gonna get out of this one.\"", library.getSwccgoCardBlueprint("601_92").getLore());
        assertEquals(library.getSwccgoCardBlueprint("601_92").getLore(), library.getSwccgoCardBlueprint("2261_46").getLore());
        assertEquals(library.getSwccgoCardBlueprint("601_92").getLore(), library.getSwccgoCardBlueprint("2261_1046").getLore());
        assertEquals("\"It's not wise to upset a Wookiee.\" \"But sir, nobody worries about upsetting a droid.\" \"That's 'cause a droid don't pull people's arms out of their sockets when they lose.\"", library.getSwccgoCardBlueprint("601_51").getLore());
        assertEquals(library.getSwccgoCardBlueprint("601_51").getLore(), library.getSwccgoCardBlueprint("2261_97").getLore());
    }
}
