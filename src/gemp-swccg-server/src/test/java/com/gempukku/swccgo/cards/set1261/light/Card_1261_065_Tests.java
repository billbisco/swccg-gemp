package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1261_065_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("boshek", "1261_65"); put("falcon", "1_143"); put("red5", "2_71");
            put("home", "9_74"); put("pilot", "1_27"); put("luke", "1_19");
            put("launch", "9_36"); put("kessel", "1_126");
        }}, new HashMap<>() {{
            put("tie", "1_304"); put("infiltrator", "12_182"); put("maul", "11_54");
        }}, 40, 40, StartingSetup.LSStartingLocation("1_127"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BoShekBrashSmugglerStatsMatchPrintedSource() {
        var scn = scenario(); var b = scn.GetLSCard("boshek").getBlueprint();
        assertEquals("BoShek, Brash Smuggler", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(2, b.getPower(), scn.epsilon); assertEquals(4, b.getAbility(), scn.epsilon);
        assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.ALIEN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.ALIEN, Icon.PILOT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.SMUGGLER)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(Arrays.asList(Persona.BOSHEK)));
    }

    private void start(VirtualTableScenario scn) {
        scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("kessel"));
    }

    private void pilot(VirtualTableScenario scn, String ship, com.gempukku.swccgo.game.PhysicalCardImpl system) {
        scn.MoveCardsToLocation(system, scn.GetLSCard(ship));
        scn.BoardAsPilot(scn.GetLSCard(ship), scn.GetLSCard("boshek"));
    }

    @Test
    public void BoShekBrashSmugglerAddsThreePowerToFreighter() {
        var scn = scenario(); start(scn); pilot(scn, "falcon", scn.GetLSStartingLocation());
        assertEquals(6, scn.GetPower(scn.GetLSCard("falcon")));
    }

    @Test
    public void BoShekBrashSmugglerAddsThreePowerToNonFreighterToo() {
        var scn = scenario(); start(scn); pilot(scn, "red5", scn.GetLSStartingLocation());
        assertEquals(6, scn.GetPower(scn.GetLSCard("red5")));
    }

    @Test
    public void BoShekBrashSmugglerPassengerDoesNotAddPilotPower() {
        var scn = scenario(); start(scn);
        var ship = scn.GetLSCard("falcon"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("pilot"));
        scn.BoardAsPassenger(ship, scn.GetLSCard("boshek"));
        assertEquals(5, scn.GetPower(ship));
    }

    private void attrition(String ship, boolean battleground, int expected) {
        var scn = scenario(); start(scn);
        var system = battleground ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        pilot(scn, ship, system); scn.MoveCardsToLocation(system, scn.GetDSCard("tie"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(0);
        scn.LSInitiateBattle(system); scn.PassAllResponses(); scn.SkipToDamageSegment(true);
        assertEquals(expected, scn.GetUnpaidDSAttrition());
    }

    @Test public void BoShekBrashSmugglerAddsAttritionAtBattlegroundInFreighter() { attrition("falcon", true, 1); }
    @Test public void BoShekBrashSmugglerAddsNoAttritionInOtherShip() { attrition("red5", true, 0); }
    @Test public void BoShekBrashSmugglerAddsNoAttritionAtNonBattleground() { attrition("falcon", false, 0); }

    @Test
    public void BoShekBrashSmugglerAttritionDoesNotReachAnotherSystem() {
        var scn = scenario(); start(scn); pilot(scn, "falcon", scn.GetLSCard("kessel"));
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetLSCard("red5"), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("red5"), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(0);
        scn.LSInitiateBattle(system); scn.PassAllResponses(); scn.SkipToDamageSegment(true);
        assertEquals(0, scn.GetUnpaidDSAttrition());
    }

    private VirtualTableScenario cloak(String ship, boolean battleground, boolean sameSystem) {
        var scn = scenario(); start(scn);
        var boshekSystem = battleground ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        pilot(scn, ship, boshekSystem);
        var targetSystem = sameSystem ? boshekSystem : scn.GetLSCard("kessel");
        scn.MoveCardsToLocation(targetSystem, scn.GetDSCard("infiltrator"));
        scn.BoardAsPilot(scn.GetDSCard("infiltrator"), scn.GetDSCard("maul"));
        scn.SkipToDSTurn(Phase.MOVE);
        return scn;
    }

    @Test
    public void BoShekBrashSmugglerPreventsOpponentsCloakHere() {
        var scn = cloak("falcon", true, true);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("infiltrator"), "Cloak"));
    }

    @Test
    public void BoShekBrashSmugglerAllowsCloakAtAnotherSystem() {
        var scn = cloak("falcon", true, false);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("infiltrator"), "Cloak"));
    }

    @Test
    public void BoShekBrashSmugglerAllowsCloakWithoutFreighter() {
        var scn = cloak("red5", true, true);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("infiltrator"), "Cloak"));
    }

    @Test
    public void BoShekBrashSmugglerAllowsCloakAtNonBattleground() {
        var scn = cloak("falcon", false, true);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("infiltrator"), "Cloak"));
    }

    private void forceDrain(boolean capped, boolean sameSystem, boolean launch, int expected) {
        var scn = scenario(); start(scn); var kessel = scn.GetLSCard("kessel");
        scn.MoveCardsToLocation(kessel, scn.GetLSCard("home"));
        scn.BoardAsPilot(scn.GetLSCard("home"), scn.GetLSCard("pilot"));
        if (launch) scn.AttachCardsTo(kessel, scn.GetLSCard("launch"));
        if (capped) {
            if (sameSystem) scn.BoardAsPassenger(scn.GetLSCard("home"), scn.GetLSCard("boshek"));
            else pilot(scn, "falcon", scn.GetLSStartingLocation());
        }
        scn.SkipToLSTurn(Phase.CONTROL);
        int before = scn.GetDSLostPileCount();
        scn.LSForceDrainAt(kessel); scn.PassForceDrainStartResponses();
        assertTrue(scn.AwaitingDSForceLossPayment()); scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses(); assertEquals(expected, scn.GetDSLostPileCount() - before);
    }

    @Test public void BoShekBrashSmugglerCapsFourForceDrainToThreeEvenAsPassenger() { forceDrain(true, true, true, 3); }
    @Test public void BoShekBrashSmugglerLeavesThreeForceDrainAtThree() { forceDrain(true, true, false, 3); }
    @Test public void BoShekBrashSmugglerFourForceDrainControlWithoutBoShek() { forceDrain(false, false, true, 4); }
    @Test public void BoShekBrashSmugglerDoesNotCapDrainAtAnotherSystem() { forceDrain(true, false, true, 4); }
}
