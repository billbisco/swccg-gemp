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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_149_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("spin", "1210_149");
                    put("luke", "1_19");
                    put("kessel", "1_126");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("odds", "14_106");
                    put("worse", "5_139");
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
    public void DarklighterSpinStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("spin").getBlueprint();

        assertEquals("Darklighter Spin", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarklighterSpinDeploysBattlegroundSystemFromReserveDeck() {
        var scn = GetScenario();
        var spin = scn.GetLSCard("spin");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();
        scn.MoveCardsToLSHand(spin);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(kessel);

        assertTrue(scn.LSCardPlayAvailable(spin, "Deploy battleground system"));
        scn.LSPlayCard(spin, "Deploy battleground system");
        for (int i = 0; i < 20; i++) {
            if (kessel.getZone() == Zone.LOCATIONS) {
                break;
            }
            if (chooseIfSelectable(scn, false, kessel)) {
                continue;
            }
            if (scn.LSDecisionAvailable("which side") || scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
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
        finishUsedInterrupt(scn, spin);

        assertEquals(Zone.LOCATIONS, kessel.getZone());
        assertInZone(Zone.USED_PILE, spin);
    }

    @Test
    public void DarklighterSpinCancelsAndRedrawsBattleDestinyOpponentJustModified() {
        var scn = GetScenario();
        var spin = scn.GetLSCard("spin");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var odds = scn.GetDSCard("odds");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(spin);
        scn.MoveCardsToDSHand(odds);
        scn.PrepareLSDestiny(4);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected LS battle destiny prompt", scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, false)) {
            scn.LSPass();
        }

        boolean modified = playDsDestinyResponse(scn, odds, "Subtract 1");
        boolean redrawn = playLsSpin(scn, spin, "Cancel and redraw");
        settleInterruptToUsedPile(scn, spin);

        assertTrue(modified);
        assertTrue(redrawn);
        assertInZone(Zone.USED_PILE, spin);
    }

    @Test
    public void DarklighterSpinSetsCanceledBattleDestinyToOccupiedBattlegrounds() {
        var scn = GetScenario();
        var spin = scn.GetLSCard("spin");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var worse = scn.GetDSCard("worse");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(spin);
        scn.MoveCardsToDSHand(worse);
        scn.EnsureDSForcePile(4);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected LS battle destiny prompt", scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, false)) {
            scn.LSPass();
        }

        boolean canceled = playDsDestinyResponse(scn, worse, "Cancel battle destiny");
        boolean setX = playLsSpin(scn, spin, "Set canceled");
        settleInterruptToUsedPile(scn, spin);

        assertTrue(canceled);
        assertTrue(setX);
        assertInZone(Zone.USED_PILE, spin);
    }

    private boolean playDsDestinyResponse(VirtualTableScenario scn,
                                          com.gempukku.swccgo.game.PhysicalCardImpl card, String actionText) {
        if (scn.DSGetDecision() == null || !hasCardActions(scn, true)) {
            return false;
        }
        if (scn.DSCardPlayAvailable(card, actionText)) {
            scn.DSPlayCard(card, actionText);
        } else if (scn.DSActionAvailable(actionText)) {
            scn.DSChooseAction(actionText);
        } else if (scn.DSCardPlayAvailable(card)) {
            scn.DSPlayCard(card);
        } else {
            return false;
        }
        for (int i = 0; i < 12; i++) {
            if (hasCardActions(scn, false)) {
                break;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (scn.DSDecisionAvailable("Playing") || scn.LSDecisionAvailable("Playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (scn.DSDecisionAvailable("Force - Optional") || scn.LSDecisionAvailable("Force - Optional")) {
                scn.PassForceUseResponses();
                continue;
            }
            if (scn.DSDecisionAvailable("PUT_IN_CARD_PILE") || scn.LSDecisionAvailable("PUT_IN_CARD_PILE")
                    || scn.DSDecisionAvailable("FORFEITED") || scn.LSDecisionAvailable("FORFEITED")) {
                scn.PassResponses();
                continue;
            }
            break;
        }
        return true;
    }

    private boolean playLsSpin(VirtualTableScenario scn,
                               com.gempukku.swccgo.game.PhysicalCardImpl spin, String actionText) {
        if (scn.LSGetDecision() == null || !hasCardActions(scn, false)) {
            return false;
        }
        if (scn.LSCardPlayAvailable(spin, actionText)) {
            scn.LSPlayCard(spin, actionText);
        } else if (scn.LSActionAvailable(actionText)) {
            scn.LSChooseAction(actionText);
        } else {
            return false;
        }
        return true;
    }

    private void settleInterruptToUsedPile(VirtualTableScenario scn,
                                           com.gempukku.swccgo.game.PhysicalCardImpl spin) {
        for (int i = 0; i < 16; i++) {
            if (spin.getZone() == Zone.USED_PILE || spin.getZone() == Zone.TOP_OF_USED_PILE) {
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

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        String[] cardId = dark ? scn.DSGetADParam("cardId") : scn.LSGetADParam("cardId");
        String[] actionText = dark ? scn.DSGetADParam("actionText") : scn.LSGetADParam("actionText");
        return cardId != null && cardId.length > 0 && actionText != null && actionText.length > 0;
    }

    private boolean chooseIfSelectable(VirtualTableScenario scn, boolean dark,
                                       com.gempukku.swccgo.game.PhysicalCardImpl card) {
        if (!isSelectable(scn, dark, card)) {
            return false;
        }
        if (dark) {
            scn.DSChooseCard(card);
        } else {
            scn.LSChooseCard(card);
        }
        return true;
    }

    private boolean isSelectable(VirtualTableScenario scn, boolean dark,
                                 com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] bps = dark ? scn.DSGetADParam("blueprintId") : scn.LSGetADParam("blueprintId");
        String[] selectable = dark ? scn.DSGetADParam("selectable") : scn.LSGetADParam("selectable");
        if (bps != null && selectable != null) {
            for (int i = 0; i < Math.min(bps.length, selectable.length); i++) {
                if ("true".equalsIgnoreCase(selectable[i]) && card.getBlueprintId(true).equals(bps[i])) {
                    return true;
                }
            }
            return false;
        }
        return dark ? scn.DSHasCardChoiceAvailable(card) : scn.LSHasCardChoiceAvailable(card);
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
