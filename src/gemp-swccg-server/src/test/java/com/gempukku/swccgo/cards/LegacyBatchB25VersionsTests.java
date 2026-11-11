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
 * Legacy batch B25 (Virtual Blocks 4-6, single-version cards with an open defect; ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json): the strict audit found 2012 PC card-list versions (PC-E1) of
 * Keder The Black (V) and Breached Defenses &amp; Molator. The current card carries the card-list text (typos as
 * published) and the release-sheet original is its own 22xx card. Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB25VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1250_9", "While at Senate: Sidious is politics +3; 'senators on Squabling Delegates is treated as 'Coruscant Guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile."),
            Map.entry("1261_29", "Deploy on table. Once per game, unless [Virtual] Crossfire on table (or out of play), may use 3 Force to [upload] an Effect that deploys on a location (except Expand The Empire). If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Your non-Dark Jedi characters without [Permanent Weapon] or [Maintenance] are immune to Goo Nee Tay. May not be canceled. (Immune to Alter.)"));
    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2250_9", new String[]{"1250_9", "", "While at Senate: Sidious is politics +3; 'senators' on Squabbling Delegates is treated as 'Coruscant Guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile."}),
            Map.entry("2261_29", new String[]{"1261_29", "", "Deploy on table. Once per game, unless [Virtual] Crossfire on table (or out of play), may use 3 Force to [upload] an Effect that deploys on a location (except Expand The Empire). If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Your non-Dark Jedi characters without [Permanent Weapon] or [Maintenance] are immune to Goo Nee Tay. May not be canceled. (Immune to Alter.)"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(2, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(2, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.of(2012, 7, 21), version.supersededOn());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getTitle(), card.getTitle());
        });
        assertEquals(LocalDate.of(2009, 12, 7), library.getSwccgoCardBlueprint("2250_9").getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2009, 9, 4), library.getSwccgoCardBlueprint("2261_29").getLegacyVersion().printedOn());
    }
}
