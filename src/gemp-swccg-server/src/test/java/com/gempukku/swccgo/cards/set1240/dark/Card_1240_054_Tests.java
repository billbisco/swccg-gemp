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
        if (lightTurn) {
            scn.EnsureLSForcePile(force);
            while (scn.GetLSForcePileCount() > force) scn.MoveCardsToTopOfLSUsedPile((PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        }
        else {
            scn.EnsureDSForcePile(force);
            while (scn.GetDSForcePileCount() > force) scn.MoveCardsToTopOfDSUsedPile((PhysicalCardImpl) scn.GetDSForcePile().getFirst());
        }
        scn.SkipToPhase(Phase.MOVE);
        return scn;
    }

    private void Transit(VirtualTableScenario scn, boolean light, boolean fromKessel) {
        var from = fromKessel ? scn.GetDSCard("bay") : scn.GetLSCard("otherbay");
        var to = fromKessel ? scn.GetLSCard("otherbay") : scn.GetDSCard("bay");
        var traveler = light ? scn.GetLSCard("traveler") : scn.GetDSCard("traveler");
        if (light) {
            scn.LSUseCardAction(from, "transit");
            scn.LSChooseCard(to);
            scn.LSChooseCard(traveler);
        }
        else {
            scn.DSUseCardAction(from, "transit");
            scn.DSChooseCard(to);
            scn.DSChooseCard(traveler);
        }
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
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bay"), "transit"));
    }

    @Test
    public void KesselDockingBayLightSideCannotTransitWithOnlyTwoForceWithoutSmuggler() {
        var scn = Ready(true, 2);
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("bay"), "transit"));
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

    @Test
    public void KesselDockingBayDoesNotApplyDarkFromCostToIncomingTransit() {
        var scn = Ready(false, 2);
        scn.MoveCardsToLocation(scn.GetLSCard("otherbay"), scn.GetDSCard("traveler"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("smuggler"));
        Transit(scn, false, false);
        assertEquals(0, scn.GetDSForcePileCount()); // Original Yavin4 origin costs2.
    }

    @Test
    public void KesselDockingBayDoesNotChargeLightFromCostOnFreeIncomingTransit() {
        var scn = Ready(true, 0);
        scn.MoveCardsToLocation(scn.GetLSCard("otherbay"), scn.GetLSCard("traveler"));
        Transit(scn, true, false);
        assertEquals(0, scn.GetLSForcePileCount()); // Original Yavin4 origin is free.
    }
}
