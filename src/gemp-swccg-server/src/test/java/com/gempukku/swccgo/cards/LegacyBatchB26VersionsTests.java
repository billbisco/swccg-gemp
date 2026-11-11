package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch B26 (Virtual Blocks 6-7, single-version families with an open defect; ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json): the 2012-13 PC card list spelled three cards differently from their
 * release sheets ("Wookie", "deploytment", "Aura"), so those texts are PC-E1 (the current card) and the sheet texts are
 * 22xx originals. Three current cards had a text that matched no printing (Remote Depot "same site or related site",
 * Boba Fett In Slave I (V) "unable", Commando Training "[Independent]"). Images are pinned in cardImageUrlTests.js.
 * 601_98 / 601_205 are the current classes of 1261_48 / 1262_18.
 */
public class LegacyBatchB26VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("601_98", "USED: Cancel Disarmed or Wookiee Strangle. OR Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle. LOST: Use 1 Force to ▼ (or retrieve) Vader. OR If opponent just initiated a battle against Vader defending alone at a site, his forfeit cannot be reduced."),
            Map.entry("1261_79", "Deploy on table. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Rebels (except Jedi or [Permanent Weapon] characters) ignore location deployment restrictions (and \"power -1\") in their game text, apply their Endor deployment modifiers at any location, and are immune to Bad Feeling Have I. Your Interrupts that retrieve exactly 1 Force (except Corellian Retort) are not stacked on Do They Have A Code Clearance?. Cannot be canceled. (Immune to Alter.)"),
            Map.entry("1263_9", "Deploy on table. Maul is lost. Once per character, when you deploy Aurra, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)"),
            Map.entry("1261_53", "Dark: May not deploy to Bespin, Coruscant, Kessel, Ralltiir, or Tatooine. Once per game, may deploy an Effect that deploys on a character or a device here from Reserve Deck; reshuffle. Light: Unless opponent's Sleen or matching operative at same or related site, those sites are immune to Ounee Ta."),
            Map.entry("601_205", "Permanent pilot is •Boba Fett, who provides ability of 3. Draws one battle destiny if not able to otherwise. Slave I's power may not be reduced by opponent. Immune to Eject! Eject! and attrition < 5."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2261_48", new String[]{"1261_48", "", "USED: Cancel Disarmed or Wookiee Strangle. OR Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle. LOST: Use 1 Force to ▼ (or retrieve) Vader. OR If opponent just initiated a battle against Vader defending alone at a site, his forfeit cannot be reduced."}),
            Map.entry("2261_79", new String[]{"1261_79", "", "Deploy on table. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Rebels (except Jedi or [Permanent Weapon] characters) ignore location deployment restrictions (and \"power -1\") in their game text, apply their Endor deployment modifiers at any location, and are immune to Bad Feeling Have I. Your Interrupts that retrieve exactly 1 Force (except Corellian Retort) are not stacked on Do They Have A Code Clearance?. Cannot be canceled. (Immune to Alter.)"}),
            Map.entry("2263_9", new String[]{"1263_9", "", "Deploy on table. Maul is lost. Once per character, when you deploy Aurra, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)"}));

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
    }

    @Test
    public void CurrentIdsResolveToTheCurrentClasses() {
        assertEquals(library.getSwccgoCardBlueprint("601_98").getGameText(), library.getSwccgoCardBlueprint("1261_48").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("601_205").getGameText(), library.getSwccgoCardBlueprint("1262_18").getGameText());
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(3, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getTitle(), card.getTitle());
        });
    }

    @Test
    public void ReleaseSheetSourcesNameTheSlipNumber() {
        assertEquals("Virtual Block 6.1 release sheet (2009-09-04), card #51", library.getSwccgoCardBlueprint("2261_48").getLegacyVersion().source());
        assertEquals("Virtual Block 6.1 release sheet (2009-09-04), card #78", library.getSwccgoCardBlueprint("2261_79").getLegacyVersion().source());
        assertEquals("Virtual Block 6.3 release sheet (released 2010-03-12), card #3-09", library.getSwccgoCardBlueprint("2263_9").getLegacyVersion().source());
    }
}
