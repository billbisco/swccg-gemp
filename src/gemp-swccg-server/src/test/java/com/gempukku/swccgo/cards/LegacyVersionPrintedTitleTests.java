package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgGameParticipant;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pre-errata Legacy versions printed under an older name (docs/legacy-versions.md, "Printed title"): the printed title is
 * shown everywhere a card name is displayed, while every rule (uniqueness, persona, title matching, deck counts) keeps
 * treating the version as its current card.
 */
public class LegacyVersionPrintedTitleTests {
    /** Version id -> printed title (release sheet slip), per legacy-versions-v3.json printed_title. */
    private static final Map<String, String> PRINTED_TITLES = new LinkedHashMap<>() {{
        put("2240_2", "Black Leader (V)");
        put("2240_8", "Galen, Secret Apprentice (V)");
        put("2240_59", "Galen's Fighter");
        put("2240_17", "Thrawn's Pet");
        put("2240_66", "Fallen Jedi");
        put("2240_112", "BoShek's Modified Light Freighter (V)");
        put("2250_8", "The Mandalorian");
        put("2263_1", "Bane, The Bounty Hunter");
        put("2263_7", "The Emperor's Reach");
        put("2272_15", "Blind Jedi");
        put("2272_18", "Kashyyyk Insurgent Leader");
        put("2261_93", "Krayt Dragon Howl & Armed And Dangerous");
        put("2271_1", "Cyborg Commander, Hunter Of Jedi");
        put("2271_5", "Ice-Heart");
        put("2273_6", "IG Bodyguard Droid");
        put("2274_4", "Floating Refinery (V)");
        put("2274_11", "Cyborg Commander's Lightsabers");
        put("2281_47", "Rebel Agent's Blaster Rifle");
        put("2273_9", "Spice Mine Administrator");
        put("2281_9", "The Mandalorian, Father Of Fett");
        put("2281_28", "Rebel Agent");
        put("2281_45", "Booster's Star Destroyer (V)");
        put("2240_26", "He Is Not Ready & Imperial Propaganda");
        put("2240_81", "Eject! Eject! & Imperial Atrocity");
        put("2282_13", "Devaronian Uncle");
        put("2282_27", "Lando's Luxury Yacht");
    }};

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void VersionsPrintedUnderAnOlderNameShowThatName() {
        for (var entry : PRINTED_TITLES.entrySet()) {
            String id = entry.getKey();
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(id);
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(version.getLegacyVersion().currentPrintingId());
            assertEquals(id, entry.getValue(), version.getPrintedTitle());
            assertEquals(id, entry.getValue(), GameUtils.getDisplayTitle(version));
            assertEquals(id, entry.getValue(), GameUtils.getFullName(version));
            // Rules title is the current card's
            assertEquals(id, current.getTitle(), version.getTitle());
            assertEquals(id, current.getTitles(), version.getTitles());
            assertEquals(id, GameUtils.getFullName(current), GameUtils.getRulesFullName(version));
        }
        // A card that is not a version has no printed title
        assertEquals(null, library.getSwccgoCardBlueprint("1271_1").getPrintedTitle());
        assertEquals("Grievous, Hunter Of Jedi", GameUtils.getFullName(library.getSwccgoCardBlueprint("1271_1")));
    }

    @Test
    public void EveryPrintedTitleIsOnAVersionAndDiffersFromTheRulesTitle() {
        int withPrintedTitle = 0;
        for (String id : LegacyCardPool.implementedVersionIds(library)) {
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(id);
            if (version.getPrintedTitle() == null)
                continue;
            withPrintedTitle++;
            assertNotNull(id, version.getLegacyVersion());
            assertNotEquals(id, version.getTitle(), version.getPrintedTitle());
            assertFalse(id + ": printed title without uniqueness dots", version.getPrintedTitle().contains("•"));
        }
        assertTrue(String.valueOf(withPrintedTitle), withPrintedTitle >= PRINTED_TITLES.size());
    }

    private static VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("obi", "1_21");
        }}, new HashMap<>() {{
            put("cyborg", "2271_1");
            put("current", "1271_1");
            put("general", "203_27");
            put("trace", "1240_40");
            put("sabers", "1274_11");
            put("fighter", "2240_59");
            put("rogueShadow", "601_95");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void InGameTheCardShowsItsPrintedTitleAndCardInfoNamesTheCurrentCard() throws Exception {
        var scn = scenario();
        var cyborg = scn.GetDSCard("cyborg");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), cyborg);
        assertEquals("Grievous, Hunter Of Jedi", cyborg.getTitle());
        assertEquals("Cyborg Commander, Hunter Of Jedi", GameUtils.getFullName(cyborg));
        assertTrue(GameUtils.getCardLink(cyborg), GameUtils.getCardLink(cyborg).contains(">\u2022Cyborg Commander, Hunter Of Jedi</div>"));

        SwccgGameMediator mediator = mediatorFor(scn.game());
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(cyborg.getOwner());
        String info = mediator.produceCardInfo(player, cyborg.getCardId());
        assertTrue(info, info.contains("Printed as Cyborg Commander, Hunter Of Jedi; the same card as Grievous, Hunter Of Jedi for every rule"));
        String currentInfo = mediator.produceCardInfo(player, scn.GetDSCard("current").getCardId());
        assertFalse(currentInfo, currentInfo.contains("Printed as"));
    }

    @Test
    public void TitleAndPersonaRulesStillSeeTheCurrentCard() {
        var scn = scenario();
        var cyborg = scn.GetDSCard("cyborg");
        var fighter = scn.GetDSCard("fighter");
        scn.StartGame();
        assertTrue(Filters.title("Grievous, Hunter Of Jedi").accepts(scn.game(), cyborg));
        assertFalse(Filters.title("Cyborg Commander, Hunter Of Jedi").accepts(scn.game(), cyborg));
        assertTrue(Filters.Grievous.accepts(scn.game(), cyborg));
        assertTrue(Filters.titleContains("Grievous").accepts(scn.game(), cyborg));
        assertFalse(Filters.titleContains("Cyborg Commander").accepts(scn.game(), cyborg));
        assertTrue(Filters.title(Title.Rogue_Shadow).accepts(scn.game(), fighter));
        assertTrue(Filters.sameTitle(scn.GetDSCard("rogueShadow")).accepts(scn.game(), fighter));
    }

    @Test
    public void PrintedTitleVersionIsTheSameUniqueCardAndPersonaAsTheCurrentCard() {
        // Unique: the current Grievous, Hunter Of Jedi on table blocks the version (and the other way round);
        // persona: General Grievous on table blocks the version too.
        for (String onTableName : List.of("current", "general")) {
            var scn = scenario();
            var onTable = scn.GetDSCard(onTableName);
            var cyborg = scn.GetDSCard("cyborg");
            scn.StartGame();
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
            scn.MoveCardsToDSHand(cyborg);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(15);
            assertFalse(onTableName, scn.DSDeployAvailable(cyborg));
        }
        var scn = scenario();
        var cyborg = scn.GetDSCard("cyborg");
        var current = scn.GetDSCard("current");
        var general = scn.GetDSCard("general");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), cyborg);
        scn.MoveCardsToDSHand(current, general);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(15);
        assertFalse(scn.DSDeployAvailable(current));
        assertFalse(scn.DSDeployAvailable(general));
    }

    @Test
    public void CardsThatNameTheCurrentCardFindTheVersion() {
        // If The Trace Was Correct (V): "Rogue Shadow ... is immune to attrition < 4" applies to the version printed as
        // Galen's Fighter; Grievous' Lightsabers "Deploy on Grievous" may deploy on the version printed as Cyborg Commander.
        var scn = scenario();
        var trace = scn.GetDSCard("trace");
        var fighter = scn.GetDSCard("fighter");
        var cyborg = scn.GetDSCard("cyborg");
        var sabers = scn.GetDSCard("sabers");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), fighter, cyborg);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), fighter), scn.epsilon);
        scn.MoveCardsToDSSideOfTable(trace);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), fighter), scn.epsilon);

        scn.MoveCardsToDSHand(sabers);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        assertTrue(scn.DSDeployAvailable(sabers));
        scn.DSDeployCard(sabers);
        if (scn.DSHasCardChoiceAvailable(cyborg))
            scn.DSChooseCard(cyborg);
        scn.PassAllResponses();
        assertEquals(cyborg, sabers.getAttachedTo());
    }

    @Test
    public void DeckBuilderNameSearchFindsAVersionByEitherName() {
        var formats = new SwccgoFormatLibrary(library);
        var collections = new CollectionsManager(null, null, null, library);
        var all = collections.getDefaultCollection(false).getAll().values();
        var byPrinted = new SortAndFilterCards().process("name:Cyborg name:Hunter", all, library, formats, Collections.emptyMap());
        var byCurrent = new SortAndFilterCards().process("name:Grievous, name:Hunter", all, library, formats, Collections.emptyMap());
        assertTrue(byPrinted.stream().anyMatch(item -> item.getBlueprintId().equals("2271_1")));
        assertFalse(byPrinted.stream().anyMatch(item -> item.getBlueprintId().equals("1271_1")));
        assertTrue(byCurrent.stream().anyMatch(item -> item.getBlueprintId().equals("2271_1")));
        assertTrue(byCurrent.stream().anyMatch(item -> item.getBlueprintId().equals("1271_1")));
    }

    private static SwccgGameMediator mediatorFor(SwccgGame game) throws Exception {
        SwccgGameMediator mediator = new SwccgGameMediator("1", mock(SwccgFormat.class), null,
                new SwccgGameParticipant[]{new SwccgGameParticipant(game.getLightPlayer(), new SwccgDeck("light")),
                        new SwccgGameParticipant(game.getDarkPlayer(), new SwccgDeck("dark"))},
                mock(SwccgCardBlueprintLibrary.class), 3600, true, false, false, false, 60, false, false);
        Field gameField = SwccgGameMediator.class.getDeclaredField("_swccgoGame");
        gameField.setAccessible(true);
        gameField.set(mediator, game);
        return mediator;
    }
}
