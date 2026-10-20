package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("redeemed", "1281_33");
                    put("jawa", "1_12");
                    put("nabrun", "1_97");
                }},
                new HashMap<>() {{
                    put("bast", "1_165");
                    put("elis", "1_243");
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

    protected VirtualTableScenario GetHuntDownScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("redeemed", "1281_33");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("hunt", "1240_58"); // Hunt Down And Destroy The Jedi (V), a [Virtual Block 4] objective
                    put("vader", "108_006"); // permanent weapon: Vader's Lightsaber
                    put("sniper", "2_139");
                    put("corridor", "1_284"); // Death Star: Detention Block Corridor (not a battleground)
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
    public void RedeemedApprenticeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("redeemed").getBlueprint();

        assertEquals("Redeemed Apprentice", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedeemedApprenticeDeploysAfterYouForfeitACharacter() {
        var scn = GetScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var jawa = scn.GetLSCard("jawa");
        var bast = scn.GetDSCard("bast");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, bast);
        scn.MoveCardsToLSHand(redeemed);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        scn.LSChooseCard(jawa);
        scn.DSPass();

        assertTrue(scn.GetLSAvailableActions().toString().contains("Deploy due to"));
        scn.LSChooseAction("Deploy due to");
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, redeemed);
    }

    @Test
    public void RedeemedApprenticeCancelsElisHelrotAndNabrunLeids() {
        var scn = GetScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var nabrun = scn.GetLSCard("nabrun");
        var elis = scn.GetDSCard("elis");
        var site = scn.GetLSStartingLocation();

        var jawa = scn.GetLSCard("jawa");
        var bast = scn.GetDSCard("bast");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa);
        scn.MoveCardsToLocation(dsSite, bast);
        scn.MoveCardsToLSHand(nabrun);
        scn.MoveCardsToDSHand(elis);
        scn.LSActivateForceCheat(8);
        scn.DSActivateForceCheat(8);

        // Without Redeemed Apprentice on table, both Interrupts are playable.
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(nabrun));
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.DSCardPlayAvailable(elis));

        scn.MoveCardsToLSSideOfTable(redeemed);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardPlayAvailable(nabrun));
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSCardPlayAvailable(elis));
    }

    /**
     * During DS's control phase, Vader (via Sniper) hits the target with Vader's Lightsaber. A character hit outside
     * a battle is lost at once, so Hunt Down And Destroy The Jedi (V) makes LS lose Force: LS must choose exactly
     * expectedLoss cards and its Life Force drops by that much.
     */
    private void assertLsLosesWhenVaderHitsAndTargetLeavesTable(boolean redeemedOnTable, String targetKey, int expectedLoss) {
        var scn = GetHuntDownScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var target = targetKey == null ? scn.GetLSFiller(1) : scn.GetLSCard(targetKey);
        var hunt = scn.GetDSCard("hunt");
        var vader = scn.GetDSCard("vader");
        var sniper = scn.GetDSCard("sniper");
        var site = scn.GetDSCard("corridor");
        scn.MoveOutOfPlay(hunt);

        scn.StartGame();
        // Not a battleground site, so the objective stays on its front side (no flip mid-test).
        scn.MoveLocationToTable(site);
        scn.MoveCardsToDSSideOfTable(hunt);
        hunt.setObjectiveDeploymentComplete(true);
        if (redeemedOnTable) {
            scn.MoveCardsToLSSideOfTable(redeemed);
        }
        // A second Light Side character, so the Sniper target is a real choice.
        scn.MoveCardsToLocation(site, vader, target, scn.GetLSFiller(2));
        scn.MoveCardsToDSHand(sniper);
        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(7);

        int lifeForceBefore = scn.GetLSLifeForceRemaining();
        scn.DSPlayCard(sniper);
        scn.DSChooseCard(vader);
        scn.LSPass(); // Playing Sniper - Optional responses
        scn.DSPass();
        scn.DSChooseCard(target);
        scn.PassAllResponses();
        // Two required responses to the hit: the objective's 'hit character leaves table' trigger and the rule that
        // loses a character hit outside battle (no action text). DS resolves the objective's trigger first.
        var requiredResponses = scn.GetDSAvailableActions();
        assertEquals(2, requiredResponses.size());
        assertTrue(requiredResponses.contains(null));
        scn.DSDecided(String.valueOf(requiredResponses.indexOf(null) == 0 ? 1 : 0));
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, target);
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());
        for (int i = 0; i < expectedLoss; ++i) {
            assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            scn.PassAllResponses();
        }
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
        assertEquals(expectedLoss, lifeForceBefore - scn.GetLSLifeForceRemaining());
    }

    @Test
    public void HuntDownVaderLightsaberHitNonJediLeavesTableLosesTwoWithoutRedeemedApprentice() {
        assertLsLosesWhenVaderHitsAndTargetLeavesTable(false, null, 2);
    }

    @Test
    public void RedeemedApprenticeReducesHuntDownLossForYourNonJediCharacterBy1() {
        assertLsLosesWhenVaderHitsAndTargetLeavesTable(true, null, 1);
    }

    @Test
    public void RedeemedApprenticeDoesNotReduceHuntDownLossForYourJedi() {
        assertLsLosesWhenVaderHitsAndTargetLeavesTable(true, "obiwan", 2);
    }

    @Test
    public void RedeemedApprenticeDeploysAfterOpponentDrawsDestinyForElisHelrot() {
        var scn = GetScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var elis = scn.GetDSCard("elis");
        var bast = scn.GetDSCard("bast");
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSite, bast);
        scn.MoveCardsToLSHand(redeemed);
        scn.MoveCardsToDSHand(elis);
        scn.DSActivateForceCheat(8);
        scn.SkipToDSTurn(Phase.MOVE);
        scn.PrepareDSDestiny(2);

        assertTrue(scn.DSCardPlayAvailable(elis));
        scn.DSPlayCard(elis);
        scn.DSChooseCard(dsSite);
        scn.DSChooseCard(lsSite);
        scn.DSChooseCard(bast);
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        assertTrue(scn.LSDecisionAvailable("for destiny"));
        assertTrue(scn.GetLSAvailableActions().contains("Deploy due to Redeemed Apprentice"));
        scn.LSChooseAction("Deploy due to");
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, redeemed);
    }
}
