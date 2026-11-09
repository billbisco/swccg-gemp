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
 * Legacy batch B10 (Virtual Block 6, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest text and each older version is its own 22xx card with its text, version label
 * and current printing. Images (CardMaker recreations of each latest text; 2261_87 the first-copy release-sheet slip of
 * the original Strong Is Vader) are pinned in cardImageUrlTests.js. 601_258 / 601_169 / 601_250 / 601_253 are the current
 * classes of 1261_94 / 1261_96 / 1261_106 / 1261_107.
 */
public class LegacyBatchB10VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1261_82", "Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to take its matching pilot character (or vice versa) from Reserve Deck and deploy both simultaneously; reshuffle. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. (Immune to Alter.)"),
            Map.entry("1261_87", "If [Virtual] Mind What You have Learned on table, deploy on table with up to 6 Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and chose one. You may retrieve 3 Force whenever you complete a Jedi Test. Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table."),
            Map.entry("601_258", "USED: Initiate a battle for free. STARTING: If Dantooine Operations on table, deploy from Reserve Deck Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile."),
            Map.entry("601_169", "Use 1 Force to deploy (or retrieve) non-[Virtual] Amidala, Panaka, or Ric Olie from Reserve Deck; reshuffle.  OR  If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power and may draw one battle destiny if not able to otherwise. (Immune to Sense.)"),
            Map.entry("1261_101", "Use 1 Force to deploy (or retrieve) non-[Permanent Weapon] Han (except Falcon) from Reserve Deck; reshuffle. OR If battle just initiated, and Han defending alone at a site, he is power +1 for each character and vehicle present, his forfeit cannot be reduced, he adds one destiny to power, and he draws one battle destiny if not able to otherwise. (Immune to Sense.)"),
            Map.entry("1261_105", "If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Cancel an attempt to 'operate' on a character; character is protected from any further attempts until start of opponent's next turn. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile."),
            Map.entry("601_250", "May add Harc as a pilot. During battle, adds one destiny to total power. If opponent just deployed a Dark Jedi or Sith to a related site, may deploy a related location from Reserve Deck; reshuffle. Immune to attrition < 4 (< 6 while at Bespin)."),
            Map.entry("601_253", "Permanent pilot is •Wedge, who provides ability of 3. During a battle with opponent's [Independent] starship, may add one destiny to total power or attrition. Immune to Come With Me, Tallon Roll, and attrition < 4."),
            Map.entry("1262_11", "Deploy on table. When Luke dpeloys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may take Rise, My Friend into hand from Reserve Deck; reshuffle. If this Effect canceled by opponent, stack it on Insignificant Rebellion."),
            Map.entry("1263_6", "Adds 2 to power of anything she pilots. Opponent may not add destiny draws to power or attrition here. Sandwhirl and Ice Storm are canceled at same and related locations."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2261_82", new String[]{"1261_82", "", "Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to ▲ its matching pilot character (or vice versa) and deploy both simultaneously. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. May not be suspended. (Immune to Alter.)"}),
            Map.entry("2261_87", new String[]{"1261_87", "", "If [Virtual] Mind What You Have Learned on table, deploy on table with up to 6 different Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test (cannot be canceled while you occupy a battleground). Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table."}),
            Map.entry("2261_1087", new String[]{"1261_87", "PC-E1", "If [Virtual] Mind What You Have Learned on table, deploy on table with up to 6 Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test. Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table."}),
            Map.entry("2261_94", new String[]{"1261_94", "", "USED: Initiate a battle for free. STARTING: If Dantooine Base Operations on table, ▼ Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile."}),
            Map.entry("2261_96", new String[]{"1261_96", "", "Use 1 Force to ▼ (or retrieve) Panaka, Ric Olie, or non-[Virtual] Amidala. OR If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power, and may draw one battle destiny if unable to otherwise. (Immune to Sense.)"}),
            Map.entry("2261_101", new String[]{"1261_101", "", "Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"}),
            Map.entry("2261_105", new String[]{"1261_105", "", "If your character's forfeit was just reduced to 0, use 1 Force (free if reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Cancel an attempt to 'operate' on a character (who is also protected from any further attempts until start of opponent's next turn). OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place this Interrupt in Lost Pile."}),
            Map.entry("2261_106", new String[]{"1261_106", "", "May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may ▼ a related location. Immune to attrition < 4 (< 6 at Bespin)."}),
            Map.entry("2261_107", new String[]{"1261_107", "", "Permanent pilot is •Wedge, who provides ability of 3. During a battle with opponent's [Independent] starship, may add one destiny to attrition or total power. Immune to Come With Me, Tallon Roll, and attrition < 4."}),
            Map.entry("2262_11", new String[]{"1262_11", "", "Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may ▲ Rise, My Friend. If this Effect canceled by opponent, stack it on Insignificant Rebellion."}),
            Map.entry("2263_6", new String[]{"1263_6", "", "[Pilot] 2. Opponent may not add destiny draws to power or attrition here. Ice Storm and Sandwhirl are canceled at same and related locations."}));

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
    public void ReleaseSheetSourcesNameTheSlipNumber() {
        assertEquals("Virtual Block 6.1 release sheet (2009-09-04), card #89", library.getSwccgoCardBlueprint("2261_87").getLegacyVersion().source());
        assertEquals("PC news \"Strong Is Vader / Rebel Fleet changes\" (2009-09-21: errata, text not quoted; most likely this one); Virtual Block 6.1 release sheet, revised (2010-04-20), card #89", library.getSwccgoCardBlueprint("2261_1087").getLegacyVersion().source());
        assertEquals("Virtual Block 6.1 release sheet (2009-09-04), card #97", library.getSwccgoCardBlueprint("2261_96").getLegacyVersion().source());
        assertEquals("Virtual Block 6.3 release sheet (released 2010-03-12), card #3-06", library.getSwccgoCardBlueprint("2263_6").getLegacyVersion().source());
    }
}
