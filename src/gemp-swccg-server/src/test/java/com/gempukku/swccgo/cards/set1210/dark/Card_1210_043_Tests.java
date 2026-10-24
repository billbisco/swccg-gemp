package com.gempukku.swccgo.cards.set1210.dark;

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
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("detainment", "1210_43");
                    put("prisoner", "2_142");
                    put("besieged", "1210_32");
                    put("launch", "4_165");
                    put("scout", "1_305");
                    put("vader", "1_168");
                    put("devastator", "1_301");
                }},
                20, 20,
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
    public void ImperialDetainmentStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("detainment").getBlueprint();

        assertEquals("Imperial Detainment", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialDetainmentDeploysOnTable() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");

        scn.StartGame();
        scn.MoveCardsToDSHand(detainment);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(detainment));
        scn.DSPlayCard(detainment);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, detainment);
    }

    @Test
    public void ImperialDetainmentMakesWeHaveAPrisonerAUsedInterrupt() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");
        var prisoner = scn.GetDSCard("prisoner");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detainment);
        scn.MoveCardsToDSHand(prisoner);

        assertEquals(CardSubtype.USED, scn.game().getModifiersQuerying().getInterruptType(scn.gameState(), prisoner));
        assertFalse(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.USED_INTERRUPT, prisoner)
                .isEmpty());
    }

    @Test
    public void ImperialDetainmentDeploysBesiegedFromReserveDeck() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");
        var besieged = scn.GetDSCard("besieged");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detainment);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfDSReserveDeck(besieged);

        assertTrue(scn.DSCardActionAvailable(detainment, "Deploy Besieged or Launch Bay"));
        scn.DSUseCardAction(detainment, "Deploy Besieged or Launch Bay");
        for (int i = 0; i < 20; i++) {
            if (besieged.getZone() == Zone.SIDE_OF_TABLE) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(besieged)) {
                scn.DSChooseCard(besieged);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.PassAllResponses();
                continue;
            }
            break;
        }

        assertInZone(Zone.SIDE_OF_TABLE, besieged);
    }

    @Test
    public void ImperialDetainmentRetrievesBesiegedFromLostPile() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");
        var besieged = scn.GetDSCard("besieged");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detainment);
        scn.MoveCardsToTopOfDSLostPile(besieged);
        int usedBefore = scn.GetDSUsedPileCount();
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(detainment, "Retrieve Besieged or Launch Bay"));
        scn.DSUseCardAction(detainment, "Retrieve Besieged or Launch Bay");
        for (int i = 0; i < 16; i++) {
            if (besieged.getZone() == Zone.USED_PILE || besieged.getZone() == Zone.TOP_OF_USED_PILE
                    || scn.GetDSUsedPileCount() > usedBefore) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(besieged)) {
                scn.DSChooseCard(besieged);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.PassAllResponses();
                continue;
            }
            break;
        }

        assertTrue(besieged.getZone() == Zone.USED_PILE || besieged.getZone() == Zone.TOP_OF_USED_PILE
                || scn.GetDSUsedPileCount() > usedBefore);
    }

    @Test
    public void ImperialDetainmentMakesTIEsDeployPlusOneToLaunchBay() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");
        var launch = scn.GetDSCard("launch");
        var scout = scn.GetDSCard("scout");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.MoveCardsToDSHand(launch);
        scn.MoveLocationToTable(launch);
        scn.MoveCardsToDSHand(scout);
        float without = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scout, scout, launch, false, null, false, 0, null, false);
        scn.MoveCardsToDSSideOfTable(detainment);
        float with = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scout, scout, launch, false, null, false, 0, null, false);

        assertEquals(without + 1, with, scn.epsilon);
    }

    @Test
    public void ImperialDetainmentMakesOpponentLoseOneForceWhenYourImperialCapturesACharacter() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("detainment");
        var prisoner = scn.GetDSCard("prisoner");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detainment);
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        int lsForceBefore = scn.GetLSLifeForceRemaining();
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);

        boolean captured = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && (scn.DSPlayUsedInterruptAvailable(prisoner)
                    || scn.DSCardActionAvailable(prisoner, "Capture")
                    || scn.DSCardPlayAvailable(prisoner))) {
                if (scn.DSPlayUsedInterruptAvailable(prisoner)) {
                    scn.DSPlayUsedInterrupt(prisoner);
                } else if (scn.DSCardActionAvailable(prisoner, "Capture")) {
                    scn.DSUseCardAction(prisoner, "Capture");
                } else {
                    scn.DSPlayCard(prisoner);
                }
                captured = true;
                break;
            }
            if (scn.LSGetDecision() != null && (scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment())
                    && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)
                    && (scn.DSChoiceAvailable("Pass") || scn.DSDecisionAvailable("optional")
                    || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() == null && scn.DSGetDecision() == null) {
                break;
            }
            break;
        }
        assertTrue(captured);
        boolean forceLoss = false;
        for (int i = 0; i < 24; i++) {
            var actionTexts = scn.DSGetDecision() != null && scn.DSGetADParam("actionText") != null
                    ? scn.DSGetADParamAsList("actionText") : null;
            if (actionTexts != null) {
                for (String a : actionTexts) {
                    if (a != null && a.toLowerCase().contains("lose 1 force")) {
                        scn.DSChooseAction(a);
                        forceLoss = true;
                        break;
                    }
                }
            }
            if (forceLoss) {
                break;
            }
            if (scn.DSCaptureDecisionAvailable()) {
                scn.DSChooseSeizeCaptive();
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
                continue;
            }
            if (Boolean.TRUE.equals(scn.DSChoiceAvailable("Yes"))) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.DSDecisionAvailable(" Force - Optional responses")
                    && (actionTexts == null || actionTexts.stream().noneMatch(a -> a != null && a.toLowerCase().contains("lose 1 force")))) {
                scn.PassForceUseResponses();
                continue;
            }
            if (scn.DSDecisionAvailable("Playing <div")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSDecisionAvailable("optional")
                    || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(forceLoss);
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Force to lose")) {
            if (scn.GetLSForcePileCount() > 0) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            } else {
                scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            }
        }
        scn.PassAllResponses();
        assertTrue(scn.GetLSLifeForceRemaining() < lsForceBefore);
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }

    private void reenterDsPhaseActions(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        for (int i = 0; i < 6; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(card, text)) {
                return;
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
            break;
        }
    }
}
