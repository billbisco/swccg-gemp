package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Keyword;
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
 * Legacy batch B18 (Virtual Block 7, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json,
 * record legacy-versions-v4.json): the current card carries the latest version's text and every older version is its own
 * 22xx card. Grievous, Hunter Of Jedi (printed as Cyborg Commander) and Insidious Prisoner gain a middle version, the
 * 2012 PC card-list text (PC-E1: 2271_1001, 2273_1013); the current cards are the Master List texts (PC-E2).
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB18VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1271_1", "[Virtual Block 5] Dark Jedi Lightsaber may deploy on Grievous. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4."),
            Map.entry("1271_38", "If you just forfeited a character of ability < 5 present at a site, return that character to the same site from your Lost Pile and deploy this card on him or her. Character is forfeit = 0 (even if forfeit may not be normally reduced). May play First Aid to place this card in Used Pile. Immune to your Control."),
            Map.entry("1272_5", "Deploy on table; take into hand from Reserve Deck and stack a non-lightsaber weapon (without 'lost' in game text) here; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), at end of battle, or if it is about to leave table, be stolen, or become inactive. (Immune to Alter.)"),
            Map.entry("1272_22", "Deploy on table; take and stack a non-lightsaber weapon (without 'lost' in game text) here from Reserve Deck; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), at end of battle, or if it is about to leave table, be stolen, or become inactive. (Immune to Alter.)"),
            Map.entry("1272_23", "Deploy on table. Canceled if your Senate or [Reflections 2] objective on table. Unless your character of ability > 4 at a site, Rogue Squadron pilots are power +1 (power +2 if ability < 3), and armor = 4. (Immune to Alter.)"),
            Map.entry("1273_6", "Shockstaff deploys and fires for free on this droid. While on Blockade Flagship or Coruscant (or with Grievous), this droid is Heroic and power +2. Your characters here are immune to Ambush, Clash Of Sabers, and attrition < 4."),
            Map.entry("601_155", "Deploy on Galen, Grievous or your female of ability > 3. Character is immunity to attrition +2 (and power +2 if female). May add 1 to Force drain where present. This device lost if character Disarmed."),
            Map.entry("1273_13", "If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); before drawing a starting hand opponent may deploy a Jedi and/or a lightsaber here from Reserve Deck; reshuffle. Evil Is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2271_1", new String[]{"1271_1", "", "[Virtual Block 5] Dark Jedi Lightsaber may deploy on Cyborg Commander. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4."}),
            Map.entry("2271_1001", new String[]{"1271_1", "PC-E1", "[Virtual Block 5] Dark Jedi Lightsaber may deploy on Cyborg. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws are +1 here and Cyborg Commander's forfeit may not be reduced. Immune to attrition < 4."}),
            Map.entry("2271_38", new String[]{"1271_38", "PC-E2", "If you just forfeited a character of ability < 5 present at a site, deploy on that character (who remains at same site and, if 'hit,' is restored to normal). Character is forfeit = 0 (even if its forfeit may not normally be reduced). May play First Aid to place this card in Used Pile. Immune to your Control."}),
            Map.entry("2271_1038", new String[]{"1271_38", "PC-E1", "If you just forfeited a character of ability < 5 present at a site, deploy on that character (who remains at same site and, if 'hit,' is restored to normal). Character is forfeit = 0 (even if forfeit may not normally be reduced). May play First Aid to place this card in Used Pile. Immune to your Control."}),
            Map.entry("2272_5", new String[]{"1272_5", "", "Deploy on table; take into hand from Reserve Deck and stack a non-lightsaber weapon (without 'lost' in game text) here; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)"}),
            Map.entry("2272_22", new String[]{"1272_22", "", "Deploy on table; take and stack a non-lightsaber weapon (without 'lost' in game text) here from Reserve Deck; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)"}),
            Map.entry("2272_23", new String[]{"1272_23", "", "Deploy on table. Canceled if your Senate or [Reflections 2] objective on table. Rogue Squadron pilots are spies, and, unless your character of ability > 4 at a site, power +2, armor = 4, immune to attrition < 4, and weapon destiny draws targeting them are -1 (may not be further reduced). (Immune to Alter.)"}),
            Map.entry("2273_6", new String[]{"1273_6", "", "Shockstaff deploys and fires for free on this droid. While on Blockade Flagship or Coruscant (or with Cyborg Commander), this droid is Heroic and power +2. Your characters here are immune to Ambush, Clash Of Sabers, and attrition < 4."}),
            Map.entry("2273_11", new String[]{"1273_11", "", "Deploy on Cyborg Commander, Galen, or your female of ability > 3. Character is immunity to attrition +2 (and power +2 if female). May add 1 to Force drain where present. This device lost if character Disarmed."}),
            Map.entry("2273_13", new String[]{"1273_13", "", "If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); opponent may deploy a Jedi and/or a lightsaber here. Evil Is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site."}),
            Map.entry("2273_1013", new String[]{"1273_13", "PC-E1", "If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); opponent may deploy a Jedi and/or a lightsaber here. Evil is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, it a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(8, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(11, VERSIONS.size());
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
    public void GrievousHasThreeVersions() {
        assertEquals("2271_1001", LegacyVersion.versionIdOf("1271_1", 1));
        LegacyVersion original = library.getSwccgoCardBlueprint("2271_1").getLegacyVersion();
        LegacyVersion cardList = library.getSwccgoCardBlueprint("2271_1001").getLegacyVersion();
        assertEquals(LocalDate.of(2012, 7, 21), original.supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), cardList.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), cardList.supersededOn());
        for (String id : new String[]{"2271_1", "2271_1001"}) {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertEquals(id, "Cyborg Commander, Hunter Of Jedi", card.getPrintedTitle());
            assertEquals(id, "Leader. \"He's no Jedi.\"", card.getLore());
            assertTrue(id, card.hasKeyword(Keyword.LEADER));
            assertFalse(id, card.hasKeyword(Keyword.COMMANDER));
        }
        SwccgCardBlueprint current = library.getSwccgoCardBlueprint("1271_1");
        assertEquals("Commander. Leader.", current.getLore());
        assertTrue(current.hasKeyword(Keyword.COMMANDER));
    }

    @Test
    public void InsidiousPrisonerHasThreeVersions() {
        assertEquals("2273_1013", LegacyVersion.versionIdOf("1273_13", 1));
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2273_13").getLegacyVersion().supersededOn());
        LegacyVersion cardList = library.getSwccgoCardBlueprint("2273_1013").getLegacyVersion();
        assertEquals(LocalDate.of(2012, 7, 21), cardList.printedOn());
        assertEquals(LocalDate.of(2013, 7, 24), cardList.supersededOn()); // PC forum errata (legacy batch B37)
    }

    @Test
    public void ErrataDatesFollowTheRecord() {
        assertEquals(LocalDate.of(2011, 1, 28), library.getSwccgoCardBlueprint("2272_5").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2011, 1, 28), library.getSwccgoCardBlueprint("2272_22").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2011, 6, 22), library.getSwccgoCardBlueprint("2272_23").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2010, 7, 28), library.getSwccgoCardBlueprint("2271_1038").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2011, 8, 26), library.getSwccgoCardBlueprint("2271_38").getLegacyVersion().supersededOn());
    }

    @Test
    public void LocationTextsAsPrinted() {
        // Death Star: Central Core (V): the Master List prints "Immue" (kept as published). Roche (V): "Force drain" as printed; the 7.2 sheet names Roche.
        assertEquals("Opponent's Force drain bonuses at systems (except your systems) are limited to 1. Immue to Expand The Empire.", library.getSwccgoCardBlueprint("1271_22").getLocationLightSideGameText());
        assertEquals("Opponent's Force drain bonuses at systems (except your systems) are limited to 1. Immune to Expand The Empire.", library.getSwccgoCardBlueprint("2271_22").getLocationLightSideGameText());
        assertEquals("Unless opponent controls this system, Commence Primary Ignition may not target Roche.", library.getSwccgoCardBlueprint("1272_28").getLocationLightSideGameText());
        assertEquals("Unless opponent controls this system, Commence Primary Ignition may not target it.", library.getSwccgoCardBlueprint("2272_28").getLocationLightSideGameText());
        for (String id : new String[]{"1272_28", "2272_28"})
            assertEquals(id, "Force drain -1 here. If you control, Asteroid Sanctuary at related sector is canceled.", library.getSwccgoCardBlueprint(id).getLocationDarkSideGameText());
    }
}
