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
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B15 (Virtual Blocks 4-5, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest text and each older version is its own 22xx card with its text, version label
 * and current printing. Images are pinned in cardImageUrlTests.js. 601_1 / 601_87 / 601_95 / 601_39 / 601_110 are the
 * current classes of 1240_31 / 1240_58 / 1240_59 / 1240_78 / 1240_86; 2250_2ai (Aurra Sing (V) AI original) is covered
 * by LegacyVersionsTests.
 */
public class LegacyBatchB15VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1240_31", "Deploy on table with up to 15 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn."),
            Map.entry("1240_37", "Unless your [Reflections III] Objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). Unless duel canceled or tied, for remainder of game any Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny."),
            Map.entry("1240_40", "Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Rogue Shadow may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle)."),
            Map.entry("1240_58", "Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy Your Spies Have Located A Jedi?.\nFor remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\nWhile this side up, may ▲ Rogue Shadow. Galen's immunity to attrition is +2 for each Jedi present.\nFlip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site."),
            Map.entry("1240_59", "May add 2 pilots and 2 passengers. If Juno Eclipse piloting, adds one battle destiny. If it just took off, may use its hyperspeed and land. Immune to attrition < 7 if Galen on table, even while landed."),
            Map.entry("1240_64", "Deploy on Galen. While Galen defending a battle, his and Juno Eclipse's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. Target hit, power -3, and may not be used to satisfy attrition if total destiny > defense value."),
            Map.entry("1240_78", "Deploy on table with up to 15 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Uncertain Is The Future until the end of your first turn."),
            Map.entry("1240_86", "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card) that loss is cumulatively -1 (to a minimum of 1). Non-[Virtual Block 4] Imperial Atrocity is canceled."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2240_31", new String[]{"1240_31", "", "Deploy on table with up to 12 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn."}),
            Map.entry("2240_37", new String[]{"1240_37", "", "Unless your [Reflections III] objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). After the duel (unless it was canceled or tied) Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny."}),
            Map.entry("2240_40", new String[]{"1240_40", "", "Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Galen's Fighter may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle)."}),
            Map.entry("2240_58", new String[]{"1240_58", "", "Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy If The Trace Was Correct.\nFor remainder of game, you may not deploy [Episode I] Dark Jedi. Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force.\nWhile this side up, may ▲ Galen's Fighter. Galen's immunity to attrition is +2 for each Jedi present.\nFlip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site."}),
            Map.entry("2240_1058", new String[]{"1240_58", "PC-E1", "Deploy Coruscant system, Imperial City, and a Sith's Plans. May also deploy If The Trace Was Correct.\nFor remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\nWhile this side up, may ▲ Galen's Fighter. Galen's immunity to attrition is +2 for each Jedi present.\nFlip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site."}),
            Map.entry("2240_59", new String[]{"1240_59", "", "May add 2 pilots and 2 passengers. If Black Leader piloting, adds one battle destiny. If it just took off, may use its hyperspeed and land. Immune to attrition < 7 if Galen on table, even while landed."}),
            Map.entry("2240_64", new String[]{"1240_64", "", "Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. Target hit, power -3, and may not be used to satisfy attrition if total destiny > defense value."}),
            Map.entry("2240_1064", new String[]{"1240_64", "PC-E1", "Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to atttition is +2. May target a character or creature for free. Draw two destiny. If total destiny > defense value, target hit, power -3, and may not be used to satisfy attrition."}),
            Map.entry("2240_78", new String[]{"1240_78", "", "Deploy on table with up to 12 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Uncertain Is The Future until the end of your first turn."}),
            Map.entry("2240_86", new String[]{"1240_86", "", "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card) that loss is cumulatively -1 (to a minimum of 1)."}),
            Map.entry("2240_101", new String[]{"1240_101", "", "Light: May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ a device (except a comlink) or Effect that deploys on a character here. Dark: Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape."}));

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
    public void OutpostCurrentTextIsTheErrataWording() {
        SwccgCardBlueprint card = library.getSwccgoCardBlueprint("1240_101");
        assertNull(card.getLegacyVersion());
        assertEquals("May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ an Effect that deploys on a character or a device (except a comlink) here.", card.getLocationLightSideGameText());
        assertEquals("1240_101", library.getSwccgoCardBlueprint("2240_101").getLegacyVersion().currentPrintingId());
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
    public void PrintedTextsKeptAsPublished() {
        assertTrue(library.getSwccgoCardBlueprint("2240_1064").getGameText().contains("immunity to atttition is +2"));
        assertTrue(library.getSwccgoCardBlueprint("2240_37").getGameText().startsWith("Unless your [Reflections III] objective on table"));
        assertTrue(library.getSwccgoCardBlueprint("2240_1058").getGameText().startsWith("Deploy Coruscant system, Imperial City, and a Sith's Plans."));
        assertEquals("\"A Jedi uses the Force for knowledge and defense, never for attack.\"", library.getSwccgoCardBlueprint("1240_31").getLore());
    }
}
