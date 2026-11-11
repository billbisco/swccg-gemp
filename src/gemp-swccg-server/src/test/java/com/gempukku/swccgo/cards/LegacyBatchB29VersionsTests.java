package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B29 (Virtual Block 9 and Virtual Shields, single-version cards with an open defect; ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json). No family has a second published text, so no 22xx cards are
 * added. Current cards now carry the text the release-sheet slips print: Droideka "if total destiny" and
 * "another [Presence] droid" (9.2 slip, PC card image), Weapons Display (V) "inititate" (typo printed on the card
 * list, the Master List and the Virtual Shields sheet), and Republic At War / Aggressive Negotiations has no
 * [Episode I] icon (9.1 slip and PC card images, both sides; Separatist Uprising / At War With Itself does).
 */
public class LegacyBatchB29VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1292_1", "Permanent weapon is Twin Blaster Cannons (may target a character or vehicle; draw destiny; add 2 if targeting a vehicle; if total destiny > defense value, target hit and you may activate 1 Force). While with another [Presence] droid at a site, adds one battle destiny."),
            Map.entry("1200_13", "Plays on table. Whenever opponent excludes any character(s) from battle, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and a system) and opponent deployed a card with ability and did not inititate a battle, may retrieve 1 Force."),
            Map.entry("1292_9", "[Pilot] 2, 3: Moldy Crow. Permanent weapon is Blaster Pistol (may target a character for free; draw destiny; target hit and forfeit -3, if destiny +1 > defense value). Your characters here are immune to Trample and You Are Beaten."),
            Map.entry("1291_19", "Dark: May deploy Gunray from Reserve Deck; reshuffle. While two senators here, they use their game text as if in a senate majority. Light: While Gunray or opponent's senator here, your spies may not deploy here."),
            Map.entry("1291_17", "Dark: Dooku and Jango are power +1 here. Imprisoned captives released here may Escape. Light: Once per turn, if you control, may raise your converted Petranaki Arena to the top."));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheSlipText() {
        assertEquals(5, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void ObjectiveEpisodeIIconsAsPrinted() {
        for (String id : new String[]{"1291_69", "1291_69_BACK"}) {
            assertFalse(id, library.getSwccgoCardBlueprint(id).hasIcon(Icon.EPISODE_I));
        }
        for (String id : new String[]{"1291_29", "1291_29_BACK"}) {
            assertTrue(id, library.getSwccgoCardBlueprint(id).hasIcon(Icon.EPISODE_I));
        }
    }
}
