package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Legacy batch B38 (ai-team-skills shared/gemp-legacy-cards/versions, research R5: PC forum "Current Rulings/Errata"
 * t=36574): errata posted in 2014 become the current text (PC-E1) and the release-sheet texts become 22xx originals.
 * Rogue Squadron X-wing 2014-07-09, Corellian Engineering Corporation (V) 2014-03-04, Protocol Failure and Return Of The
 * Jedi 2014-04-16 (Return Of The Jedi also became unique), 4-LOM With Concussion Rifle (V) 2014-03-27 ("until end of
 * turn"). Images are pinned in cardImageUrlTests.js; behaviour in Card_1261_036_Tests, Card_1261_085_Tests and
 * Card_601_177_Tests. 601_177 is the current class of 1262_1.
 */
public class LegacyBatchB38VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1263_34", "May add 1 pilot and 1 astromech. While Rebel Aces on table, pilot is forfeit +2 and, if in battle, this card may not be lost before the damage segment. Matching starship for any Rogue Squadron pilot."),
            Map.entry("1240_79", "Deploy on table if [Virtual Block 4] Watch Your Step on table. X = number of your spaceport sites controlled by your Corellians. While you control Corellia, your total battle destiny is +X, and once per turn, if you are about to lose Force (except from your card), may reduce loss by X. [Immune to Alter]"),
            Map.entry("1261_36", "Deploy on a site to cancel a Force drain there. Vader is deploy -1 here. At end of your turn If you control this site, place this Effect in Lost Pile. OR At any time, target a unique (•) [Episode I] droid. Opponent chooses: deploy on droid (its game text is canceled), or place this Effect in Lost Pile (you may ▲ any one card). [Immune to Control]"),
            Map.entry("1261_85", "Deploy on a site to cancel a Force drain there. Skywalkers are power +1 here. At end of your turn If you control this site, place this Effect in Lost Pile. OR At any time, deploy on Sidious or [Virtual Block 3] Emperor. Opponent's Interrupts with “Emperor” in game text are canceled and Sidious may not place Jedi out of play. [Immune to Control]"),
            Map.entry("1262_1", "Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1 until end of turn). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck."));

    /** version id -> {current printing id, label, game text, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2263_34", new String[]{"1263_34", "", "May add 1 pilot and 1 astromech. While Rebel Aces on table, pilot is forfeit +2 and this starship may not be lost before the damage segment of battle. Matching starship for any Rogue Squadron pilot.", "2010-03-12", "2014-07-09"}),
            Map.entry("2240_79", new String[]{"1240_79", "", "Deploy on table if [Virtual Block 4] Watch Your Step on table. X = number of spaceport sites controlled by Corellians. While you control Corellia, your total battle destiny is +X, and once per turn, if you are about to lose Force (except from your card), may reduce loss by X. [Immune to Alter]", "2009-12-07", "2014-03-04"}),
            Map.entry("2261_36", new String[]{"1261_36", "", "Deploy on a site to cancel a Force drain there. Vader is deploy -1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, target a unique (•) [Episode I] droid. Opponent chooses: deploy on droid (its game text is canceled), or place this Effect in Lost Pile (you may ▲ any one card). [Immune to Control]", "2009-09-04", "2014-04-16"}),
            Map.entry("2261_85", new String[]{"1261_85", "", "Deploy on a site to cancel a Force drain there. Skywalkers are power +1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, deploy on Sidious or [Virtual Block 3] Emperor. Opponent's Interrupts with “Emperor” in game text are canceled and Sidious may not place Jedi out of play. [Immune to Control]", "2009-09-04", "2014-04-16"}),
            Map.entry("2262_1", new String[]{"1262_1", "", "Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck.", "2009-12-07", "2014-03-27"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(5, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
        assertEquals(library.getSwccgoCardBlueprint("601_177").getGameText(), library.getSwccgoCardBlueprint("1262_1").getGameText());
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
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.parse(v[3]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[4]), version.supersededOn());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getTitle(), card.getTitle());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getUniqueness(), card.getUniqueness());
        });
    }

    @Test
    public void ReturnOfTheJediIsUniqueSinceTheErrata() {
        assertEquals(Uniqueness.UNIQUE, library.getSwccgoCardBlueprint("1261_85").getUniqueness());
        assertEquals("Virtual Block 6.1 release sheet (2009-09-04), card #86 (printed without the uniqueness dot)",
                library.getSwccgoCardBlueprint("2261_85").getLegacyVersion().source());
    }
}
