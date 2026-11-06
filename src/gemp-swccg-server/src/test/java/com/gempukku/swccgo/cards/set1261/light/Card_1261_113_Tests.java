package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_113_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wookiee", "7_50"); put("wookiee2", "7_50"); put("wookiee3", "7_50"); put("wookiee4", "7_50");
            put("luke", "1_19"); put("trooper", "1_28"); put("speeder", "3_69"); put("ship", "12_91");
            put("sacred", "1116_16"); put("haven", "1116_17");
            put("kashyyyk", "2_64"); put("falcon", "1_143"); put("safeSite", "1_136");
        }}, new HashMap<>() {{
            put("skyhook", "601_14"); put("vader", "1_168"); put("walker", "3_156"); put("shuttle", "7_309"); put("tie", "1_304");
        }}, 50, 50, StartingSetup.LSStartingLocation("1261_113"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KashyyykForestDepthsStatsMatchFinalMaster() {
        //test1: independently read final-master title, icons and location attributes.
        var scn = scenario(); var b = scn.GetLSStartingLocation().getBlueprint();
        assertEquals("Kashyyyk: Forest Depths", b.getTitle()); //test1
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertFalse(b.hasVirtualSuffix()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, b.getCardSubtype()); assertEquals(Title.Kashyyyk, b.getSystemName());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.DAGOBAH, Icon.EPISODE_I,
                Icon.PLANET, Icon.EXTERIOR_SITE, Icon.LIGHT_FORCE, Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.FOREST)));
    }

    private VirtualTableScenario battle(int occupiedLocations, boolean kashyyykBattle, boolean sameSite) {
        var scn = scenario(); scn.StartGame(); var forest = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetDSCard("skyhook"));
        for (String site : new String[]{"sacred", "haven"}) scn.MoveLocationToTable(scn.GetLSCard(site));
        var locations = new com.gempukku.swccgo.game.PhysicalCardImpl[]{forest,
                scn.GetLSCard("sacred"), scn.GetLSCard("haven"), scn.GetDSCard("skyhook")};
        String[] names = {"wookiee", "wookiee2", "wookiee3", "wookiee4"};
        for (int i = 0; i < occupiedLocations; i++) scn.MoveCardsToLocation(locations[sameSite ? 0 : i], scn.GetLSCard(names[i]));
        var site = kashyyykBattle ? forest : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(0);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.SkipToDamageSegment(true);
        return scn;
    }

    @Test public void KashyyykForestDepthsAddsNoAttritionWithoutWookiee() {
        //test1: a non-Wookiee character occupying Kashyyyk does not qualify.
        var scn = battle(0, true, false); assertEquals(0, scn.GetUnpaidDSAttrition()); //test1
    }
    @Test public void KashyyykForestDepthsAddsOneForOneOccupiedLocation() {
        //test1: count an occupied contested site, not only a controlled one.
        var scn = battle(1, true, false); assertEquals(1, scn.GetUnpaidDSAttrition()); //test1
    }
    @Test public void KashyyykForestDepthsAddsTwoForTwoOccupiedLocations() {
        var scn = battle(2, true, false); assertEquals(2, scn.GetUnpaidDSAttrition());
    }
    @Test public void KashyyykForestDepthsReachesThreeAtThreeOccupiedLocations() {
        var scn = battle(3, true, false); assertEquals(3, scn.GetUnpaidDSAttrition());
    }
    @Test public void KashyyykForestDepthsCapsFourOccupiedLocationsAtThree() {
        var scn = battle(4, true, false); assertEquals(3, scn.GetUnpaidDSAttrition());
    }
    @Test public void KashyyykForestDepthsCountsLocationsInsteadOfWookiees() {
        var scn = battle(4, true, true); assertEquals(1, scn.GetUnpaidDSAttrition());
    }
    @Test public void KashyyykForestDepthsDoesNotModifyBattlesOnAnotherPlanet() {
        var scn = battle(3, false, false); assertEquals(0, scn.GetUnpaidDSAttrition());
    }
    @Test public void KashyyykForestDepthsDoesNotAddAttritionAgainstItsOwner() {
        var scn = battle(3, true, false); assertEquals(1, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void KashyyykForestDepthsCountsOccupiedSystemAndAffectsSystemBattle() {
        //test1: locations include systems; an aboard Wookiee counts as being at that system.
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("kashyyyk"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetLSCard("falcon"), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        scn.BoardAsPassenger(scn.GetLSCard("falcon"), scn.GetLSCard("wookiee"));
        scn.BoardAsPilot(scn.GetDSCard("tie"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(0);
        scn.LSInitiateBattle(system); scn.PassAllResponses(); scn.SkipToDamageSegment(true);
        assertEquals(1, scn.GetUnpaidDSAttrition()); //test1
    }

    private void activation(boolean occupied, boolean contested) {
        var scn = scenario(); scn.StartGame(); var forest = scn.GetLSStartingLocation();
        if (occupied) scn.MoveCardsToLocation(forest, scn.GetDSCard("vader"));
        if (contested) scn.MoveCardsToLocation(forest, scn.GetLSCard("trooper"));
        assertEquals(occupied ? 1 : 0,
                scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), forest, scn.DS), scn.epsilon);
        assertEquals(2, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), forest, scn.LS), scn.epsilon);
        scn.SkipToDSTurn(Phase.ACTIVATE); int before = scn.GetDSForcePileCount();
        assertEquals(occupied ? 3 : 2, scn.DSActivateMaxForceAndPass());
        assertEquals(before + (occupied ? 3 : 2), scn.GetDSForcePileCount());
        if (occupied) {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
            assertEquals(0, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), forest, scn.DS), scn.epsilon);
        }
    }
    @Test public void KashyyykForestDepthsCancelsUnoccupiedDarkSideGeneration() { activation(false, false); }
    @Test public void KashyyykForestDepthsRestoresOccupiedDarkSideGeneration() { activation(true, false); }
    @Test public void KashyyykForestDepthsRequiresOccupationInsteadOfControlForGeneration() { activation(true, true); }

    private void deployment(boolean lightSide, boolean starship) {
        var scn = scenario(); scn.StartGame(); var forest = scn.GetLSStartingLocation();
        var safe = scn.GetLSCard("safeSite"); scn.MoveLocationToTable(safe);
        var card = lightSide ? scn.GetLSCard(starship ? "ship" : "speeder") : scn.GetDSCard(starship ? "shuttle" : "walker");
        if (lightSide) {
            scn.MoveCardsToLSHand(card); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(10);
            scn.LSDeployCard(card); assertFalse(scn.LSHasCardChoiceAvailable(forest));
            assertTrue(scn.LSHasCardChoiceAvailable(safe)); scn.LSChooseCard(safe);
        } else {
            scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSActivateForceCheat(10);
            scn.DSDeployCard(card); assertFalse(scn.DSHasCardChoiceAvailable(forest));
            assertTrue(scn.DSHasCardChoiceAvailable(safe)); scn.DSChooseCard(safe);
        }
        scn.PassAllResponses(); assertAtLocation(safe, card);
    }
    @Test public void KashyyykForestDepthsRejectsLightSideVehicleDeployment() { deployment(true, false); }
    @Test public void KashyyykForestDepthsRejectsDarkSideVehicleDeployment() { deployment(false, false); }
    @Test public void KashyyykForestDepthsRejectsLightSideStarshipDeployment() { deployment(true, true); }
    @Test public void KashyyykForestDepthsRejectsDarkSideStarshipDeployment() { deployment(false, true); }
}
