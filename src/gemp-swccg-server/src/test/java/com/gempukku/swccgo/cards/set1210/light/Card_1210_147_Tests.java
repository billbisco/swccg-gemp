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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_147_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("stew", "1210_147");
                    put("owen", "1_22");
                    put("farm", "1_132");
                    put("hydro", "1210_117");
                    put("crop", "1_12");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void BeruStewStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("stew").getBlueprint();

        assertEquals("Beru Stew", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
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
    public void BeruStewUsedActivatesOneForceWithoutALars() {
        var scn = GetScenario();
        var stew = scn.GetLSCard("stew");

        scn.StartGame();
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(1), scn.GetLSFiller(2));
        scn.SkipToPhase(Phase.DEPLOY);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(stew, "Activate 1"));
        scn.LSPlayCard(stew, "Activate 1");
        finishUsedInterrupt(scn, stew);

        assertEquals(force + 1, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, stew);
    }

    @Test
    public void BeruStewUsedActivatesTwoForceWhenALarsIsOnTable() {
        var scn = GetScenario();
        var stew = scn.GetLSCard("stew");
        var owen = scn.GetLSCard("owen");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, owen);
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3));
        scn.SkipToPhase(Phase.DEPLOY);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(stew, "Activate 2"));
        scn.LSPlayCard(stew, "Activate 2");
        finishUsedInterrupt(scn, stew);

        assertEquals(force + 2, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, stew);
    }

    @Test
    public void BeruStewUsedAddsDeviceDestinyToTotalPowerDuringBattleAtAFarm() {
        var scn = GetScenario();
        var stew = scn.GetLSCard("stew");
        var farm = scn.GetLSCard("farm");
        var hydro = scn.GetLSCard("hydro");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, hydro);
        scn.MoveCardsToLocation(farm, luke, vader);
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(farm);
        scn.PassAllResponses();
        assertTrue(scn.LSCardPlayAvailable(stew, "Add device destiny") || scn.LSCardPlayAvailable(stew));
        if (scn.LSCardPlayAvailable(stew, "Add device destiny")) {
            scn.LSPlayCard(stew, "Add device destiny");
        } else {
            scn.LSPlayCard(stew);
        }
        for (int i = 0; i < 20; i++) {
            if (stew.getZone() == Zone.USED_PILE) {
                break;
            }
            if (chooseIfSelectable(scn, false, hydro)) {
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
        finishUsedInterrupt(scn, stew);

        assertInZone(Zone.USED_PILE, stew);
        assertEquals(farm, hydro.getAttachedTo());
    }

    @Test
    public void BeruStewLostPlacesACropOutOfPlayAfterBattleDestinies() {
        var scn = GetScenario();
        var stew = scn.GetLSCard("stew");
        var farm = scn.GetLSCard("farm");
        var hydro = scn.GetLSCard("hydro");
        var crop = scn.GetLSCard("crop");
        var luke = scn.GetLSCard("luke");
        var owen = scn.GetLSCard("owen");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, hydro);
        scn.StackCardsOn(hydro, crop);
        scn.MoveCardsToLocation(farm, luke, owen, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(stew);
        scn.PrepareLSDestiny(4);
        scn.PrepareDSDestiny(5);

        scn.LSInitiateBattle(farm);
        scn.SkipToPowerSegment();
        if (scn.LSDecisionAvailable("battle destiny?")) {
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        if (scn.DSDecisionAvailable("battle destiny?")) {
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        if (scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH") && !hasCropAction(scn)) {
            scn.DSPass();
        }
        assertTrue("LS=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : "null")
                        + " DS=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : "null"),
                hasCropAction(scn));
        scn.LSPlayCard(stew, "Place");
        if (chooseIfSelectable(scn, false, crop) || scn.LSHasCardChoiceAvailable(crop)) {
            scn.LSChooseCard(crop);
        }
        for (int i = 0; i < 12; i++) {
            if (crop.getZone() == Zone.OUT_OF_PLAY && (stew.getZone() == Zone.LOST_PILE
                    || stew.getZone() == Zone.TOP_OF_LOST_PILE)) {
                break;
            }
            if (chooseIfSelectable(scn, false, crop)) {
                continue;
            }
            if (scn.LSDecisionAvailable("Playing") || scn.DSDecisionAvailable("Playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.OUT_OF_PLAY, crop.getZone());
        assertInZone(Zone.LOST_PILE, stew);
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt) {
        for (int i = 0; i < 12; i++) {
            if (interrupt.getZone() == Zone.USED_PILE || interrupt.getZone() == Zone.TOP_OF_USED_PILE) {
                return;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (scn.LSDecisionAvailable("Playing") || scn.DSDecisionAvailable("Playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional")
                    && !hasCardActions(scn, false)) {
                scn.LSPass();
            }
        }
    }

    private void drawBattleDestiny(VirtualTableScenario scn, boolean dark) {
        if (dark) {
            if (scn.DSDecisionAvailable("battle destiny?")) {
                scn.DSChooseYes();
                scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
                scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
            }
            if (scn.DSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, true)) {
                scn.DSPass();
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCropAction(scn)) {
                scn.LSPass();
            }
            return;
        }
        if (scn.LSDecisionAvailable("battle destiny?")) {
            scn.LSChooseYes();
            scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
            scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        }
        if (scn.LSDecisionAvailable("DESTINY_DRAWN") && !hasCropAction(scn)) {
            scn.LSPass();
        }
        if (scn.DSDecisionAvailable("DESTINY_DRAWN") && !hasCardActions(scn, true)) {
            scn.DSPass();
        }
    }

    private boolean hasCropAction(VirtualTableScenario scn) {
        return hasCardActions(scn, false) && (scn.LSCardPlayAvailable(stewCard(scn), "Place")
                || scn.LSCardPlayAvailable(stewCard(scn), "crop"));
    }

    private PhysicalCardImpl stewCard(VirtualTableScenario scn) {
        return scn.GetLSCard("stew");
    }

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        String[] cardId = dark ? scn.DSGetADParam("cardId") : scn.LSGetADParam("cardId");
        String[] actionText = dark ? scn.DSGetADParam("actionText") : scn.LSGetADParam("actionText");
        return cardId != null && cardId.length > 0 && actionText != null && actionText.length > 0;
    }

    private boolean chooseIfSelectable(VirtualTableScenario scn, boolean dark, PhysicalCardImpl card) {
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

    private boolean isSelectable(VirtualTableScenario scn, boolean dark, PhysicalCardImpl card) {
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
