package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_153_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dice", "1210_153");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("cube", "11_90");
                    put("watto", "11_65");
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
    public void HansDiceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dice").getBlueprint();

        assertEquals("Han's Dice", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansDiceCancelsWattosChanceCubeBeingPlayed() {
        var scn = GetScenario();
        var dice = scn.GetLSCard("dice");
        var luke = scn.GetLSCard("luke");
        var cube = scn.GetDSCard("cube");
        var watto = scn.GetDSCard("watto");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, watto);
        scn.MoveCardsToLSHand(dice);
        scn.MoveCardsToDSHand(cube);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassForceUseResponses();

        assertTrue(scn.DSCardPlayAvailable(cube) || scn.DSActionAvailable("Watto"));
        if (scn.DSCardPlayAvailable(cube)) {
            scn.DSPlayCard(cube);
        } else {
            scn.DSChooseAction("Watto");
        }
        boolean canceled = false;
        for (int i = 0; i < 24; i++) {
            if (cube.getZone() == Zone.LOST_PILE || cube.getZone() == Zone.USED_PILE
                    || cube.getZone() == Zone.TOP_OF_LOST_PILE) {
                canceled = true;
                break;
            }
            if (hasCardActions(scn, false) && (scn.LSCardPlayAvailable(dice)
                    || scn.LSActionAvailable("Cancel"))) {
                if (scn.LSCardPlayAvailable(dice, "Cancel")) {
                    scn.LSPlayCard(dice, "Cancel");
                } else if (scn.LSCardPlayAvailable(dice)) {
                    scn.LSPlayCard(dice);
                } else {
                    scn.LSChooseAction("Cancel");
                }
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (scn.DSDecisionAvailable("Playing") || scn.LSDecisionAvailable("Playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }
        finishUsedInterrupt(scn, dice);

        assertTrue(canceled);
        assertInZone(Zone.USED_PILE, dice);
        assertTrue(cube.getZone() == Zone.LOST_PILE || cube.getZone() == Zone.USED_PILE
                || cube.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void HansDiceCancelsAndRedrawsAJustDrawnDestinyWhenAbilityGreaterThanTwoIsInBattle() {
        var scn = GetScenario();
        var dice = scn.GetLSCard("dice");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(dice);
        scn.EnsureLSForcePile(1);
        scn.PrepareLSDestiny(4);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected LS battle destiny prompt", scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        boolean played = false;
        for (int i = 0; i < 16; i++) {
            if (dice.getZone() == Zone.USED_PILE || dice.getZone() == Zone.TOP_OF_USED_PILE) {
                played = true;
                break;
            }
            if (hasCardActions(scn, false) && (scn.LSCardPlayAvailable(dice, "Cancel")
                    || scn.LSCardPlayAvailable(dice, "re-draw") || scn.LSCardPlayAvailable(dice))) {
                if (scn.LSCardPlayAvailable(dice, "Cancel")) {
                    scn.LSPlayCard(dice, "Cancel");
                } else if (scn.LSCardPlayAvailable(dice, "re-draw")) {
                    scn.LSPlayCard(dice, "re-draw");
                } else {
                    scn.LSPlayCard(dice);
                }
                played = true;
                continue;
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, false)) {
                scn.LSPass();
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }
        settleInterruptToUsedPile(scn, dice);

        assertTrue(played);
        assertInZone(Zone.USED_PILE, dice);
    }

    @Test
    public void HansDiceOncePerTurnTakesItselfIntoHandWhenDrawnForDestiny() {
        var scn = GetScenario();
        var dice = scn.GetLSCard("dice");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfLSReserveDeck(dice);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected LS battle destiny prompt", scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        boolean taken = false;
        for (int i = 0; i < 16; i++) {
            if (dice.getZone() == Zone.HAND) {
                taken = true;
                break;
            }
            if (hasCardActions(scn, false) && (scn.LSActionAvailable("Take into hand")
                    || scn.LSActionAvailable("re-draw") || scn.LSCardActionAvailable(dice))) {
                if (scn.LSActionAvailable("Take into hand")) {
                    scn.LSChooseAction("Take into hand");
                } else if (scn.LSActionAvailable("re-draw")) {
                    scn.LSChooseAction("re-draw");
                } else {
                    scn.LSUseCardAction(dice);
                }
                continue;
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, false)) {
                scn.LSPass();
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }

        assertTrue(taken);
        assertInHand(dice);
    }

    private void finishUsedInterrupt(VirtualTableScenario scn,
                                     com.gempukku.swccgo.game.PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            return;
        }
    }

    private void settleInterruptToUsedPile(VirtualTableScenario scn,
                                           com.gempukku.swccgo.game.PhysicalCardImpl interrupt) {
        for (int i = 0; i < 16; i++) {
            if (interrupt.getZone() == Zone.USED_PILE || interrupt.getZone() == Zone.TOP_OF_USED_PILE) {
                return;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }
    }

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        String[] cardId = dark ? scn.DSGetADParam("cardId") : scn.LSGetADParam("cardId");
        String[] actionText = dark ? scn.DSGetADParam("actionText") : scn.LSGetADParam("actionText");
        return cardId != null && cardId.length > 0 && actionText != null && actionText.length > 0;
    }

    private boolean payForce(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null || decision.getText() == null) {
            return false;
        }
        String text = decision.getText().toLowerCase();
        if (!text.contains("force") || text.contains("retriev") || text.contains("forfeit")) {
            return false;
        }
        var force = dark ? scn.GetTopOfDSForcePile() : scn.GetTopOfLSForcePile();
        if (force != null && (dark ? scn.DSHasCardChoiceAvailable(force) : scn.LSHasCardChoiceAvailable(force))) {
            if (dark) {
                scn.DSChooseCard(force);
            } else {
                scn.LSChooseCard(force);
            }
            return true;
        }
        if (text.contains("optional")) {
            scn.PassForceUseResponses();
            return true;
        }
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        if (minParam != null && minParam.length > 0) {
            int min = Integer.parseInt(minParam[0]);
            if (dark) {
                scn.DSDecided(min);
            } else {
                scn.LSDecided(min);
            }
            return true;
        }
        return false;
    }

    private boolean resolveDecision(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null) {
            return false;
        }
        String text = decision.getText() == null ? "" : decision.getText().toLowerCase();
        if (text.contains("optional") || text.contains("or pass")) {
            if (dark) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
            return true;
        }
        var results = dark ? scn.DSGetMultipleChoices() : scn.LSGetMultipleChoices();
        if (results != null && !results.isEmpty()) {
            String pick = results.get(0);
            for (String option : results) {
                if (option.equalsIgnoreCase("Yes")) {
                    pick = option;
                    break;
                }
            }
            if (dark) {
                scn.DSChoose(pick);
            } else {
                scn.LSChoose(pick);
            }
            return true;
        }
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        String[] selectable = dark ? scn.DSGetADParam("selectable") : scn.LSGetADParam("selectable");
        if (minParam != null && selectable != null) {
            int min = Integer.parseInt(minParam[0]);
            if (min == 0) {
                if (dark) {
                    scn.DSPass();
                } else {
                    scn.LSPass();
                }
                return true;
            }
            java.util.List<String> picks = new java.util.ArrayList<>();
            for (int i = 0; i < selectable.length && picks.size() < min; i++) {
                if ("true".equalsIgnoreCase(selectable[i])) {
                    picks.add("    " + i);
                }
            }
            if (!picks.isEmpty()) {
                if (dark) {
                    scn.DSDecided(String.join(",", picks));
                } else {
                    scn.LSDecided(String.join(",", picks));
                }
                return true;
            }
        }
        return false;
    }
}
