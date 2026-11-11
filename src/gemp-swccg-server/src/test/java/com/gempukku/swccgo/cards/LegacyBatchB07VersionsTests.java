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
 * Legacy batch B07 (Virtual Block 3-4, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest (PC-E1) text and the original is its own 22xx card with its text, version label
 * and current printing. Images (CardMaker recreations of each PC-E1 text; originals keep their slips) are pinned in
 * cardImageUrlTests.js. 601_211 is the current class of 1240_63 Stinger (V).
 */
public class LegacyBatchB07VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1230_114", "May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you here."),
            Map.entry("1230_116", "Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2."),
            Map.entry("1230_118", "Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (If on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value."),
            Map.entry("1240_20", "Deploy on opponent's location. Wherever you have two Mistryl, your Force drains are +1. 'Stolen' on Swilla Corey is treated as 'placed in owner's Used Pile' and it may target a weapon instead of a device. Your Force drains here cannot be modified or canceled by opponent."),
            Map.entry("1240_21", "Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or admiral here, may subtract X from a starship's forfeit and immunity to attrition for remainder of turn, where X = total influence at Conference Room."),
            Map.entry("1240_22", "Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or general here, may subtract X from forfeit and immunity to attrition of a character at a site for remainder of turn, where X = total influence at Conference Room."),
            Map.entry("1240_36", "Deploy on Conference Room. Revolution is canceled. You may not deploy aliens (except bounty hunters and Mara Jade) or [Episode I] cards. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present here, each Imperial Council Member adds one [Dark Side Force]. Once per turn, may use 1 Force to deploy one Laser Gate or a Political Effect from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)"),
            Map.entry("1240_47", "If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. If destiny +2 > target's ability, target excluded from battle and adds her power to Xizor's for remainder of turn."),
            Map.entry("601_211", "May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Chewie or Leia on table, Force Pile instead) and immune to attrition < 5."),
            Map.entry("1240_92", "Deploy on an exterior Tatooine battleground site. X = the face up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, may add X to your ability during battle at a site. This Effect canceled if opponent controls this site."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2230_114", new String[]{"1230_114", "", "May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you here."}),
            Map.entry("2230_116", new String[]{"1230_116", "", "Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2."}),
            Map.entry("2230_118", new String[]{"1230_118", "", "Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (If on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value."}),
            Map.entry("2240_20", new String[]{"1240_20", "", "Deploy on opponent's location. Wherever you have two Mistryl, your Force drains are +1. 'Stolen' on Swilla Corey is treated as 'placed in owner's Used Pile' and it may target a weapon instead of a device. Your Force drains here cannot be modified or canceled by opponent."}),
            Map.entry("2240_21", new String[]{"1240_21", "", "Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or admiral here, may subtract X from a starship's forfeit and immunity to attrition for remainder of turn, where X = total influence at Conference Room."}),
            Map.entry("2240_22", new String[]{"1240_22", "", "Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or general here, may subtract X from forfeit and immunity to attrition of a character at a site for remainder of turn, where X = total influence at Conference Room."}),
            Map.entry("2240_36", new String[]{"1240_36", "", "Deploy on Conference Room. Revolution is canceled. You may not deploy aliens (except bounty hunters and Mara Jade) or [Episode I] cards. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present, each Imperial Council Member adds one [Dark Side Force]. Once per turn, may use 1 Force to deploy one Laser Gate or a Political Effect from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)"}),
            Map.entry("2240_47", new String[]{"1240_47", "", "If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. If destiny +2 > target's ability, target excluded from battle and adds her power to Xizor's for remainder of turn."}),
            Map.entry("2240_63", new String[]{"1240_63", "", "May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Chewie or Leia on table, Force Pile instead) and immune to attrition < 5."}),
            Map.entry("2240_92", new String[]{"1240_92", "", "Deploy on an exterior Tatooine battleground site. X = the face up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, may add X to your ability during battle at a site. This Effect canceled if opponent controls this site."}));

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
        assertEquals(10, VERSIONS.size());
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
    public void ReleaseSheetSourcesNameTheSlipNumber() {
        assertEquals("Virtual Block 3 release sheet (2009-12-07), card #117", library.getSwccgoCardBlueprint("2230_114").getLegacyVersion().source());
        assertEquals("Virtual Block 3 release sheet (2009-12-07), card #115", library.getSwccgoCardBlueprint("2230_116").getLegacyVersion().source());
        assertEquals("Virtual Block 3 release sheet (2009-12-07), card #120", library.getSwccgoCardBlueprint("2230_118").getLegacyVersion().source());
        assertEquals("Virtual Block 4 release sheet (2009-12-07), card #62", library.getSwccgoCardBlueprint("2240_63").getLegacyVersion().source());
    }
}
