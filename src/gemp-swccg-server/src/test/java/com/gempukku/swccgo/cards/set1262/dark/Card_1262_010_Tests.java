package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1262_010_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("independence", "9_75"); put("outrider", "10_17"); put("red1", "1_144"); }},
                new HashMap<>() {{ put("trap", "1262_10"); put("devastator", "1_301"); }},
                10, 10, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ProgramTrapVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("trap").getBlueprint();
        assertEquals("Program Trap", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.A_NEW_HOPE); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void ProgramTrapVDeploysOnlyOnOpponentsCapitalOrIndependentStarship() {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var trap = scn.GetDSCard("trap");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("independence"), scn.GetLSCard("outrider"), scn.GetLSCard("red1"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("devastator"));
        scn.MoveCardsToHand(trap);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(2);
        assertTrue(scn.DSDeployAvailable(trap));
        scn.DSDeployCard(trap);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("independence")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("outrider")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("red1")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("devastator")));
        scn.DSChooseCard(scn.GetLSCard("outrider"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("outrider"), trap));
    }

    private VirtualTableScenario trapped(boolean attach) {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("independence"));
        if (attach) scn.AttachCardsTo(scn.GetLSCard("independence"), scn.GetDSCard("trap"));
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn;
    }

    @Test
    public void ProgramTrapVMakesStarshipPowerMinusTwo() {
        var without = trapped(false);
        assertEquals(8, without.GetPower(without.GetLSCard("independence")));
        var scn = trapped(true);
        assertEquals(6, scn.GetPower(scn.GetLSCard("independence")));
    }

    @Test
    public void ProgramTrapVCancelsStarshipGameText() {
        var without = trapped(false);
        assertFalse(without.GetLSCard("independence").isGameTextCanceled());
        var scn = trapped(true);
        assertTrue(scn.GetLSCard("independence").isGameTextCanceled());
    }

    @Test
    public void ProgramTrapVKeepsImmunityToAttrition() {
        var scn = trapped(true);
        var ship = scn.GetLSCard("independence");
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }

    @Test
    public void ProgramTrapVKeepsPermanentPilotAbilityAndCapacity() {
        var without = trapped(false);
        int ability = without.GetBattleDestinyAbility(without.GetLSCard("independence"));
        int passengers = without.GetPassengerCapacity(without.GetLSCard("independence"));
        var scn = trapped(true);
        var ship = scn.GetLSCard("independence");
        assertEquals(2, ability);
        assertEquals(ability, scn.GetBattleDestinyAbility(ship));
        assertEquals(passengers, scn.GetPassengerCapacity(ship));
    }
}
