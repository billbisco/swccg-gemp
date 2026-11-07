package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_014_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); put("walker", "3_69"); put("worrt", "6_48"); }},
                new HashMap<>() {{ put("pair", "1261_14"); put("officer", "1_166"); put("plains", "3_148"); put("mountains", "104_4"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void P13AndP14PrintedMetadataMatchesSource() {
        var scn = scenario(); var b = scn.GetDSCard("pair").getBlueprint();
        assertEquals("P-13 & P-14", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(5, b.getDeployCost(), scn.epsilon);
        assertEquals(5, b.getPower(), scn.epsilon); assertEquals(5, b.getArmor(), scn.epsilon); assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.DESTROYER); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.DROID); add(Icon.PERMANENT_WEAPON); add(Icon.PRESENCE); add(Icon.THEED_PALACE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_6); }});
    }

    private VirtualTableScenario battle() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("pair"), scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    private void fire(VirtualTableScenario scn, String target, int destiny) {
        scn.PrepareDSDestiny(destiny); scn.DSUseCardAction(scn.GetDSCard("pair"), "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard(target))) scn.DSChooseCard(scn.GetLSCard(target));
        scn.PassAllResponses();
    }

    @Test public void P13AndP14FreeSingleDestinyPlusOneHitsCharacterAndCostsOpponentExactlyOneForce() {
        var scn = battle(); int force = scn.GetDSForcePileCount(); int lost = scn.GetLSLostPileCount();
        assertEquals(6, scn.GetDefense(scn.GetLSCard("obiwan"))); fire(scn, "obiwan", 6);
        assertTrue(scn.GetLSCard("obiwan").isHit()); assertEquals(force, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingLSForceLossPayment()); scn.LSPayForceLossFromReserveDeck(); scn.PassAllResponses();
        assertFalse(scn.AwaitingLSForceLossPayment()); assertEquals(lost + 1, scn.GetLSLostPileCount());
    }

    @Test public void P13AndP14EqualityMissesWithoutOpponentForceLoss() {
        var scn = battle(); int lost = scn.GetLSLostPileCount(); fire(scn, "obiwan", 5);
        assertFalse(scn.GetLSCard("obiwan").isHit()); assertFalse(scn.AwaitingLSForceLossPayment());
        assertEquals(lost, scn.GetLSLostPileCount());
    }

    @Test public void P13AndP14SecondFiringLosesTwoForceAndCannotFireThirdTime() {
        var scn = battle(); fire(scn, "obiwan", 0); int force = scn.GetDSForcePileCount(); int lost = scn.GetDSLostPileCount();
        scn.PrepareDSDestiny(0); scn.DSUseCardAction(scn.GetDSCard("pair"), "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("han"))) scn.DSChooseCard(scn.GetLSCard("han")); scn.PassAllResponses();
        for (int i = 0; i < 2; i++) { assertTrue(scn.AwaitingDSForceLossPayment()); scn.DSPayForceLossFromReserveDeck(); scn.PassAllResponses(); }
        assertFalse(scn.AwaitingDSForceLossPayment()); assertEquals(lost + 2, scn.GetDSLostPileCount());
        assertEquals(force, scn.GetDSForcePileCount()); assertFalse(scn.GetLSCard("han").isHit());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("pair"), "Fire"));
    }

    @Test public void P13AndP14CanFireAtVehicleForFreeAndCauseOneForceLoss() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("pair"), scn.GetLSCard("obiwan"), scn.GetLSCard("walker"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); int force = scn.GetDSForcePileCount();
        fire(scn, "walker", 6); assertTrue(scn.GetLSCard("walker").isHit()); assertEquals(force, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingLSForceLossPayment()); scn.LSPayForceLossFromReserveDeck(); scn.PassAllResponses(); assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test public void P13AndP14CannotFireAtCreature() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("pair"), scn.GetLSCard("worrt")); scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("pair"), "Fire"));
    }

    private void attrition(int destiny, boolean immune) {
        var scn = battle(); var pair = scn.GetDSCard("pair"); scn.PrepareLSDestiny(destiny);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(immune ? "0" : "1", scn.GetADParam(scn.DS, "min")[0]);
        if (immune) { scn.DSPass(); scn.PassAllResponses(); assertEquals(Zone.AT_LOCATION, pair.getZone()); }
        else { scn.DSPayAttritionFromCardInPlay(pair); scn.PassAllResponses(); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, pair); }
    }
    @Test public void P13AndP14ImmuneToThreeAttritionButMustSatisfyFour() { attrition(3, true); attrition(4, false); }

    @Test public void P13AndP14PresenceDoesNotInventBattleDestinyIfUnable() {
        assertEquals(0, battle().GetDSBattleDestinyCount());
    }

    @Test public void P13AndP14ActuallyMoveAsPaidReactToAdjacentForceDrain() {
        var scn = scenario(); scn.StartGame(); var pair = scn.GetDSCard("pair"); var plains = scn.GetDSCard("plains"); var mountains = scn.GetDSCard("mountains");
        scn.MoveLocationToTable(plains); scn.MoveLocationToTable(mountains);
        scn.MoveCardsToLocation(plains, pair); scn.MoveCardsToLocation(mountains, scn.GetLSCard("obiwan"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.DSActivateForceCheat(1); int force = scn.GetDSForcePileCount();
        scn.LSPlayCard(mountains, "drain"); assertTrue(scn.DSCardActionAvailable(pair, "Move"));
        scn.DSUseCardAction(pair, "Move"); scn.PassAllResponses();
        assertEquals(mountains, pair.getAtLocation()); assertEquals(force - 1, scn.GetDSForcePileCount()); assertFalse(scn.AwaitingDSForceLossPayment());
    }

    @Test public void P13AndP14ReactRequiresNormalMovementCost() {
        var scn = scenario(); scn.StartGame(); var pair = scn.GetDSCard("pair"); var plains = scn.GetDSCard("plains"); var mountains = scn.GetDSCard("mountains");
        scn.MoveLocationToTable(plains); scn.MoveLocationToTable(mountains);
        scn.MoveCardsToLocation(plains, pair); scn.MoveCardsToLocation(mountains, scn.GetLSCard("obiwan"));
        scn.SkipToLSTurn(Phase.ACTIVATE); scn.DrainDSForcePileTo(0); scn.SkipToPhase(Phase.CONTROL);
        scn.LSPlayCard(mountains, "drain"); assertFalse(scn.DSCardActionAvailable(pair, "Move"));
    }
}
