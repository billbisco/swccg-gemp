package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_013_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("tantive", "2_73"); }},
                new HashMap<>() {{ put("tarkin", "1261_13"); put("officer", "1_166"); put("alien", "4_100");
                    put("devastator", "1_301"); put("scout", "3_156"); put("superlaser", "2_161"); put("mark2", "9_182");
                    put("deathstar", "2_143"); put("bay", "1_285"); put("eriadu", "102_7"); put("hoth", "3_143"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void MoffTarkinCommandantPrintedMetadataMatchesSource() {
        var scn = scenario(); var b = scn.GetDSCard("tarkin").getBlueprint();
        assertEquals("Moff Tarkin, Death Star Commandant", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.LEADER); add(Keyword.MOFF); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintPersonaCheck(b, new ArrayList<>() {{ add(Persona.TARKIN); }});
        assertEquals("Leader.", b.getLore());
    }

    @Test public void MoffTarkinCommandantAddsOneToPilotedVehicleAndShipNotPassenger() {
        var scn = scenario(); scn.StartGame(); var tarkin = scn.GetDSCard("tarkin"); var scout = scn.GetDSCard("scout");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scout); scn.BoardAsPilot(scout, tarkin); assertEquals(4, scn.GetPower(scout));
        scn.MoveCardsToDSHand(tarkin); scn.BoardAsPassenger(scout, tarkin); assertEquals(3, scn.GetPower(scout));
        scn.MoveCardsToDSHand(tarkin); scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveCardsToLocation(scn.GetDSCard("hoth"), scn.GetDSCard("devastator")); scn.BoardAsPilot(scn.GetDSCard("devastator"), tarkin);
        assertEquals(10, scn.GetPower(scn.GetDSCard("devastator")));
    }

    private void deploy(boolean deathStar) {
        var scn = scenario(); scn.StartGame(); var tarkin = scn.GetDSCard("tarkin");
        if (deathStar) scn.MoveLocationToTable(scn.GetDSCard("bay"));
        var target = deathStar ? scn.GetDSCard("bay") : scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(tarkin); scn.SkipToDSTurn(Phase.DEPLOY); int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(tarkin); if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target); scn.PassAllResponses();
        assertEquals(target, tarkin.getAtLocation()); assertEquals(force - (deathStar ? 2 : 4), scn.GetDSForcePileCount());
    }

    @Test public void MoffTarkinCommandantActuallyDeploysForTwoAtDeathStar() { deploy(true); }
    @Test public void MoffTarkinCommandantStillPaysFourAwayFromDeathStar() { deploy(false); }

    @Test public void MoffTarkinCommandantAddsExactlyTwoOwnGenerationOnlyAtEriaduAndRevokesOnLeaving() {
        var scn = scenario(); scn.StartGame(); var eriadu = scn.GetDSCard("eriadu"); var hoth = scn.GetDSCard("hoth");
        scn.MoveLocationToTable(eriadu); scn.MoveLocationToTable(hoth); var q = scn.game().getModifiersQuerying();
        float before = q.getForceGenerationFromLocation(scn.gameState(), eriadu, scn.DS);
        float light = q.getForceGenerationFromLocation(scn.gameState(), eriadu, scn.LS);
        float other = q.getForceGenerationFromLocation(scn.gameState(), hoth, scn.DS);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tarkin"));
        assertEquals(before + 2, q.getForceGenerationFromLocation(scn.gameState(), eriadu, scn.DS), scn.epsilon);
        assertEquals(light, q.getForceGenerationFromLocation(scn.gameState(), eriadu, scn.LS), scn.epsilon);
        assertEquals(other, q.getForceGenerationFromLocation(scn.gameState(), hoth, scn.DS), scn.epsilon);
        scn.MoveCardsToDSHand(scn.GetDSCard("tarkin"));
        assertEquals(before, q.getForceGenerationFromLocation(scn.gameState(), eriadu, scn.DS), scn.epsilon);
    }

    private VirtualTableScenario battleCost(String participant, int force) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("tarkin"));
        scn.MoveCardsToLocation(site, scn.GetDSCard(participant), scn.GetLSCard("obiwan"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(force); scn.DrainDSForcePileTo(force); scn.SkipToPhase(Phase.BATTLE);
        assertEquals(force, scn.GetDSForcePileCount()); return scn;
    }

    @Test public void MoffTarkinCommandantActuallyInitiatesRemoteImperialBattleForZeroForce() {
        var scn = battleCost("officer", 0); var site = scn.GetDSStartingLocation();
        assertTrue(scn.DSCanInitiateBattle(site)); scn.DSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount()); assertTrue(scn.gameState().isDuringBattle());
    }

    @Test public void MoffTarkinCommandantAlienOnlyBattleStillCostsOneForce() {
        var scn = battleCost("alien", 1); scn.DSInitiateBattle(scn.GetDSStartingLocation()); scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount()); assertTrue(scn.gameState().isDuringBattle());
        var none = battleCost("alien", 0); assertFalse(none.DSCanInitiateBattle(none.GetDSStartingLocation()));
    }

    private VirtualTableScenario superlaser(boolean tarkin) {
        var scn = scenario(); scn.StartGame(); var system = scn.GetDSCard("deathstar"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetDSCard("devastator"), scn.GetLSCard("tantive"));
        scn.AttachCardsTo(system, scn.GetDSCard("superlaser"));
        if (tarkin) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tarkin"));
        scn.DSStartBattleAndSkipToWeaponsSegment(system); return scn;
    }

    private void fire(VirtualTableScenario scn, int first, int second) {
        scn.PrepareDSDestiny(second); scn.PrepareDSDestiny(first); int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("superlaser"), "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("tantive"))) scn.DSChooseCard(scn.GetLSCard("tantive"));
        scn.PassAllResponses(); assertEquals(reserve - 2, scn.GetDSReserveDeckCount());
    }

    @Test public void MoffTarkinCommandantActualSuperlaserFireIsFreeAndHitReducesForfeitByFour() {
        var scn = superlaser(true); int force = scn.GetDSForcePileCount(); int forfeit = scn.GetForfeit(scn.GetLSCard("tantive"));
        fire(scn, 3, 3); assertTrue(scn.GetLSCard("tantive").isHit());
        assertEquals(forfeit - 4, scn.GetForfeit(scn.GetLSCard("tantive"))); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test public void MoffTarkinCommandantSuperlaserEqualDefenseMissesWithoutForfeitReduction() {
        var scn = superlaser(true); assertEquals(4, scn.GetDefense(scn.GetLSCard("tantive")));
        int forfeit = scn.GetForfeit(scn.GetLSCard("tantive")); fire(scn, 2, 2);
        assertFalse(scn.GetLSCard("tantive").isHit()); assertEquals(forfeit, scn.GetForfeit(scn.GetLSCard("tantive")));
    }

    @Test public void SuperlaserWithoutMoffTarkinStillCostsFourAndLeavesHitForfeitUnchanged() {
        var scn = superlaser(false); int force = scn.GetDSForcePileCount(); int forfeit = scn.GetForfeit(scn.GetLSCard("tantive"));
        fire(scn, 3, 3); assertTrue(scn.GetLSCard("tantive").isHit());
        assertEquals(force - 4, scn.GetDSForcePileCount()); assertEquals(forfeit, scn.GetForfeit(scn.GetLSCard("tantive")));
    }

    @Test public void MoffTarkinCommandantDoesNotMakeSuperlaserMarkTwoFree() {
        var scn = superlaser(true);
        assertEquals(3, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), scn.GetDSCard("mark2"), null,
                scn.GetLSCard("tantive"), 3), scn.epsilon);
    }

    private void attrition(int destiny, boolean immune) {
        var scn = scenario(); scn.StartGame(); var tarkin = scn.GetDSCard("tarkin"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, tarkin, scn.GetLSCard("obiwan")); scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(destiny); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(immune ? "0" : "1", scn.GetADParam(scn.DS, "min")[0]);
        if (immune) { scn.DSPass(); scn.PassAllResponses(); assertEquals(Zone.AT_LOCATION, tarkin.getZone()); }
        else { scn.DSPayAttritionFromCardInPlay(tarkin); scn.PassAllResponses();
            com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, tarkin); }
    }

    @Test public void MoffTarkinCommandantSurvivesTwoAttritionButMustSatisfyThree() { attrition(2, true); attrition(3, false); }
}
