package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("professor", "1200_10");
                    put("wait", "3_44");
                    put("alter", "1_71");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("praji", "1_167");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TheProfessorVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("professor").getBlueprint();

        assertEquals("The Professor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheProfessorVItCanWaitMayNotBePlayed() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var wait = scn.GetLSCard("wait");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(professor);
        scn.MoveCardsToLSHand(wait);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(trooper));
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertFalse(scn.LSCardPlayAvailable(wait));
    }

    @Test
    public void TheProfessorVMayNotBeCanceled() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var alter = scn.GetLSCard("alter");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(professor);
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(alter));
    }

    /**
     * Puts The Professor (V) on table (or not) and Commander Praji (printed deploy cost 2, ability 2) in Dark's hand.
     */
    private void setUpDarkDeployOfPraji(VirtualTableScenario scn, boolean professorOnTable) {
        var professor = scn.GetLSCard("professor");
        var praji = scn.GetDSCard("praji");

        scn.StartGame();
        if (professorOnTable) {
            scn.MoveCardsToLSSideOfTable(professor);
        }
        scn.MoveCardsToDSHand(praji);
        assertEquals(2, praji.getBlueprint().getDeployCost(), scn.epsilon);
    }

    /**
     * Dark deploys Praji to the Dark starting site, asserting that the given amount of Force is used in total.
     */
    private void darkDeploysPrajiUsing(VirtualTableScenario scn, int forceUsed) {
        var praji = scn.GetDSCard("praji");
        var site = scn.GetDSStartingLocation();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(praji));
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSDeployCard(praji);
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        scn.DSChooseCard(site);
        if (forceUsed > 0) {
            assertTrue(scn.LSDecisionAvailable("Use " + forceUsed + " Force - Optional responses"));
        }
        scn.PassAllResponses();
        assertAtLocation(site, praji);
        assertEquals(forceBefore - forceUsed, scn.GetDSForcePileCount());
    }

    @Test
    public void TheProfessorVRequiresOneForceWhenCharacterDeployCostIsReducedToZero() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var praji = scn.GetDSCard("praji");

        setUpDarkDeployOfPraji(scn, true);
        scn.ApplyAdHocModifier(new DeployCostModifier(professor, Filters.sameCardId(praji), -2));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(praji));

        // Deploying for 0 Force: must first use 1 Force
        darkDeploysPrajiUsing(scn, 1);
    }

    @Test
    public void WithoutTheProfessorVCharacterDeployCostReducedToZeroCostsNothing() {
        var scn = GetScenario();
        var praji = scn.GetDSCard("praji");
        var site = scn.GetDSStartingLocation();

        setUpDarkDeployOfPraji(scn, false);
        // The modifier's source must be in play, so the site stands in for The Professor here
        scn.ApplyAdHocModifier(new DeployCostModifier(site, Filters.sameCardId(praji), -2));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(praji));

        darkDeploysPrajiUsing(scn, 0);
    }

    @Test
    public void TheProfessorVDoesNotAddForceWhenCharacterDeploysForItsNormalCost() {
        var scn = GetScenario();
        var praji = scn.GetDSCard("praji");

        setUpDarkDeployOfPraji(scn, true);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(2, scn.GetDeployCost(praji));

        // Printed cost 2, not reduced and not free: just the normal 2 Force
        darkDeploysPrajiUsing(scn, 2);
    }

    @Test
    public void TheProfessorVChargesOnlyHalfPrintedCostWhenCharacterDeploysForFree() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var praji = scn.GetDSCard("praji");

        setUpDarkDeployOfPraji(scn, true);
        scn.ApplyAdHocModifier(new DeploysFreeModifier(professor, Filters.sameCardId(praji)));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(praji));

        // Deploying for free: half of printed cost 2 (rounded up) = 1, and the "for 0 Force" clause does not stack
        darkDeploysPrajiUsing(scn, 1);
    }
}
