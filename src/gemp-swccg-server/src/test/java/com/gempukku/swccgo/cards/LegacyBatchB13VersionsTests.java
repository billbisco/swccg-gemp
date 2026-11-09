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
 * Legacy batch B13 (Virtual Blocks 1-2, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json,
 * record legacy-versions-v4.json): the current card carries the latest version's text and every older version is its own
 * 22xx card. Death Star Gunner (V) gains a middle version (PC-E1, the 2010-06-17 errata, 2210_1008); the current card is
 * the 2012 card-list text (PC-E2). Defensive Fire (V) (601_198) carries the card-list text ("non-Permanent weapon").
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB13VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1210_8", "While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to take a Superlaser into hand from Reserve Deck; reshuffle."),
            Map.entry("1210_31", "Deploy on table. During the power segment of a battle, may use X Force to reduce a starship's immunity to attrition by X for remainder of turn. Once per turn, may use 2 Force to cancel the game text of a [Maintenance] character or a non-[Theed Palace] senator for remainder of turn."),
            Map.entry("1210_55", "If you just initiated battle where you have at least two characters (one a trooper and the other a leader), add two battle destiny. Opponent takes no battle damagethis battle. OR Deploy one Dewback from Reserve Deck; reshuffle."),
            Map.entry("1210_80", "Deploys only on Tatooine. Ability = ¼. May add 1 'rider' (passenger). May be targeted by Trample instead of a piloted AT-AT. During battle, may 'roar' to make a character here defense value -1."),
            Map.entry("1210_110", "While on Yavin 4: cancels Force drain bonuses from Yavin sites' game text, your Attack Run total is cumulatively +1, and during your deploy phase, may place this character in Used Pile to take one Advance Preparation or Death Star: Trench into hand from Reserve Deck; reshuffle."),
            Map.entry("1210_159", "USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, may place target (and all cards on it) in owner's Used Pile. LOST: Target a Heroic character or starship. It loses Heroic and/or immunity to attrition, if any, this turn."),
            Map.entry("1220_50", "Deploy on table. Slave I is destiny +2 when drawn for weapon or battle destiny. During battle: while non-[Reflections 3] Boba Fett with Vader, neither may be excluded or lost until the damage segment, and may place this Effect in Lost Pile to make Boba Fett's total weapon destiny +3."),
            Map.entry("601_198", "If opponent just initiated battle, you may immediately fire one non-lightsaber, non-Permanent weapon present in that battle once (for free), adding 2 to each of its destiny draws. That weapon may fire again this battle."),
            Map.entry("1220_67", "Dark: May deploy Biker Scout Gear here from Reserve Deck; reshuffle, and while here, once per turn, may deploy one card (without ability) in its game text from Reserve Deck; reshuffle. Light: Your characters may enter/exit here for 2 Force each. Unless you occupy, AT-AT's immunity to attrition is +1 (or immune to attrition if non-unique)."),
            Map.entry("1220_72", "Deploy Hoth system, [Virtual Block 6] Ice Plains, and [Virtual Block 8] Main Power Generators. May deploy Imperial Decree. For remainder of game, AT-ATs deploy -1 to marker sites. Walker Garrison may only be played once per game and is [Immune to Sense]. Non-[Virtual Block 8] You May Start Your Landing is canceled. While this side up, Revolution is canceled. Once during your turn, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2210_8", new String[]{"1210_8", "", "While on Death Star: cancels opponent's game text on your non-docking bay Death Star sites, your [A New Hope] Epic Event total is cumulatively +2, and, during your deploy phase, may place this character in Used Pile to ▲ Superlaser."}),
            Map.entry("2210_1008", new String[]{"1210_8", "PC-E1", "While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to ▲ Superlaser."}),
            Map.entry("2210_31", new String[]{"1210_31", "", "Deploy on table. During the power segment of a battle, may use X Force to reduce a starship's immunity to attrition by X for remainder of turn. Once per turn, may use 2 Force to cancel the game text of a [Maintenance] character or a senator for remainder of turn."}),
            Map.entry("2210_55", new String[]{"1210_55", "", "If you just initiated battle where you have at least two characters (one a trooper and the other a leader), add two battle destiny. Opponent takes no battle damage this battle. OR ▼ one Dewback"}),
            Map.entry("2210_80", new String[]{"1210_80", "", "Deploys only on Tatooine. Ability = ¼. May add 1 'rider' (passenger). May be targeted by Trample instead of a piloted AT-AT. During battle, may 'roar' to make a character here defense value -1."}),
            Map.entry("2210_110", new String[]{"1210_110", "", "Dark Side game text on your non-docking bay Yavin 4 sites is canceled. Your Attack Run total is cumulatively +1. During your deploy phase, may place this character in Used Pile to ▲ one Advance Preparation or Death Star: Trench."}),
            Map.entry("2210_159", new String[]{"1210_159", "", "USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn."}),
            Map.entry("2220_50", new String[]{"1220_50", "", "Deploy on table. During battle, while non-[Reflections 3] Boba Fett with Vader, neither may be excluded or lost until the damage segment. Slave I is destiny +2 when drawn for weapon or battle destiny. May place this Effect in Lost Pile to make Boba Fett's total weapon destiny +3."}),
            Map.entry("2220_58", new String[]{"1220_58", "", "If opponent just initiated battle, immediately fire a non-lightsaber, non-[Permanent Weapon] weapon present (for free and each weapon destiny is +2). That weapon may fire again this battle."}),
            Map.entry("2220_67", new String[]{"1220_67", "", "Dark: May deploy Biker Scout Gear here from Reserve Deck; reshuffle, and while here, may deploy one card without ability in its game text from Reserve Deck; reshuffle. Light: Your characters may enter/exit here for 2 Force each. Unless you occupy, AT-AT's immunity to attrition is +1 (or immune to attrition if non-unique)."}),
            Map.entry("2220_72", new String[]{"1220_72", "", "Deploy Hoth system, [Virtual Block 6] Ice Plains, and Main Power Generators (Light Side). May deploy Imperial Decree. For remainder of game, Walker Garrison is an Out Of Play Interrupt and [Immune to Sense]. AT-ATs are deploy -1 to marker sites. While this side up, Revolution is canceled. Opponent loses no more than 1 Force to your [Special Edition] Effects. During your control phase, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth."}));

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
    public void DeathStarGunnerHasThreeVersions() {
        assertEquals("2210_1008", LegacyVersion.versionIdOf("1210_8", 1));
        assertEquals("1210_8", LegacyVersion.currentPrintingIdOf("2210_1008"));
        LegacyVersion original = library.getSwccgoCardBlueprint("2210_8").getLegacyVersion();
        LegacyVersion pcE1 = library.getSwccgoCardBlueprint("2210_1008").getLegacyVersion();
        assertEquals(LocalDate.of(2010, 6, 17), original.supersededOn());
        assertEquals(LocalDate.of(2010, 6, 17), pcE1.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), pcE1.supersededOn());
    }

    @Test
    public void ErrataDatesFollowTheRecord() {
        assertEquals(LocalDate.of(2010, 6, 17), library.getSwccgoCardBlueprint("2210_110").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2210_159").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2220_50").getLegacyVersion().supersededOn());
    }

    @Test
    public void LoreAsPrinted() {
        // Rebel Tech (V) prints Decipher's "joing"; Astromech Shortage (V) prints "astromechs" twice.
        for (String id : new String[]{"1210_110", "2210_110"})
            assertEquals(id, "Del Goren, typical expert in communications and scanner jamming, served aboard the Rebel privateer corvette Spiral before joing the Alliance High Command.", library.getSwccgoCardBlueprint(id).getLore());
        for (String id : new String[]{"1210_31", "2210_31"})
            assertEquals(id, "Imperial pilots often target astromechs aboard Rebel starfighters in an attempt to prevent hyper-escapes. Scarcity of undamaged astromechs can delay starfighter deployment.", library.getSwccgoCardBlueprint(id).getLore());
    }
}
