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
            put("tibrin", "6_87"); put("plo", "12_21");
        }}, new HashMap<>() {{ put("vader", "1_168"); }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
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

    /** Places Yoda, then reports whether Depa ("Deploys only to Jedi Council Chamber") may deploy to Battle Plains. */
    private boolean depaMayDeployToBattlePlains(boolean yodaAtChamber, boolean darkJediAtChamber) {
        var s = scenario(); s.StartGame(); var yoda = s.GetLSCard("yoda"); var depa = s.GetLSCard("depa");
        var chamber = s.GetLSCard("chamber"); var naboo = s.GetLSCard("naboo");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(naboo);
        assertTrue(com.gempukku.swccgo.filters.Filters.battleground.accepts(s.game(), naboo));
        s.MoveCardsToLocation(yodaAtChamber ? chamber : s.GetLSStartingLocation(), yoda);
        if (darkJediAtChamber) s.MoveCardsToLocation(chamber, s.GetDSCard("vader"));
        s.MoveCardsToHand(depa); s.LSActivateForceCheat(9); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.LSCardPlayAvailable(depa));
        s.LSDeployCard(depa);
        boolean naboOk = s.LSHasCardChoiceAvailable(naboo);
        assertTrue(s.LSHasCardChoiceAvailable(chamber));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetLSStartingLocation()));
        if (naboOk) {
            s.LSChooseCard(naboo); s.PassAllResponses();
            assertEquals(naboo, depa.getAtLocation());
        }
        return naboOk;
    }

    @Test public void OtherJediMayActuallyDeployToEpisodeOneBattlegroundAsIfJediCouncilChamber() {
        assertTrue(depaMayDeployToBattlePlains(true, false));
    }

    @Test public void SubstitutionNeedsYodaAtJediCouncilChamber() {
        assertFalse(depaMayDeployToBattlePlains(false, false));
    }

    @Test public void SubstitutionStopsWhileADarkJediIsAtJediCouncilChamber() {
        assertFalse(depaMayDeployToBattlePlains(true, true));
    }

    @Test public void JediAtEpisodeOneBattlegroundUseTheirJediCouncilChamberImmunityOnlyWhileYodaAllows() {
        var s = scenario(); s.StartGame(); var yoda = s.GetLSCard("yoda"); var plo = s.GetLSCard("plo");
        var chamber = s.GetLSCard("chamber"); var naboo = s.GetLSCard("naboo");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(naboo);
        var modifiers = s.game().getModifiersQuerying();
        s.MoveCardsToLocation(naboo, plo);
        s.MoveCardsToLocation(s.GetLSStartingLocation(), yoda);
        assertFalse(modifiers.hasAnyImmunityToAttrition(s.gameState(), plo));
        s.MoveCardsToLocation(chamber, yoda);
        assertTrue(modifiers.hasAnyImmunityToAttrition(s.gameState(), plo));
        // A non-Episode I site (Cloud City) is not substituted
        s.MoveCardsToLocation(s.GetLSStartingLocation(), plo);
        assertFalse(modifiers.hasAnyImmunityToAttrition(s.gameState(), plo));
        s.MoveCardsToLocation(naboo, plo);
        s.MoveCardsToLocation(chamber, s.GetDSCard("vader"));
        assertFalse(modifiers.hasAnyImmunityToAttrition(s.gameState(), plo));
        // Yoda keeps his own Chamber immunity
        assertTrue(modifiers.hasAnyImmunityToAttrition(s.gameState(), yoda));
    }
}
