package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_036_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("artoo", "1250_36"); put("ownDroid", "1_5"); put("ship", "1115_26"); put("bravo", "1116_26");
            put("ordinary", "1_140"); put("epiVehicle", "14_60"); put("speeder", "3_69"); put("system", "1_127"); put("pilot", "12_1");
            put("senate", "12_75"); put("chamber", "1116_29"); put("archives", "1117_4"); put("platform", "1113_3");
        }}, new HashMap<>() {{
            put("droid1", "1_163"); put("droid2", "1_186"); put("trooper", "1_194");
            put("destroyer", "1_301"); put("motti", "1_164"); put("decree", "1106_38");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test
    public void ArtooMetadataMatchesPrinted34Astromech() {
        var scn = scenario(); var bp = scn.GetLSCard("artoo").getBlueprint();
        assertEquals("Artoo, Brave Little Droid", bp.getTitle()); assertTrue(bp.hasVirtualSuffix()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(6, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon); assertEquals(2, bp.getPower(), scn.epsilon); assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.DROID); add(Icon.THEED_PALACE); add(Icon.EPISODE_I); add(Icon.NAV_COMPUTER); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>() {{ add(ModelType.ASTROMECH); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.R2D2); }}); scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }
    private void setupDroids(VirtualTableScenario scn, boolean two, boolean darkTurn) {
        scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("artoo"), scn.GetLSCard("ownDroid"), scn.GetDSCard("droid1"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(two ? site : scn.GetDSStartingLocation(), scn.GetDSCard("droid2"));
        if (darkTurn) { scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass(); } else scn.SkipToLSTurn(Phase.CONTROL);
    }
    private void target(VirtualTableScenario scn, boolean two) {
        var artoo = scn.GetLSCard("artoo"); assertTrue(scn.LSCardActionAvailable(artoo, "Target up to two opponent's droids"));
        scn.LSUseCardAction(artoo, "Target up to two opponent's droids");
        if (scn.LSDecisionAvailable("Choose up to two opponent's droids")) {
            assertFalse(scn.LSHasCardChoicesAvailable(scn.GetLSCard("ownDroid"))); assertFalse(scn.LSHasCardChoicesAvailable(scn.GetDSCard("trooper")));
            assertEquals(scn.GetLSStartingLocation().equals(scn.GetDSCard("droid2").getAtLocation()) ? 2 : 1, scn.LSGetChoiceMax());
            if (two) scn.LSChooseCards(scn.GetDSCard("droid1"), scn.GetDSCard("droid2")); else scn.LSChooseCard(scn.GetDSCard("droid1"));
        }
        scn.PassAllResponses(); assertTrue(scn.DSDecisionAvailable("Place targeted droids in Used Pile or lose 4 Force"));
    }
    @Test
    public void ArtooOwnerMayPlaceTwoTargetDroidsInUsedPile() { checkChoice(true, false, false); }
    @Test
    public void ArtooMayTargetOnlyOneOfTwoPresentDroids() { checkChoice(false, false, false); }
    @Test
    public void ArtooDroidOwnerMayLoseExactlyFourForceInstead() { checkChoice(true, true, false); }
    @Test
    public void ArtooMayUseDroidActionDuringOpponentsTurn() { checkChoice(true, false, true); }

    @Test
    public void ArtooForceLossCanBeReducedByImperialDecree() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        var droid = scn.GetDSCard("droid1");
        var decree = scn.GetDSCard("decree");
        scn.MoveCardsToLocation(site, scn.GetLSCard("artoo"), droid);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("motti"));
        scn.MoveCardsToDSSideOfTable(decree);
        scn.SkipToLSTurn(Phase.CONTROL);
        int life = scn.GetDSLifeForceRemaining();
        target(scn, false);
        scn.DSChooseOption("Lose 4 Force");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(decree, "Reduce Force loss"));
        scn.DSUseCardAction(decree, "Reduce Force loss");
        scn.PassAllResponses();
        for (int i = 0; i < 3; i++) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }
        assertEquals(life - 3, scn.GetDSLifeForceRemaining());
        assertEquals(site, droid.getAtLocation());
    }

    private void checkChoice(boolean two, boolean loseForce, boolean darkTurn) {
        var scn = scenario(); setupDroids(scn, true, darkTurn); int life = scn.GetDSLifeForceRemaining();
        target(scn, two);
        if (loseForce) {
            scn.DSChooseOption("Lose 4 Force"); scn.PassAllResponses();
            for (int i = 0; i < 4; i++) { scn.DSChooseCard(scn.GetTopOfDSReserveDeck()); scn.PassAllResponses(); }
            assertEquals(life - 4, scn.GetDSLifeForceRemaining());
            assertEquals(scn.GetLSStartingLocation(), scn.GetDSCard("droid1").getAtLocation());
            assertEquals(scn.GetLSStartingLocation(), scn.GetDSCard("droid2").getAtLocation());
        }
        else {
            scn.DSChooseOption("Place targets in Used Pile"); scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose card to place")) { scn.DSChooseCard(scn.GetDSCard("droid1")); scn.PassAllResponses(); }
            assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("droid1"))); assertEquals(two, scn.GetDSUsedPile().contains(scn.GetDSCard("droid2")));
            assertEquals(life + (two ? 2 : 1), scn.GetDSLifeForceRemaining());
        }
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("ownDroid").getAtLocation()); assertEquals(scn.GetLSStartingLocation(), scn.GetDSCard("trooper").getAtLocation());
    }
    @Test
    public void ArtooMayTargetSolePresentDroidButNotDroidAtAnotherSite() {
        var scn = scenario(); setupDroids(scn, false, false); target(scn, false);
        scn.DSChooseOption("Place targets in Used Pile"); scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("droid1"))); assertEquals(scn.GetDSStartingLocation(), scn.GetDSCard("droid2").getAtLocation());
    }
    @Test
    public void ArtooCannotUseDroidActionWithoutOpponentDroidPresent() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("artoo"), scn.GetLSCard("ownDroid"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("droid1")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("artoo"), "Target up to two opponent's droids"));
    }
    @Test
    public void ArtooAboardEnclosedVehicleIsNotPresentWithDroidsOnFoot() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("speeder"), scn.GetDSCard("droid1")); scn.BoardAsPassenger(scn.GetLSCard("speeder"), scn.GetLSCard("artoo"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("artoo"), "Target up to two opponent's droids"));
    }
    @Test
    public void ArtooDroidActionDoesNotRefreshOnBounceAndActualRedeployment() {
        var scn = scenario(); setupDroids(scn, true, false); target(scn, false); scn.DSChooseOption("Place targets in Used Pile"); scn.PassAllResponses();
        scn.MoveCardsToHand(scn.GetLSCard("artoo")); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("artoo")); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses(); scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("artoo"), "Target up to two opponent's droids"));
    }
    @Test
    public void ArtooActuallyFliesTwoSitesForNormalLandspeedCost() {
        var scn = scenario(); scn.StartGame(); var from = scn.GetLSCard("senate"); var middle = scn.GetLSCard("chamber"); var to = scn.GetLSCard("archives");
        scn.MoveLocationToTable(from); scn.MoveLocationToTable(middle); scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, scn.GetLSCard("artoo")); scn.SkipToLSTurn(Phase.MOVE); assertEquals(2, scn.GetLandspeed(scn.GetLSCard("artoo")));
        int force = scn.GetLSForcePileCount(); scn.LSMoveCard(scn.GetLSCard("artoo"), to); scn.PassAllResponses();
        assertEquals(to, scn.GetLSCard("artoo").getAtLocation()); assertEquals(force - 1, scn.GetLSForcePileCount());
    }
    @Test
    public void ArtooAboardEpisodeIShipAddsPowerAndNewImmunityThenExpiresOnDeparture() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ship"); scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        int power = scn.GetPower(ship); assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.BoardAsPassenger(ship, scn.GetLSCard("artoo")); assertEquals(power + 3, scn.GetPower(ship));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveOutOfPlay(scn.GetLSCard("artoo")); assertEquals(power, scn.GetPower(ship)); assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
    @Test
    public void ArtooAddsTwoToExistingImmunityInsteadOfReplacingItWithFour() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("bravo"); scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.BoardAsPassenger(ship, scn.GetLSCard("artoo")); assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
    @Test
    public void ArtooDoesNotImproveNonEpisodeIShip() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ordinary"); scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        int power = scn.GetPower(ship); scn.BoardAsPassenger(ship, scn.GetLSCard("artoo")); assertEquals(power, scn.GetPower(ship));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
    @Test
    public void ArtooDoesNotImproveEpisodeIVehicle() {
        var scn = scenario(); scn.StartGame(); var vehicle = scn.GetLSCard("epiVehicle"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vehicle);
        int power = scn.GetPower(vehicle); scn.BoardAsPassenger(vehicle, scn.GetLSCard("artoo")); assertEquals(power, scn.GetPower(vehicle));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vehicle), scn.epsilon);
    }
    @Test
    public void ArtooShipCanIgnoreThreeActualAttritionBelowFour() { checkAttrition(3, true); }
    @Test
    public void ArtooShipMustForfeitAtFourActualAttrition() { checkAttrition(4, false); }
    private void checkAttrition(int attrition, boolean ignore) {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ship"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("destroyer")); scn.BoardAsPilot(ship, scn.GetLSCard("pilot")); scn.BoardAsPassenger(ship, scn.GetLSCard("artoo"));
        scn.BoardAsPilot(scn.GetDSCard("destroyer"), scn.GetDSCard("motti")); scn.LSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareLSDestiny(7); scn.PrepareDSDestiny(attrition); scn.SkipToDamageSegment(true);
        assertEquals(0, scn.gameState().getBattleState().getBattleDamageRemaining(scn.game(), scn.LS), scn.epsilon);
        assertEquals(attrition, scn.gameState().getBattleState().getAttritionRemaining(scn.game(), scn.LS), scn.epsilon);
        assertTrue(scn.LSDecisionAvailable("card from battle to forfeit")); assertEquals(ignore ? 0 : 1, scn.LSGetChoiceMin());
        if (ignore) scn.LSChooseCards(); else scn.LSChooseCard(scn.GetLSCard("artoo")); scn.PassAllResponses();
        assertEquals(!ignore, scn.GetLSLostPile().contains(scn.GetLSCard("artoo")));
    }
}
