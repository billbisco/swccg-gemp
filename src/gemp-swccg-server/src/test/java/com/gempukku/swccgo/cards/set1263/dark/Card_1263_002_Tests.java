package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1263_002_Tests {
    private static final String EXCHANGE = "Exchange hand card with random Lost Pile card";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("brangus", "1263_2"); put("bay", "5_169"); put("casino", "7_269"); put("ship", "3_152"); put("fighter", "1_299"); put("vehicle", "3_154"); put("system", "1_282"); put("quiet", "1_281"); put("hand1", "1_201"); put("hand2", "1_317"); put("lost1", "1_194"); put("lost2", "3_87"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void BrangusGleeStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("brangus").getBlueprint();
        assertEquals("Brangus Glee", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon); assertEquals(4, bp.getAbility(), scn.epsilon); assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.GAMBLER); }});
    }
    private VirtualTableScenario ships() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("system")); scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("ship"), scn.GetDSCard("fighter"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vehicle")); return scn;
    }
    @Test public void BrangusGleeAddsTwoToAnyPilotedShipOrVehicleButNotAsPassenger() {
        var scn = ships(); var brangus = scn.GetDSCard("brangus"); var ship = scn.GetDSCard("ship");
        scn.BoardAsPilot(ship, brangus); scn.SkipToDSTurn(Phase.CONTROL); assertEquals(10, scn.GetPower(ship));
        scn.MoveCardsToDSHand(brangus); assertEquals(8, scn.GetPower(ship)); scn.BoardAsPassenger(ship, brangus); assertEquals(8, scn.GetPower(ship));
        scn.MoveCardsToDSHand(brangus); scn.BoardAsPilot(scn.GetDSCard("fighter"), brangus); assertEquals(3, scn.GetPower(scn.GetDSCard("fighter")));
        scn.MoveCardsToDSHand(brangus); scn.BoardAsPilot(scn.GetDSCard("vehicle"), brangus); assertEquals(9, scn.GetPower(scn.GetDSCard("vehicle")));
        scn.MoveCardsToDSHand(brangus); scn.BoardAsPassenger(scn.GetDSCard("vehicle"), brangus); assertEquals(0, scn.GetPower(scn.GetDSCard("vehicle")));
    }
    @Test public void BrangusGleeForfeitBonusAppliesAtBayOrAboardShipAndDoesNotStack() {
        var scn = ships(); var brangus = scn.GetDSCard("brangus"); var ship = scn.GetDSCard("ship");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), brangus); scn.SkipToDSTurn(Phase.CONTROL); assertEquals(4, scn.GetForfeit(brangus));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), brangus); assertEquals(5, scn.GetForfeit(brangus));
        scn.BoardAsPassenger(ship, brangus); assertEquals(5, scn.GetForfeit(brangus));
        scn.MoveCardsToDSHand(brangus); scn.BoardAsPilot(scn.GetDSCard("fighter"), brangus); scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("fighter")); assertEquals(5, scn.GetForfeit(brangus));
        scn.MoveCardsToDSHand(brangus); scn.BoardAsPassenger(scn.GetDSCard("vehicle"), brangus); assertEquals(4, scn.GetForfeit(brangus));
    }
    private void deploy(String target, int force, int spent) {
        var scn = ships(); var brangus = scn.GetDSCard("brangus");
        scn.MoveCardsToDSHand(brangus); scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new)); scn.EnsureDSForcePile(force); scn.SkipToPhase(Phase.DEPLOY);
        var destination = target.equals("ordinary") ? scn.GetDSStartingLocation() : scn.GetDSCard(target);
        if (spent > force) { assertFalse(scn.DSDeployAvailable(brangus)); return; }
        assertTrue(scn.DSDeployAvailable(brangus)); scn.DSDeployCard(brangus); scn.DSChooseCard(destination);
        if (scn.DSDecisionAvailable("Choose capacity slot")) scn.DSChoose("Pilot");
        scn.PassAllResponses(); assertEquals(force - spent, scn.GetDSForcePileCount());
        assertEquals(destination, target.equals("ship") ? brangus.getAttachedTo() : brangus.getAtLocation());
    }
    @Test public void BrangusGleeDeploysToBayForExactlyTwoForce() { deploy("bay", 2, 2); }
    @Test public void BrangusGleeDeploysAboardStarshipForExactlyTwoForce() { deploy("ship", 2, 2); }
    @Test public void BrangusGleeOrdinarySiteDeploymentCostsThree() { deploy("ordinary", 3, 3); }
    @Test public void BrangusGleeOneForceCannotPayReducedDeployCost() { deploy("bay", 1, 2); }
    private void exactForce(VirtualTableScenario scn, int amount) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new)); scn.EnsureDSForcePile(amount);
    }
    private VirtualTableScenario exchangeSetup(String place, int force, boolean hand, int lost) {
        var scn = scenario(); scn.StartGame(); var brangus = scn.GetDSCard("brangus");
        if (place.equals("quiet")) {
            scn.MoveLocationToTable(scn.GetDSCard("quiet")); scn.MoveCardsToLocation(scn.GetDSCard("quiet"), scn.GetDSCard("ship")); scn.BoardAsPilot(scn.GetDSCard("ship"), brangus);
        }
        else if (place.equals("casino")) { scn.MoveLocationToTable(scn.GetDSCard("casino")); scn.MoveCardsToLocation(scn.GetDSCard("casino"), brangus); }
        else scn.MoveCardsToLocation(scn.GetDSStartingLocation(), brangus);
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getHand(scn.DS).toArray(PhysicalCardImpl[]::new));
        if (hand) scn.MoveCardsToDSHand(scn.GetDSCard("hand1"), scn.GetDSCard("hand2"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getLostPile(scn.DS).toArray(PhysicalCardImpl[]::new));
        if (lost > 0) scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost1"));
        if (lost > 1) scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost2"));
        scn.SkipToDSTurn(Phase.ACTIVATE); exactForce(scn, force); scn.DSPass();
        if (scn.DSDecisionAvailable("You have not activated Force")) scn.DSChooseYes();
        if (scn.AnyDecisionsAvailable(scn.LS)) { assertTrue(scn.LSDecisionAvailable("activate")); scn.LSPass(); }
        assertTrue(scn.AwaitingDSControlPhaseActions()); assertEquals(force, scn.GetDSForcePileCount()); return scn;
    }
    private void exchange(VirtualTableScenario scn, PhysicalCardImpl handCard) {
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); scn.DSUseCardAction(scn.GetDSCard("brangus"), EXCHANGE);
        // The player chooses the hand card; no Lost Pile chooser is part of the random exchange.
        assertTrue(scn.DSHasCardChoiceAvailable(handCard)); scn.DSChooseCard(handCard); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, handCard);
    }
    private void darkActions(VirtualTableScenario scn) {
        if (scn.AnyDecisionsAvailable(scn.LS)) scn.LSPass(); assertTrue(scn.AwaitingDSControlPhaseActions());
    }
    @Test public void BrangusGleePaysOneForceToExchangeAnyHandCardWithLostCard() {
        var scn = exchangeSetup("ordinary", 1, true, 1); int hand = scn.GetDSHandCount(); exchange(scn, scn.GetDSCard("hand1"));
        assertEquals(0, scn.GetDSForcePileCount()); assertInZone(Zone.HAND, scn.GetDSCard("lost1")); assertEquals(hand, scn.GetDSHandCount()); assertEquals(1, scn.GetDSLostPileCount());
        darkActions(scn); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE));
    }
    @Test public void BrangusGleeCasinoExchangeIsFreeWithZeroForceAndMayRepeat() {
        var scn = exchangeSetup("casino", 0, true, 1); exchange(scn, scn.GetDSCard("hand1")); assertEquals(0, scn.GetDSForcePileCount());
        darkActions(scn); exchange(scn, scn.GetDSCard("hand2")); assertEquals(0, scn.GetDSForcePileCount()); assertEquals(1, scn.GetDSLostPileCount());
    }
    @Test public void BrangusGleeNoncasinoExchangeMayRepeatIfForceAvailable() {
        var scn = exchangeSetup("ordinary", 2, true, 1); exchange(scn, scn.GetDSCard("hand1")); darkActions(scn); exchange(scn, scn.GetDSCard("hand2")); assertEquals(0, scn.GetDSForcePileCount());
    }
    @Test public void BrangusGleeZeroForceOutsideCasinoCannotExchange() { var scn = exchangeSetup("ordinary", 0, true, 1); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeEmptyHandCannotExchange() { var scn = exchangeSetup("ordinary", 1, false, 1); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeEmptyLostPileCannotExchange() { var scn = exchangeSetup("ordinary", 1, true, 0); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeCannotExchangeAtNonbattlegroundSystem() { var scn = exchangeSetup("quiet", 1, true, 1); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeExchangeIsUnavailableDuringYourDeployPhase() { var scn = exchangeSetup("ordinary", 1, true, 1); scn.SkipToPhase(Phase.DEPLOY); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeExchangeIsUnavailableDuringOpponentsControlPhase() { var scn = exchangeSetup("ordinary", 1, true, 1); scn.SkipToLSTurn(Phase.CONTROL); scn.LSPass(); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("brangus"), EXCHANGE)); }
    @Test public void BrangusGleeRandomExchangeSelectsEitherLostCardAndPreservesOtherPileOrder() {
        var outcomes = new HashSet<String>();
        for (long seed : new long[]{0, 4096, 8192, 12288}) {
            var scn = exchangeSetup("ordinary", 1, true, 2); var before = new ArrayList<>(scn.gameState().getLostPile(scn.DS));
            scn.gameState().getRandom().setSeed(seed); var hand = scn.GetDSCard("hand1"); exchange(scn, hand);
            var selected = scn.GetDSCard("lost1").getZone() == Zone.HAND ? scn.GetDSCard("lost1") : scn.GetDSCard("lost2");
            assertInZone(Zone.HAND, selected); outcomes.add(selected.getBlueprintId(true)); before.set(before.indexOf(selected), hand); assertEquals(before, scn.gameState().getLostPile(scn.DS));
        }
        assertEquals(2, outcomes.size());
    }
    private void attrition(int destiny, boolean immune) {
        var scn = scenario(); var brangus = scn.GetDSCard("brangus"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, brangus, scn.GetLSCard("luke")); scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(7); scn.PrepareLSDestiny(destiny); scn.DSInitiateBattle(site); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(destiny, scn.GetUnpaidDSAttrition()); assertEquals(immune, scn.DSDecisionAvailable("(if desired)"));
        if (immune) { scn.DSPass(); assertInZone(Zone.AT_LOCATION, brangus); }
        else { scn.DSChooseCard(brangus); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, brangus); }
    }
    @Test public void BrangusGleeSurvivesAttritionBelowThree() { attrition(2, true); }
    @Test public void BrangusGleeDoesNotProtectFromAttritionEqualToThree() { attrition(3, false); }
    @Test public void BrangusGleeDoesNotProtectFromAttritionAboveThree() { attrition(4, false); }
}
