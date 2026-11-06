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

public class Card_1261_103_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("falcon", "1261_103"); put("oldFalcon", "1_143"); put("red5", "2_71"); put("royal", "12_91");
            put("luke", "1_19"); put("pilot", "1_27"); put("trooper", "1_28");
            put("maneuvers", "1_70"); put("debt", "6_70");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("tie", "1_304"); put("shuttle", "7_309"); put("scythe", "9_175");
            put("come", "2_118"); put("damage", "1_222");
        }}, 50, 50, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario start() {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("falcon"));
        return scn;
    }

    @Test
    public void HanChewieAndTheFalconStatsMatchFinalMasterInsteadOfSourcePack() {
        //test1: the final slip prints maneuver5 and forfeit6, with no extra passenger capacity.
        var scn = scenario(); var b = scn.GetLSCard("falcon").getBlueprint();
        assertEquals("Han, Chewie, And The Falcon", b.getTitle()); assertTrue(b.hasVirtualSuffix()); //test1
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(6, b.getDeployCost(), scn.epsilon);
        assertEquals(8, b.getPower(), scn.epsilon); assertEquals(5, b.getManeuver(), scn.epsilon);
        assertEquals(7, b.getHyperspeed(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(0, b.getPilotCapacity()); assertEquals(0, b.getPassengerCapacity());
        assertEquals(CardSubtype.STARFIGHTER, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.STARSHIP)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.REFLECTIONS_III, Icon.PILOT, Icon.PILOT,
                Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.STARSHIP, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintModelTypeCheck(b, new ArrayList<>(Arrays.asList(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(Arrays.asList(Persona.FALCON)));
    }

    @Test
    public void HanChewieAndTheFalconPermanentPilotsProvideFiveAbility() {
        var scn = start(); var falcon = scn.GetLSCard("falcon");
        assertEquals(5, scn.GetBattleDestinyAbility(falcon));
        assertTrue(Filters.hasPermanentAboard(Filters.Han).accepts(scn.game(), falcon));
        assertTrue(Filters.hasPermanentAboard(Filters.Chewie).accepts(scn.game(), falcon));
    }

    @Test
    public void HanChewieAndTheFalconDoesNotGrantUnprintedPilotCapacity() {
        //test1: a pilot can deploy to another ship, but not aboard this full crew card.
        var scn = start(); var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetLSCard("red5"), scn.GetLSCard("royal"));
        scn.MoveCardsToLSHand(scn.GetLSCard("pilot")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(3);
        scn.LSDeployCard(scn.GetLSCard("pilot"));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("falcon"))); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("red5")));
        scn.LSChooseCard(scn.GetLSCard("red5")); scn.PassAllResponses();
        assertEquals(scn.GetLSCard("red5"), scn.GetLSCard("pilot").getAttachedTo());
    }

    @Test
    public void HanChewieAndTheFalconDoesNotGrantUnprintedPassengerCapacity() {
        var scn = start(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("royal"));
        scn.MoveCardsToLSHand(scn.GetLSCard("trooper")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(3);
        scn.LSDeployCard(scn.GetLSCard("trooper"));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("falcon")));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("royal"))) scn.LSChooseCard(scn.GetLSCard("royal"));
        scn.PassAllResponses(); assertEquals(scn.GetLSCard("royal"), scn.GetLSCard("trooper").getAttachedTo());
    }

    @Test
    public void HanChewieAndTheFalconTakesManeuversAndMayUseItNormally() {
        //test1: the up-triangle means take into hand, not deploy or play the Interrupt.
        //test2: using the fetched card produces the ordinary maneuver/hyperspeed bonus.
        var scn = start(); scn.SkipToLSTurn(Phase.DEPLOY); var falcon = scn.GetLSCard("falcon");
        scn.LSUseCardAction(falcon, "Take A Few Maneuvers"); scn.LSChooseCard(scn.GetLSCard("maneuvers"));
        scn.PassAllResponses(); assertInHand(scn.GetLSCard("maneuvers")); //test1
        assertEquals(5, scn.GetDefense(falcon));
        assertFalse(scn.LSCardActionAvailable(falcon, "Take A Few Maneuvers"));
        scn.LSPlayCard(scn.GetLSCard("maneuvers")); scn.LSChooseCard(falcon); scn.PassAllResponses();
        assertEquals(7, scn.GetDefense(falcon)); //test2
        assertInZone(Zone.USED_PILE, scn.GetLSCard("maneuvers"));
    }

    @Test
    public void HanChewieAndTheFalconSearchDoesNotResetNextTurnOrOnReentry() {
        var scn = start(); var falcon = scn.GetLSCard("falcon"); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(falcon, "Take A Few Maneuvers"); scn.LSChooseCard(scn.GetLSCard("maneuvers")); scn.PassAllResponses();
        scn.MoveCardsToLSHand(falcon); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), falcon);
        scn.SkipToDSTurn(Phase.ACTIVATE); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(falcon, "Take A Few Maneuvers"));
        assertInHand(scn.GetLSCard("maneuvers"));
    }

    @Test
    public void HanChewieAndTheFalconDoesNotSearchAnEmptyReserveDeck() {
        var scn = start(); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSReserveDeck().toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("falcon"), "Take A Few Maneuvers"));
    }

    private void lifeDebt(boolean falconInPlay) {
        var scn = start(); if (!falconInPlay) scn.MoveCardsToLSHand(scn.GetLSCard("falcon"));
        scn.MoveCardsToLSHand(scn.GetLSCard("debt")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(scn.GetLSCard("debt"), "Take Falcon into hand"); scn.PassAllResponses();
        if (!falconInPlay) {
            assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("oldFalcon")));
            scn.LSChooseCard(scn.GetLSCard("oldFalcon")); scn.PassAllResponses();
        }
        assertInZone(falconInPlay ? Zone.RESERVE_DECK : Zone.HAND, scn.GetLSCard("oldFalcon"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("debt"));
    }
    @Test public void HanChewieAndTheFalconCancelsLifeDebtInsteadOfOnlyBeingImmune() { lifeDebt(true); }
    @Test public void HanChewieAndTheFalconDoesNotCancelLifeDebtWhenOutOfPlay() { lifeDebt(false); }

    @Test
    public void HanChewieAndTheFalconRejectsComeWithMeWhileLegalControlResolves() {
        var scn = start(); var shuttle = scn.GetDSCard("shuttle");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), shuttle, scn.GetDSCard("scythe"));
        scn.MoveCardsToDSHand(scn.GetDSCard("come")); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSActivateForceCheat(2);
        scn.PrepareDSDestiny(3); scn.DSDeployCard(scn.GetDSCard("come"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("falcon")));
        assertTrue(scn.DSHasCardChoiceAvailable(shuttle)); scn.DSChooseCard(shuttle); scn.PassAllResponses();
        assertEquals(shuttle, scn.GetDSCard("come").getAttachedTo());
        assertEquals(0, scn.GetBattleDestinyAbility(shuttle));
        assertEquals(5, scn.GetBattleDestinyAbility(scn.GetLSCard("falcon")));
    }

    @Test
    public void HanChewieAndTheFalconRejectsLateralDamageWhileLegalControlResolves() {
        var scn = start(); var red5 = scn.GetLSCard("red5");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), red5, scn.GetLSCard("royal"));
        scn.BoardAsPilot(red5, scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("damage")); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("damage")); scn.DSChooseCard(scn.GetDSStartingLocation());
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("falcon")));
        assertTrue(scn.DSHasCardChoiceAvailable(red5)); scn.DSChooseCard(red5); scn.PassAllResponses();
        assertEquals(0, scn.GetPower(red5)); assertEquals(0, scn.GetForfeit(red5));
        assertEquals(8, scn.GetPower(scn.GetLSCard("falcon"))); assertEquals(6, scn.GetForfeit(scn.GetLSCard("falcon")));
    }

    private VirtualTableScenario battle(int destiny) {
        var scn = start(); var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetDSCard("tie")); scn.BoardAsPilot(scn.GetDSCard("tie"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(6); scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(destiny - 1);
        scn.LSInitiateBattle(system); scn.PassAllResponses(); return scn;
    }

    private void attrition(int destiny, boolean immune) {
        var scn = battle(destiny); var falcon = scn.GetLSCard("falcon");
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); assertEquals(destiny, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) scn.LSChooseCards(); else scn.LSPayAttritionFromCardInPlay(falcon);
        assertInZone(immune ? Zone.AT_LOCATION : Zone.LOST_PILE, falcon);
    }
    @Test public void HanChewieAndTheFalconIgnoresAttritionFour() { attrition(4, true); }
    @Test public void HanChewieAndTheFalconMustSatisfyAttritionFive() { attrition(5, false); }

    @Test
    public void HanChewieAndTheFalconDoesNotAddUnprintedDestinyToPowerOrBattleDestiny() {
        //test1: permanent pilots permit one ordinary battle destiny, with no additional power draw.
        var scn = battle(1); scn.SkipToEndOfPowerSegment(true);
        assertEquals(0, scn.GetLSTotalDestiny()); //test1
        assertEquals(8, scn.GetLSTotalPower());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(6));
    }

    @Test
    public void HanChewieAndTheFalconDoesNotRequireMaintenanceAtEndOfTurn() {
        var scn = start(); scn.SkipToLSTurn(Phase.DRAW);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSForcePile().toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
        scn.SkipToDSTurn(Phase.ACTIVATE); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("falcon"));
        assertEquals(5, scn.GetBattleDestinyAbility(scn.GetLSCard("falcon")));
    }
}
