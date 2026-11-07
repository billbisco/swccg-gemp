package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1240_054_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("otherbay", "1_136"); put("traveler", "1_19"); put("smuggler", "1_11");
            put("vehicle", "2_75");
        }}, new HashMap<>() {{
            put("bay", "1240_54"); put("traveler", "1_194"); put("smuggler", "2_82");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KesselDockingBayPrintedSidesIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("bay").getBlueprint();
        assertEquals("Kessel: Spice Mines - Docking Bay", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(0, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Your docking bay transit from here requires 1 Force (free if your smuggler present).", bp.getLocationDarkSideGameText());
        assertEquals("Your docking bay transit from here requires 3 Force (free if your smuggler present).", bp.getLocationLightSideGameText());
        assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, bp.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.HOTH,
                Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.DOCKING_BAY)));
    }

    private VirtualTableScenario Ready(boolean lightTurn, int force) {
        var scn = GetScenario();
        scn.StartGame();
        if (lightTurn) scn.SkipToLSTurn(Phase.ACTIVATE);
        else scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveLocationToTable(scn.GetLSCard("otherbay"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), lightTurn ? scn.GetLSCard("traveler") : scn.GetDSCard("traveler"));
        scn.SkipToPhase(Phase.MOVE);
        if (lightTurn) {
            scn.EnsureLSForcePile(force);
            while (scn.GetLSForcePileCount() > force) scn.MoveCardsToTopOfLSUsedPile((PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        }
        else {
            scn.EnsureDSForcePile(force);
            while (scn.GetDSForcePileCount() > force) scn.MoveCardsToTopOfDSUsedPile((PhysicalCardImpl) scn.GetDSForcePile().getFirst());
        }
        return scn;
    }

    private void Transit(VirtualTableScenario scn, boolean light, boolean fromKessel) {
        var from = fromKessel ? scn.GetDSCard("bay") : scn.GetLSCard("otherbay");
        var to = fromKessel ? scn.GetLSCard("otherbay") : scn.GetDSCard("bay");
        var traveler = light ? scn.GetLSCard("traveler") : scn.GetDSCard("traveler");
        // A transit is started from the site's action when it offers one, or else from the rule-level transit action.
        String player = light ? scn.LS : scn.DS;
        if (scn.ActionAvailable(player, from, "transit")) scn.PlayerDecided(player, scn.GetCardActionId(player, from, "transit"));
        else scn.PlayerDecided(player, scn.GetCardActionId(player, "transit"));
        if (scn.HasCardChoiceAvailable(player, to)) scn.ChooseCards(player, to);
        if (scn.HasCardChoiceAvailable(player, traveler)) scn.ChooseCards(player, traveler);
        scn.PassAllResponses();
        assertAtLocation(to, traveler);
    }

    @Test
    public void KesselDockingBayDarkSidePaysExactlyOneForceForActualTransit() {
        var scn = Ready(false, 1);
        Transit(scn, false, true);
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test
    public void KesselDockingBayLightSidePaysExactlyThreeForceForActualTransit() {
        var scn = Ready(true, 3);
        Transit(scn, true, true);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void KesselDockingBayDarkSideCannotTransitWithNoForceWithoutSmuggler() {
        var scn = Ready(false, 0);
        if (scn.DSCardActionAvailable(scn.GetDSCard("bay"), "transit")) {
            scn.DSUseCardAction(scn.GetDSCard("bay"), "transit");
            if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("otherbay"))) scn.DSChooseCard(scn.GetLSCard("otherbay"));
            assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetDSCard("traveler")));
        }
        assertAtLocation(scn.GetDSCard("bay"), scn.GetDSCard("traveler"));
    }

    @Test
    public void KesselDockingBayLightSideCannotTransitWithOnlyTwoForceWithoutSmuggler() {
        var scn = Ready(true, 2);
        if (scn.LSCardActionAvailable(scn.GetDSCard("bay"), "transit")) {
            scn.LSUseCardAction(scn.GetDSCard("bay"), "transit");
            if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("otherbay"))) scn.LSChooseCard(scn.GetLSCard("otherbay"));
            assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetLSCard("traveler")));
        }
        assertAtLocation(scn.GetDSCard("bay"), scn.GetLSCard("traveler"));
    }

    @Test
    public void KesselDockingBayDarkSmugglerAllowsFreeTransitForAnotherCharacter() {
        var scn = Ready(false, 0);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("smuggler"));
        Transit(scn, false, true);
        assertEquals(0, scn.GetDSForcePileCount());
        assertAtLocation(scn.GetDSCard("bay"), scn.GetDSCard("smuggler"));
    }

    @Test
    public void KesselDockingBayLightSmugglerAllowsFreeTransitForAnotherCharacter() {
        var scn = Ready(true, 0);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetLSCard("smuggler"));
        Transit(scn, true, true);
        assertEquals(0, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetDSCard("bay"), scn.GetLSCard("smuggler"));
    }

    @Test
    public void KesselDockingBayOpponentsSmugglerDoesNotMakeDarkTransitFree() {
        var scn = Ready(false, 1);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetLSCard("smuggler"));
        Transit(scn, false, true);
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test
    public void KesselDockingBayOpponentsSmugglerDoesNotMakeLightTransitFree() {
        var scn = Ready(true, 3);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("smuggler"));
        Transit(scn, true, true);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void KesselDockingBaySmugglerAtDestinationDoesNotWaiveDarkOriginCost() {
        var scn = Ready(false, 1);
        scn.MoveCardsToLocation(scn.GetLSCard("otherbay"), scn.GetDSCard("smuggler"));
        Transit(scn, false, true);
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test
    public void KesselDockingBaySmugglerAboardEnclosedVehicleIsNotPresentForFreeTransit() {
        var scn = Ready(true, 3);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetLSCard("vehicle"));
        scn.BoardAsPassenger(scn.GetLSCard("vehicle"), scn.GetLSCard("smuggler"));
        Transit(scn, true, true);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void KesselDockingBayRemovingOwnSmugglerRestoresPrintedTransitCost() {
        var scn = Ready(true, 3);
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetLSCard("smuggler"));
        assertEquals(0, Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(),
                scn.GetLSCard("traveler"), scn.GetDSCard("bay"), scn.GetLSCard("otherbay"), 0)));
        scn.MoveCardsToLSHand(scn.GetLSCard("smuggler"));
        Transit(scn, true, true);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    private int TransitCost(VirtualTableScenario scn, PhysicalCardImpl traveler, PhysicalCardImpl from, PhysicalCardImpl to) {
        return Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(), traveler, from, to, 0));
    }

    @Test
    public void KesselDockingBayDoesNotApplyDarkFromCostToIncomingTransit() {
        var scn = Ready(false, 2);
        // Original Yavin 4 Docking Bay: Dark transit from there requires 2; Kessel's 'from here' cost does not add.
        assertEquals(2, TransitCost(scn, scn.GetDSCard("traveler"), scn.GetLSCard("otherbay"), scn.GetDSCard("bay")));
        assertEquals(1, TransitCost(scn, scn.GetDSCard("traveler"), scn.GetDSCard("bay"), scn.GetLSCard("otherbay")));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("smuggler"));
        // A smuggler present at Kessel does not waive the cost of transit from elsewhere into Kessel.
        assertEquals(2, TransitCost(scn, scn.GetDSCard("traveler"), scn.GetLSCard("otherbay"), scn.GetDSCard("bay")));
    }

    @Test
    public void KesselDockingBayDoesNotChargeLightFromCostOnFreeIncomingTransit() {
        var scn = Ready(true, 0);
        // Original Yavin 4 Docking Bay: Light transit from there is free; Kessel's 3-Force 'from here' cost does not add.
        assertEquals(0, TransitCost(scn, scn.GetLSCard("traveler"), scn.GetLSCard("otherbay"), scn.GetDSCard("bay")));
        assertEquals(3, TransitCost(scn, scn.GetLSCard("traveler"), scn.GetDSCard("bay"), scn.GetLSCard("otherbay")));
    }
}
