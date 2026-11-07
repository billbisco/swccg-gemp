package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_033_Tests {
    private VirtualTableScenario fixture() {
        return new VirtualTableScenario(new HashMap<>(Map.of("fighter", "1263_33", "jedi", "9_24",
                "pilot", "1_19", "nonpilot", "1_21", "hoth", "3_55", "otherShip", "12_90",
                "gtys", "14_31", "jediPilot", "601_171", "fighter2", "1263_33")),
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void printedIdentityAndStats() {
        var scn = fixture(); var bp = scn.GetLSCard("fighter").getBlueprint();
        assertEquals("Republic Starfighter", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide()); assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon); assertEquals(4, bp.getManeuver(), scn.epsilon);
        assertEquals(5, bp.getHyperspeed(), scn.epsilon); assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertEquals(1, bp.getPilotCapacity()); assertEquals(0, bp.getPassengerCapacity());
        assertEquals(Rarity.V, bp.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        assertEquals(CardSubtype.STARFIGHTER, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.PILOT, Icon.NAV_COMPUTER,
                Icon.REPUBLIC, Icon.SCOMP_LINK, Icon.DEATH_STAR_II, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>(List.of(ModelType.RECONNAISSANCE_FIGHTER)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test public void permanentPilotIsSuspendedWhileSeatOccupiedAndRestoredWhenVacated() {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("hoth"); var ship = scn.GetLSCard("fighter"); var pilot = scn.GetLSCard("pilot");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        assertEquals(1, scn.GetBattleDestinyAbility(ship));
        scn.BoardAsPilot(ship, pilot);
        assertEquals(0, scn.GetBattleDestinyAbility(ship));
        assertEquals(scn.GetBattleDestinyAbility(pilot), scn.GetLSAbilityAtLocation(system));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), pilot);
        assertEquals(1, scn.GetBattleDestinyAbility(ship));
    }

    private void deploy(String who, String to, int discount) {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("hoth"); var ship = scn.GetLSCard(to); var pilot = scn.GetLSCard(who);
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        scn.MoveCardsToLSHand(pilot); scn.LSActivateForceCheat(14); scn.SkipToLSTurn(Phase.DEPLOY);
        int before = scn.GetLSForcePile().size();
        scn.LSDeployCard(pilot);
        if (scn.LSHasCardChoiceAvailable(ship)) scn.LSChooseCard(ship);
        scn.PassAllResponses();
        assertEquals(ship, pilot.getAttachedTo());
        assertEquals(pilot.getBlueprint().getDeployCost() - discount, before - scn.GetLSForcePile().size(), scn.epsilon);
    }

    @Test public void actualNonJediPilotDeploysAboardForOneLess() { deploy("pilot", "fighter", 1); }
    @Test public void actualJediPilotDeploysAboardForTwoLess() { deploy("jedi", "fighter", 2); }
    @Test public void otherShipGetsNoDiscount() { deploy("pilot", "otherShip", 0); }

    private static final String GTYS = "Reveal pilot or unpiloted starfighter from hand";

    /** Get To Your Ships! on table, Hoth system on table, Force to pay for both cards. */
    private VirtualTableScenario gtysTable() {
        var scn = fixture(); scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("gtys"));
        scn.LSActivateForceCheat(14);
        return scn;
    }

    private void finishSimultaneousDeploy(VirtualTableScenario scn, PhysicalCardImpl fromReserve, PhysicalCardImpl ship) {
        for (int i = 0; i < 10; i++) {
            if (scn.LSHasCardChoiceAvailable(fromReserve)) scn.LSChooseCard(fromReserve);
            else if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("hoth"))) scn.LSChooseCard(scn.GetLSCard("hoth"));
            else if (scn.LSHasCardChoiceAvailable(ship)) scn.LSChooseCard(ship);
            else break;
        }
        scn.PassAllResponses();
    }

    @Test public void republicStarfighterMayBeRevealedUsingGetToYourShipsToDeployItsMatchingPilotFromReserve() {
        var scn = gtysTable();
        var ship = scn.GetLSCard("fighter"); var jedi = scn.GetLSCard("jediPilot"); var gtys = scn.GetLSCard("gtys");
        scn.MoveCardsToLSHand(ship);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(jedi); // after the activate phase, which takes cards from the top
        assertTrue("Jedi Pilot matches Republic Starfighter", scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), jedi, ship));
        assertTrue(scn.LSCardActionAvailable(gtys, GTYS));
        scn.LSUseCardAction(gtys, GTYS);
        assertTrue(scn.LSHasCardChoiceAvailable(ship));
        scn.LSChooseCard(ship);
        scn.PassAllResponses();
        finishSimultaneousDeploy(scn, jedi, ship);
        assertEquals(scn.GetLSCard("hoth"), scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), ship));
        assertTrue(scn.IsAboardAsPilot(ship, jedi));
    }

    @Test public void republicStarfighterMayBeDeployedFromReserveUsingGetToYourShipsWithItsMatchingPilot() {
        var scn = gtysTable();
        var ship = scn.GetLSCard("fighter"); var jedi = scn.GetLSCard("jediPilot"); var gtys = scn.GetLSCard("gtys");
        scn.MoveCardsToLSHand(jedi);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(ship); // after the activate phase, which takes cards from the top
        assertTrue(scn.LSCardActionAvailable(gtys, GTYS));
        scn.LSUseCardAction(gtys, GTYS);
        scn.LSChooseCard(jedi);
        scn.PassAllResponses();
        finishSimultaneousDeploy(scn, ship, ship);
        assertEquals(scn.GetLSCard("hoth"), scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), ship));
        assertTrue(scn.IsAboardAsPilot(ship, jedi));
    }

    @Test public void otherPermanentPilotStarfighterMayNotBeRevealedUsingGetToYourShips() {
        var scn = gtysTable();
        scn.MoveCardsToLSHand(scn.GetLSCard("otherShip"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("gtys"), GTYS));
    }

    @Test public void republicStarfighterInHandEnablesGetToYourShips() {
        var scn = gtysTable();
        scn.MoveCardsToLSHand(scn.GetLSCard("otherShip"), scn.GetLSCard("fighter2"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("gtys"), GTYS));
    }

}
