package com.gempukku.swccgo.cards.set1102.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("goo", "7_62");
                }},
                new HashMap<>() {{
                    put("molator", "1102_42");
                    put("prophetess", "1_191");
                    put("stormtrooper", "1_194");
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
    public void MolatorStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("molator").getBlueprint();

        assertEquals("Molator", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MolatorDeploysOnTable() {
        var scn = GetScenario();
        var molator = scn.GetDSCard("molator");

        scn.StartGame();
        scn.MoveCardsToDSHand(molator);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(molator));
        scn.DSPlayCard(molator);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, molator);
    }

    @Test
    public void MolatorMakesCharactersOfAbilityLessThanFiveImmuneToGooNeeTay() {
        var scn = GetScenario();
        var molator = scn.GetDSCard("molator");
        var prophetess = scn.GetDSCard("prophetess");
        var goo = scn.GetLSCard("goo");

        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLSSideOfTable(goo);
        scn.MoveCardsToDSHand(prophetess);
        float withGoo = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), prophetess, prophetess, site, false, null, false, 0, null, false);
        scn.MoveCardsToDSSideOfTable(molator);
        float withMolator = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), prophetess, prophetess, site, false, null, false, 0, null, false);

        assertEquals(withGoo - 2, withMolator, scn.epsilon);
        scn.MoveCardsToLocation(site, prophetess);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), prophetess, Title.Goo_Nee_Tay));
    }

    @Test
    public void MolatorDelaysACharacterLostBeforeTheDamageSegmentUntilEndOfBattle() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("portal", "6_66");
                    put("bay", "1_129");
                }},
                new HashMap<>() {{
                    put("molator", "1102_42");
                    put("stormtrooper", "1_194");
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
        var molator = scn.GetDSCard("molator");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var luke = scn.GetLSCard("luke");
        var portal = scn.GetLSCard("portal");

        scn.StartGame();
        var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToDSSideOfTable(molator);
        scn.MoveCardsToLocation(bay, stormtrooper, luke);
        scn.MoveCardsToLSHand(portal);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(bay);

        boolean played = false;
        for (int i = 0; i < 16; i++) {
            if (scn.LSPlayUsedInterruptAvailable(portal) || scn.LSCardPlayAvailable(portal)) {
                if (scn.LSPlayUsedInterruptAvailable(portal)) {
                    scn.LSPlayUsedInterrupt(portal);
                } else {
                    scn.LSPlayCard(portal);
                }
                played = true;
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(played);
        if (scn.LSHasCardChoiceAvailable(stormtrooper)) {
            scn.LSChooseCard(stormtrooper);
        }
        scn.PrepareLSDestiny(7);
        scn.PassAllResponses();

        assertAtLocation(bay, stormtrooper);
        scn.SkipToDamageSegment(false);
        for (int i = 0; i < 24; i++) {
            if (stormtrooper.getZone() == Zone.LOST_PILE || stormtrooper.getZone() == Zone.TOP_OF_LOST_PILE) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(stormtrooper) || scn.LSHasCardChoiceAvailable(stormtrooper)) {
                if (scn.DSHasCardChoiceAvailable(stormtrooper)) {
                    scn.DSChooseCard(stormtrooper);
                } else {
                    scn.LSChooseCard(stormtrooper);
                }
                continue;
            }
            if (scn.AwaitingDSBattleDamagePayment() || scn.AwaitingDSAttritionPayment()) {
                if (scn.GetDSReserveDeckCount() >= 1 && scn.AwaitingDSBattleDamagePayment()) {
                    scn.DSPayRemainingBattleDamageFromReserveDeck();
                    continue;
                }
            }
            if (scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment()) {
                if (scn.GetLSReserveDeckCount() >= 1 && scn.AwaitingLSBattleDamagePayment()) {
                    scn.LSPayRemainingBattleDamageFromReserveDeck();
                    continue;
                }
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSChoiceAvailable("Pass")
                    || scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            scn.PassAllResponses();
            break;
        }
        assertInZone(Zone.LOST_PILE, stormtrooper);
    }
}
