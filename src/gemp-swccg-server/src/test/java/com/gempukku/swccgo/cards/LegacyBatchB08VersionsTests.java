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
 * Legacy batch B08 (Virtual Block 5, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * label and current printing. 1250_45 and 1250_80 are played by 601_163 and 601_175.
 */
public class LegacyBatchB08VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1250_2", "Once during opponent's activate phase, may relocate Aurra to a site where opponent has a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4."),
            Map.entry("1250_14", "Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your aliens (except Mara), senators, Maul, and [Independent Starship] starships are lost. (Immune to Alter.)"),
            Map.entry("1250_22", "Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses any immunity to attrition. If this Effect just lost, relocate it to Sidious."),
            Map.entry("1250_33", "At each system a player controls with a capital starship, their non-[Presence] characters and vehicles deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains)."),
            Map.entry("1250_34", "Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if you just Force drained with a Rebel, may retrieve 1 Force."),
            Map.entry("1250_35", "Adds 2 to power of anything he pilots. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is maneuver +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi)."),
            Map.entry("1250_44", "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. May not be Disarmed. Immune to You Are Beaten and attrition."),
            Map.entry("601_163", "Mace's game text may not be canceled (except at Senate) and he may fire one weapon twice per battle. Boba Fett may not add battle destiny draws here. Immune to attrition."),
            Map.entry("1250_65", "Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if on Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny."),
            Map.entry("601_175", "May add one astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Permanent pilot provides ability of 4. Immune to attrition < 3."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2250_2", new String[]{"1250_2", "", "Once during opponent's activate phase, may relocate Aurra to same site as a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4."}),
            Map.entry("2250_14", new String[]{"1250_14", "", "Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your Maul, aliens (except Mara), [Independent Starship] starships, and senators are lost. (Immune to Alter.)"}),
            Map.entry("2250_22", new String[]{"1250_22", "", "Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious."}),
            Map.entry("2250_1022", new String[]{"1250_22", "PC-E1", "Deploy on Sidious. If at Senate, at start of your turn may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious."}),
            Map.entry("2250_33", new String[]{"1250_33", "PC-E2", "At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains)."}),
            Map.entry("2250_34", new String[]{"1250_34", "", "Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if opponent just lost Force to a Force drain by a Rebel, may retrieve 1 Force."}),
            Map.entry("2250_35", new String[]{"1250_35", "", "[Pilot] 2. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is manuever +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi)."}),
            Map.entry("2250_44", new String[]{"1250_44", "", "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may ▲ an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. Immune to Disarmed, You Are Beaten, and attrition."}),
            Map.entry("2250_1044", new String[]{"1250_44", "PC-E2", "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. Immune to You Are Beaten and attrition. May not be Disarmed."}),
            Map.entry("2250_45", new String[]{"1250_45", "", "Mace's game text may not be canceled (except at Senate). During battle, Mace may fire one weapon twice. Boba Fett may not add battle destiny draws here. Immune to attrition."}),
            Map.entry("2250_65", new String[]{"1250_65", "", "Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny."}),
            Map.entry("2250_80", new String[]{"1250_80", "", "Permanent pilot provides ability of 4. May add 1 astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Immune to attrition < 3."}));

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
    public void WithThunderousApplauseAndMasterQuiGonHaveTwoOlderVersions() {
        assertEquals("2250_1022", LegacyVersion.versionIdOf("1250_22", 1));
        assertEquals("2250_1044", LegacyVersion.versionIdOf("1250_44", 1));
        assertEquals("1250_22", LegacyVersion.currentPrintingIdOf("2250_1022"));
        assertEquals("1250_44", LegacyVersion.currentPrintingIdOf("2250_1044"));
    }
}
