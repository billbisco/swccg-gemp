package com.gempukku.swccgo.cards.set1200.dark;

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

public class Card_1200_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("kal", "1_15");
                    put("biggs", "1_3");
                }},
                new HashMap<>() {{
                    put("detention", "1200_3");
                    put("wookiee", "3_127");
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
    public void ImperialDetentionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("detention").getBlueprint();

        assertEquals("Imperial Detention", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialDetentionIdJustAsSoonKissAWookieeMayNotBePlayed() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var wookiee = scn.GetDSCard("wookiee");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detention);
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(wookiee);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        scn.PassAllResponses();

        assertFalse(scn.DSPlayLostInterruptAvailable(wookiee));
        assertAtLocation(site, luke);
    }

    @Test
    public void ImperialDetentionRequiresExtraForceToDeployAPrintedZeroCostCharacter() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var kal = scn.GetLSCard("kal");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detention);
        scn.MoveCardsToLSHand(kal);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(1);
        assertTrue(scn.LSDeployAvailable(kal));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(kal);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, kal);
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }

    /**
     * Puts Imperial Detention on table (or not) and Biggs Darklighter (printed deploy cost 2, ability 2) in Light's hand.
     */
    private void setUpLightDeployOfBiggs(VirtualTableScenario scn, boolean detentionOnTable) {
        var detention = scn.GetDSCard("detention");
        var biggs = scn.GetLSCard("biggs");

        scn.StartGame();
        if (detentionOnTable) {
            scn.MoveCardsToDSSideOfTable(detention);
        }
        scn.MoveCardsToLSHand(biggs);
        assertEquals(2, biggs.getBlueprint().getDeployCost(), scn.epsilon);
    }

    /**
     * Light deploys Biggs to the Light starting site, asserting that the given amount of Force is used in total.
     */
    private void lightDeploysBiggsUsing(VirtualTableScenario scn, int forceUsed) {
        var biggs = scn.GetLSCard("biggs");
        var site = scn.GetLSStartingLocation();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(biggs));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(biggs);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(site);
        if (forceUsed > 0) {
            assertTrue(scn.DSDecisionAvailable("Use " + forceUsed + " Force - Optional responses"));
        }
        scn.PassAllResponses();
        assertAtLocation(site, biggs);
        assertEquals(forceBefore - forceUsed, scn.GetLSForcePileCount());
    }

    @Test
    public void ImperialDetentionRequiresOneForceWhenCharacterDeployCostIsReducedToZero() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var biggs = scn.GetLSCard("biggs");

        setUpLightDeployOfBiggs(scn, true);
        scn.ApplyAdHocModifier(new DeployCostModifier(detention, Filters.sameCardId(biggs), -2));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(biggs));

        // Deploying for 0 Force: must first use 1 Force
        lightDeploysBiggsUsing(scn, 1);
    }

    @Test
    public void WithoutImperialDetentionCharacterDeployCostReducedToZeroCostsNothing() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");
        var site = scn.GetLSStartingLocation();

        setUpLightDeployOfBiggs(scn, false);
        // The modifier's source must be in play, so the site stands in for Imperial Detention here
        scn.ApplyAdHocModifier(new DeployCostModifier(site, Filters.sameCardId(biggs), -2));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(biggs));

        lightDeploysBiggsUsing(scn, 0);
    }

    @Test
    public void ImperialDetentionDoesNotAddForceWhenCharacterDeploysForItsNormalCost() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");

        setUpLightDeployOfBiggs(scn, true);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(2, scn.GetDeployCost(biggs));

        // Printed cost 2, not reduced and not free: just the normal 2 Force
        lightDeploysBiggsUsing(scn, 2);
    }

    @Test
    public void ImperialDetentionChargesOnlyHalfPrintedCostWhenCharacterDeploysForFree() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var biggs = scn.GetLSCard("biggs");

        setUpLightDeployOfBiggs(scn, true);
        scn.ApplyAdHocModifier(new DeploysFreeModifier(detention, Filters.sameCardId(biggs)));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(biggs));

        // Deploying for free: half of printed cost 2 (rounded up) = 1, and the "for 0 Force" clause does not stack
        lightDeploysBiggsUsing(scn, 1);
    }
}
