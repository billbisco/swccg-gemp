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
 * Legacy batch B05 (Virtual Block 2, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * version label and current printing. Images (CardMaker recreations of each text) are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB05VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1220_60", "USED: For remainder of turn, your bounty hunter escorting a captive may make two additional moves (for free if escorting Han). LOST: Your just drawn Carbon-Freezing (or bounty hunter's character weapon) destiny draw is +2."),
            Map.entry("1220_64", "Destiny +2 when drawn for lightsaber combat or weapon destiny. USED: Take Your Powers Are Weak, Old Man into hand from Reserve Deck; reshuffle. LOST: If you just lost a character armed with a non-[Permanent Weapon] lightsaber, lose 1 Force to take that character (or lightsaber) into hand."),
            Map.entry("1220_68", "Cancel Path Of Least Resistance. (Immune to Sense.) OR During your move phase, target opponent's character at an interior Cloud City site. Draw destiny. Relocate target to Weather Vane if destiny > target's ability. OR Relocate your character at an interior Cloud City site to Weather Vane (if 'hit,' character is first restored to normal)."),
            Map.entry("1220_83", "Deploy on your non-[Maintenance] AT-AT. May target a character or vehicle at same or adjacent site for free. Draw destiny. Target hit if destiny +1 (+2 if a vehicle) > defense value. If lost from table, place this weapon in Used Pile."),
            Map.entry("1220_92", "If deploying to opponent's mobile site, deploys as an Undercover spy (deploy -2). Nevar Yalnal may not target spies at Webb's site. Opponent's total influence here is -1 for each of your spies at sites."),
            Map.entry("1220_93", "Deploys free to Cloud City. You generate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here."),
            Map.entry("1220_121", "Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a stacked card into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). (Immune to Alter.)"),
            Map.entry("1220_125", "Deploy on non-[Episode I] C-3PO. Once per turn, you may take How Did We Get Into This Mess?, Thank The Maker, We're Doomed, or Mantellian Savrip into hand from Reserve Deck; reshuffle. If C-3PO present at a battleground, Savrip is immune to Alter. Thank The Maker is a Used Interrupt and treat '5' in its game text as '3'."),
            Map.entry("1220_139", "USED: An opponent's just drawn [Permanent Weapon] weapon destiny is -3. (Immune to Sense.) LOST: Once per game, retrieve up to 5 weapons (may take one retireved card into hand)."),
            Map.entry("601_167", "Cancel All Too Easy, Stunning Leader (where your Jedi participating), Imperial Barrier (targeting your Jedi), or You Are Beaten (except when canceling Uncontrollable Fury). (Immune to Sense.) OR Once per game, if opponent just initiated a battle, you may take the first weapons segment action."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2220_60", new String[]{"1220_60", "", "USED: For reminder of turn, your bounty hunter escorting a captive may make two additional moves (for free if escorting Han). LOST: Your just drawn Carbon-Freezing (or bounty hunter's character weapon) destiny draw is +2."}),
            Map.entry("2220_64", new String[]{"1220_64", "", "Destiny +2 with drawn for lightsaber combat or weapon destiny. USED: Take Your Powers Are Weak, Old Man into hand from Reserve Deck; reshuffle. LOST: If you just lost a character armed with a non-[Permanent Weapon] lightsaber, lose 1 Force to take that character (or lightsaber) into hand."}),
            Map.entry("2220_1064", new String[]{"1220_64", "PC-E1", "Destiny +2 when drawn for weapon or lightsaber combat destiny. USED: Take Your Powers Are Weak, Old Man into hand from Reserve Deck; reshuffle. LOST: If you just lost a character armed with a non-[Permanent Weapon] lightsaber, lose 1 Force to take that character (or lightsaber) into hand."}),
            Map.entry("2220_68", new String[]{"1220_68", "", "Cancel Path Of Least Resistance. (Immune to Sense.) OR During your move phase, target opponent's character at an interior Cloud City site. Draw destiny. Relocate target to Weather Vane if destiny > target's ability. OR Relocate your character at an interior Cloud City site to Weather Vane."}),
            Map.entry("2220_83", new String[]{"1220_83", "", "Deploy on your non-[Maintenance] AT-AT. May target a character or vehicle at same or adjacent site for free. Draw destiny. Target hit if destiny +1 (+2 if a vehicle) > defense value. If just lost from table, place this weapon in Used Pile."}),
            Map.entry("2220_92", new String[]{"1220_92", "", "If deploying to opponent's mobile site, deploys -2 as an Undercover spy. Nevar Yalnal may not target spies here. Opponent's total influence here is -1 for each of your spies at sites."}),
            Map.entry("2220_93", new String[]{"1220_93", "", "Deploys free to Cloud City. You generate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here."}),
            Map.entry("2220_121", new String[]{"1220_121", "", "Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a stacked card into hand to prevent players from drawing more than one battle destiny (those battle destiny draws may not be modified or canceled). (Immune to Alter.)"}),
            Map.entry("2220_1121", new String[]{"1220_121", "PC-E1", "Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a card here into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). (Immune to Alter.)"}),
            Map.entry("2220_125", new String[]{"1220_125", "", "Deploy on non-[Episode I] C-3PO. Once per turn, you may take How Did We Get Into This Mess?, Mantellian Savrip, Thank The Maker, or We're Doomed into hand from Reserve Deck; reshuffle. If C-3PO present at a battleground, Savrip is immune to Alter. Thank The Maker is a Used Interrupt and treat '5' in its game text as '3'."}),
            Map.entry("2220_139", new String[]{"1220_139", "", "USED: An opponent's just drawn [Permanent Weapon] weapon destiny is -3. (Immune to Sense.) LOST: Once per game, retrieve up to five weapons (may take one retrieved card into hand)."}),
            Map.entry("2220_145", new String[]{"1220_145", "", "Cancel All Too Easy, Imperial Barrier (targeting your Jedi), Stunning Leader (where your Jedi participating), or You Are Beaten (except when canceling Uncontrollable Fury). [Immune to Sense] OR Once per game, if opponent just initiated a battle, you may take the first weapons segment action."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(10, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(12, VERSIONS.size());
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
    public void ReleaseYourAngerAndEvacuationControlHaveTwoOlderVersions() {
        assertEquals("2220_1064", LegacyVersion.versionIdOf("1220_64", 1));
        assertEquals("2220_1121", LegacyVersion.versionIdOf("1220_121", 1));
        assertEquals("1220_64", LegacyVersion.currentPrintingIdOf("2220_1064"));
        assertEquals("1220_121", LegacyVersion.currentPrintingIdOf("2220_1121"));
    }
}
