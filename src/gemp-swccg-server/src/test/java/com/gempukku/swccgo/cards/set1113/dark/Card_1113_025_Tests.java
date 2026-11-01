package com.gempukku.swccgo.cards.set1113.dark;

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

public class Card_1113_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("odds", "11_31");
                    put("worse", "5_50");
                }},
                new HashMap<>() {{
                    put("watch", "1113_25");
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("kessel", "1113_15");
                    put("carida", "8_156");
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
    public void WatchYourBackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("watch").getBlueprint();

        assertEquals("Watch Your Back!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.INTERRUPT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WatchYourBackDeploysBattlegroundSystemFromReserveDeck() {
        var scn = GetScenario();
        var watch = scn.GetDSCard("watch");
        var kessel = scn.GetDSCard("kessel");
        var carida = scn.GetDSCard("carida");

        scn.StartGame();
        scn.MoveLocationToTable(carida);
        scn.MoveCardsToDSHand(watch);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(kessel);

        assertTrue(scn.DSCardPlayAvailable(watch, "Deploy a Virtual Set 13 site"));
        scn.DSPlayCard(watch, "Deploy a Virtual Set 13 site");
        for (int i = 0; i < 20; i++) {
            if (kessel.getZone() == Zone.LOCATIONS) {
                break;
            }
            if (chooseIfSelectable(scn, true, kessel)) {
                continue;
            }
            if (scn.DSDecisionAvailable("which side") || scn.DSDecisionAvailable("On which side")) {
                scn.DSChoose("Left");
                continue;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            scn.PassAllResponses();
        }
        for (int i = 0; i < 10; i++) {
            if (watch.getZone() == Zone.USED_PILE) {
                break;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            break;
        }

        assertEquals(Zone.LOCATIONS, kessel.getZone());
        assertInZone(Zone.USED_PILE, watch);
    }

    @Test
    public void WatchYourBackCancelsAndRedrawsBattleDestinyOpponentJustModified() {
        var scn = GetScenario();
        var watch = scn.GetDSCard("watch");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var odds = scn.GetLSCard("odds");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToDSHand(watch);
        scn.MoveCardsToLSHand(odds);
        scn.PrepareDSDestiny(4);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected DS battle destiny prompt", scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        if (scn.DSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, true)) {
            scn.DSPass();
        }

        boolean modified = playLsDestinyResponse(scn, odds, "Subtract 1");
        boolean redrawn = playDsWatch(scn, watch, "Cancel and redraw");
        settleInterruptToUsedPile(scn, watch);

        assertTrue(modified);
        assertTrue(redrawn);
        assertInZone(Zone.USED_PILE, watch);
    }

    @Test
    public void WatchYourBackSetsCanceledBattleDestinyToOccupiedBattlegrounds() {
        var scn = GetScenario();
        var watch = scn.GetDSCard("watch");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var worse = scn.GetLSCard("worse");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToDSHand(watch);
        scn.MoveCardsToLSHand(worse);
        scn.EnsureLSForcePile(4);
        scn.PrepareDSDestiny(5);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue("expected DS battle destiny prompt", scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        if (scn.DSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, true)) {
            scn.DSPass();
        }

        boolean canceled = playLsDestinyResponse(scn, worse, "Cancel battle destiny");
        boolean setX = playDsWatch(scn, watch, "Set canceled");
        settleInterruptToUsedPile(scn, watch);

        assertTrue(canceled);
        assertTrue(setX);
        assertInZone(Zone.USED_PILE, watch);
    }

    private boolean playLsDestinyResponse(VirtualTableScenario scn,
                                          com.gempukku.swccgo.game.PhysicalCardImpl card, String actionText) {
        if (scn.LSGetDecision() == null || !hasCardActions(scn, false)) {
            return false;
        }
        if (scn.LSCardPlayAvailable(card, actionText)) {
            scn.LSPlayCard(card, actionText);
        } else if (scn.LSActionAvailable(actionText)) {
            scn.LSChooseAction(actionText);
        } else if (scn.LSCardPlayAvailable(card)) {
            scn.LSPlayCard(card);
        } else {
            return false;
        }
        for (int i = 0; i < 12; i++) {
            if (hasCardActions(scn, true)) {
                break;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
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

    private boolean playDsWatch(VirtualTableScenario scn,
                                com.gempukku.swccgo.game.PhysicalCardImpl watch, String actionText) {
        if (scn.DSGetDecision() == null || !hasCardActions(scn, true)) {
            return false;
        }
        if (scn.DSCardPlayAvailable(watch, actionText)) {
            scn.DSPlayCard(watch, actionText);
        } else if (scn.DSActionAvailable(actionText)) {
            scn.DSChooseAction(actionText);
        } else {
            return false;
        }
        return true;
    }

    private void settleInterruptToUsedPile(VirtualTableScenario scn,
                                           com.gempukku.swccgo.game.PhysicalCardImpl watch) {
        for (int i = 0; i < 16; i++) {
            if (watch.getZone() == Zone.USED_PILE || watch.getZone() == Zone.TOP_OF_USED_PILE) {
                return;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
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
