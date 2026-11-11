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
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B20 (Virtual Block 8, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text (the 2012 PC card list; Contract Killers: the Virtual Cards
 * Master List of 27 April 2013) and every original is its own 2281 card with the release-sheet slip's text (▲ / ▼ and
 * the [Virtual Block 4] / [Virtual Block 8] icons as printed). Images (each version's own text) are pinned in
 * cardImageUrlTests.js.
 */
public class LegacyBatchB20VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1281_6", "Once per game, may take one Jawa Blaster or a card with 'sandcrawler' in title or gametext into hand from Reserve Deck; reshuffle. If present at a Tatooine battleground, once per turn, if there is more than 1 card in opponent's Force Pile, may use 1 Force in opponent's Force Pile."),
            Map.entry("1281_8", "Use 2 Force to deploy on your alien warrior (free Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force."),
            Map.entry("1281_11", "Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may deploy a docking bay or So Be It, Jedi from Reserve Deck; reshuffle. 'Battle' on Insignificant Rebellion is treated as 'battle or duel. (Immune to Alter)."),
            Map.entry("1281_13", "Deploy on Conference Room (only at start of game); deploy Sim Aloo here and [Virtual Block 4] There is No Try from Reserve Deck; reshuffle. Players may initiate battle here for free. Once per game may deploy Ord Mantell from Reserve Deck; reshuffle. May not be canceled (except by opponent) or placed in Used Pile. Immune to Ineffective Manuever."),
            Map.entry("1281_15", "Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)"),
            Map.entry("1281_16", "USED: Cancel an attempt to target your non-droid assassin (or a starship piloted by one) with a weapon. LOST: Use 1 Force to take a just lost non-droid assassin into hand."),
            Map.entry("601_27", "Once per game, ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien."),
            Map.entry("601_24", "May reveal from hand to take a [Virtual Block 8] Fett from Reserve Deck; reshuffle; and deploy both simultaneously. May add 2 pilots and 2 passengers. Your [Virtual Block 8] Fetts deploy -2 aboard. Immune to attrition < 5."),
            Map.entry("1281_24", "Contract Killers: Deploy Coruscant system, Sub City Lair and On The Hunt. For remainder of game, Aurra, Galen, Mara, and your aliens with 'plotting to kill Jabba' in lore are assassins. Your cards with ability are deploy +3 except assassins and [Independent] starships. Characters add 1 to their Death Mark total for each weapon or device they carry. Once per turn, may ▼ a Coruscant battleground. Flip this card if assassins control two battlegrounds (unless opponent's non-droid character at a battleground site) or a [Virtual Block 8] Death Mark attempt is made. Feared Throughout The Galaxy: While this side up, during your control phase, opponent loses 1 Force. If two assassins (except Galen) are in battle, add one battle destiny. Once per turn, if opponent just lost a character, an assassin there may gain 'reputation;' retrieve any one card (may only be canceled by Aim High) and stack it face down on that assassin. Assassins with 'reputation' may add 1 to Force drains where present. Flip this card at the end of any turn if opponent's non-droid character at a battleground site (unless [Virtual Block 8] Death Mark is on your side of the table)."),
            Map.entry("1281_25", "Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and forfeit = 0, if total destiny > defense value."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2281_6", new String[]{"1281_6", "", "Once per game, may ▲ Jawa Blaster or a card with 'sandcrawler' in title or gametext. Once per turn, if present at a Tatooine battleground and there is more than 1 Force in opponent's Force Pile, you may use 1 Force in opponent's Force Pile."}),
            Map.entry("2281_8", new String[]{"1281_8", "", "Use 2 Force to deploy on your alien warrior (free on Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force."}),
            Map.entry("2281_11", new String[]{"1281_11", "", "Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may ▼ So Be It, Jedi or a docking bay. 'Battle' on Insignificant Rebellion is treated as 'battle or duel.' (Immune to Alter.)"}),
            Map.entry("2281_13", new String[]{"1281_13", "", "Deploy on Conference Room (only at start of game); ▼ Sim Aloo here and [Virtual Block 4] There is No Try. Players initiate battles here for free. Once per game may ▼ Ord Mantell. May not be canceled (except by opponent) or placed in Used Pile. Immune to Ineffective Maneuver."}),
            Map.entry("2281_15", new String[]{"1281_15", "", "Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)"}),
            Map.entry("2281_16", new String[]{"1281_16", "", "USED: Cancel an attempt to target your non-droid assassin (or a starship piloted by a non-droid assassin) with a weapon. LOST: Use 1 Force to take a just-lost non-droid assassin into hand."}),
            Map.entry("2281_17", new String[]{"1281_17", "", "Once per game ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien."}),
            Map.entry("2281_23", new String[]{"1281_23", "", "May reveal from hand to ▲ a [Virtual Block 8] Fett and deploy both simultaneously. May add 2 pilots and 2 passengers. Any [Virtual Block 8] Fett deploys -2 aboard. Immune to attrition < 5."}),
            Map.entry("2281_24", new String[]{"1281_24", "", "Contract Killers: Deploy Coruscant system, Sub City Lair and On The Hunt. For remainder of game, Aurra, Galen, Mara, and your aliens with 'plotting to kill Jabba' in lore are assassins. Your cards with ability are deploy +3 except assassins and [Independent] starships. Characters add 1 to their Death Mark total for each weapon or device they carry. Once per turn, may ▼ a Coruscant battleground. Flip this card if assassins control two battlegrounds (unless opponent's non-droid character at a battleground site) or you just drew destiny for [Virtual Block 8] Death Mark. Feared Throughout The Galaxy: While this side up, during your control phase, opponent loses 1 Force. If two assassins (except Galen) are in battle, add one battle destiny. Once per turn, if opponent just lost a character, an assassin there may gain 'reputation;' retrieve any one card (may only be canceled by Aim High) and stack it face down on that assassin. Assassins with 'reputation' may add 1 to Force drains where present. Flip this card at the end of any turn if opponent's non-droid character at a battleground site (unless [Virtual Block 8] Death Mark is on your side of the table)."}),
            Map.entry("2281_25", new String[]{"1281_25", "", "Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value."}));

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
    public void OriginalsAreTheirOwnCards() {
        assertEquals(10, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertEquals(id, id, library.getBaseBlueprintId(id));
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.of(2012, 5, 11), version.printedOn());
        });
    }

    @Test
    public void SupersededOnTheDateOfTheNextText() {
        VERSIONS.keySet().forEach(id -> assertEquals(id, "2281_24".equals(id) ? LocalDate.of(2013, 4, 27) : LocalDate.of(2012, 7, 21),
                library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn()));
    }

    @Test
    public void SonicBombardmentLoreAsPrinted() {
        String lore = library.getSwccgoCardBlueprint("601_27").getLore();
        assertEquals(26, lore.toLowerCase().split("ooui", -1).length - 1);
        assertTrue(lore.endsWith("oo-"));
    }
}
