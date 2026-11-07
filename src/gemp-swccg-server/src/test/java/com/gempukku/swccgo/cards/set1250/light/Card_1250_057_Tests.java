package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_057_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("yoda", "1250_57"); put("depa", "12_4"); put("chamber", "12_76"); put("naboo", "12_79");
            put("tibrin", "6_87");
        }}, new HashMap<>(), 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalCouncilYodaHasPrintedMetadata() {
        var s = scenario(); var bp = s.GetLSCard("yoda").getBlueprint();
        assertEquals("Yoda, Senior Council Member", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.hasPersona(Persona.YODA)); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(1, bp.getDestiny(), s.epsilon); assertEquals(4, bp.getDeployCost(), s.epsilon);
        assertEquals(3, bp.getPower(), s.epsilon); assertEquals(7, bp.getAbility(), s.epsilon);
        assertEquals(7, bp.getForfeit(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.JEDI_MASTER)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.CORUSCANT, Icon.EPISODE_I, Icon.JEDI_MASTER, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.JEDI_COUNCIL_MEMBER)));
    }

    @Test
    public void ActualYodaDeployCanChooseEpisodeOneSitesButNotOtherSitesOrSystems() {
        var s = scenario(); s.StartGame(); var yoda = s.GetLSCard("yoda");
        var chamber = s.GetLSCard("chamber"); var naboo = s.GetLSCard("naboo"); var system = s.GetLSCard("tibrin");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(naboo); s.MoveLocationToTable(system);
        s.MoveCardsToHand(yoda); s.LSActivateForceCheat(7); s.SkipToLSTurn(Phase.DEPLOY);
        int force = s.GetLSForcePileCount(); s.LSDeployCard(yoda);
        assertTrue(s.LSHasCardChoicesAvailable(chamber, naboo));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetLSStartingLocation()));
        assertFalse(s.LSHasCardChoiceAvailable(system));
        s.LSChooseCard(naboo); s.PassAllResponses();
        assertEquals(naboo, yoda.getAtLocation()); assertEquals(force - 4, s.GetLSForcePileCount());
        assertFalse(s.game().getModifiersQuerying().hasAnyImmunityToAttrition(s.gameState(), yoda));
    }

    private void paidDepa(boolean yodaAtChamber, int expected) {
        var s = scenario(); s.StartGame(); var yoda = s.GetLSCard("yoda"); var depa = s.GetLSCard("depa");
        var chamber = s.GetLSCard("chamber"); var naboo = s.GetLSCard("naboo");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(naboo);
        s.MoveCardsToLocation(yodaAtChamber ? chamber : naboo, yoda); s.MoveCardsToHand(depa);
        s.LSActivateForceCheat(9); s.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(yodaAtChamber, s.game().getModifiersQuerying().hasAnyImmunityToAttrition(s.gameState(), yoda));
        int force = s.GetLSForcePileCount(); s.LSDeployCard(depa);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(chamber);
        s.PassAllResponses();
        assertEquals(chamber, depa.getAtLocation()); assertEquals(force - expected, s.GetLSForcePileCount());
    }

    @Test public void YodaAtChamberHasImmunityAndOtherJediActuallyPaysTwoLess() { paidDepa(true, 2); }
    @Test public void YodaAwayFromChamberHasNeitherBenefit() { paidDepa(false, 4); }
}
