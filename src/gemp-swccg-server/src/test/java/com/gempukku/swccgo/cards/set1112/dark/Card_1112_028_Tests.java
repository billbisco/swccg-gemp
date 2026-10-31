package com.gempukku.swccgo.cards.set1112.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lsblaster", "1_152");
                }},
                new HashMap<>() {{
                    put("attack", "1112_28");
                    put("trooper", "1_194");
                    put("blaster", "1_317");
                    put("vader", "1210_7");
                    put("saber", "1_324");
                    put("black3", "1210_74");
                    put("ds613", "1_174");
                    put("dantooine", "1_282");
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
    public void CoordinatedAttackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("attack").getBlueprint();

        assertEquals("Coordinated Attack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoordinatedAttackUsedPlacesJustHitTargetAndAttachedCardsInOwnersUsedPile() {
        var scn = GetScenario();
        var attack = scn.GetDSCard("attack");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var lsblaster = scn.GetLSCard("lsblaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, luke);
        scn.AttachCardsTo(trooper, blaster);
        scn.AttachCardsTo(luke, lsblaster);
        scn.MoveCardsToDSHand(attack);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        fireUntilHitOptional(scn, 1);

        assertTrue(scn.DSDecisionAvailable("HIT"));
        assertTrue(scn.DSCardPlayAvailable(attack, "Place target") || scn.DSCardPlayAvailable(attack));
        if (scn.DSCardPlayAvailable(attack, "Place target")) {
            scn.DSPlayCard(attack, "Place target");
        } else {
            scn.DSPlayCard(attack);
        }
        for (int i = 0; i < 24; i++) {
            if ((luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE)
                    && (attack.getZone() == Zone.USED_PILE || attack.getZone() == Zone.TOP_OF_USED_PILE)) {
                break;
            }
            if (chooseIfSelectable(scn, true, luke)) {
                continue;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (confirmCardSelection(scn, false) || confirmCardSelection(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            scn.PassAllResponses();
        }

        assertInZone(Zone.USED_PILE, luke);
        assertInZone(Zone.USED_PILE, lsblaster);
        assertInZone(Zone.USED_PILE, attack);
    }

    @Test
    public void CoordinatedAttackUsedIsNotOfferedAfterALightsaberHit() {
        var scn = GetScenario();
        var attack = scn.GetDSCard("attack");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToDSHand(attack);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.DSCardActionAvailable(saber, "Fire"));
        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(luke);
        fireUntilHitOptional(scn, 2);

        if (scn.DSDecisionAvailable("HIT")) {
            assertFalse(scn.DSCardPlayAvailable(attack));
        }
        else {
            assertTrue(luke.isHit());
        }
    }

    @Test
    public void CoordinatedAttackLostCancelsHeroicAndImmunityToAttritionThisTurn() {
        var scn = GetScenario();
        var attack = scn.GetDSCard("attack");
        var black3 = scn.GetDSCard("black3");
        var ds613 = scn.GetDSCard("ds613");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, black3);
        scn.BoardAsPilot(black3, ds613);
        scn.MoveCardsToDSHand(attack);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.HasKeyword(black3, Keyword.HEROIC));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black3), scn.epsilon);
        assertTrue(scn.DSCardPlayAvailable(attack, "Cancel Heroic"));
        scn.DSPlayCard(attack, "Cancel Heroic");
        if (scn.DSHasCardChoiceAvailable(black3)) {
            scn.DSChooseCard(black3);
        }
        scn.PassAllResponses();

        assertFalse(scn.HasKeyword(black3, Keyword.HEROIC));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black3), scn.epsilon);
        assertInZone(Zone.LOST_PILE, attack);
    }

    private void fireUntilHitOptional(VirtualTableScenario scn, int draws) {
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        for (int i = 0; i < draws; ++i) {
            scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("ABOUT_TO_BE_HIT");
        if (scn.LSDecisionAvailable("HIT") || scn.LSDecisionAvailable("ABOUT_TO_BE_HIT")) {
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && (scn.LSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.LSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.LSPass();
            }
        }
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

    private boolean confirmCardSelection(VirtualTableScenario scn, boolean dark) {
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        String[] selectable = dark ? scn.DSGetADParam("selectable") : scn.LSGetADParam("selectable");
        if (minParam == null || selectable == null) {
            return false;
        }
        int min = Integer.parseInt(minParam[0]);
        if (min <= 0) {
            return false;
        }
        java.util.List<String> picks = new java.util.ArrayList<>();
        for (int i = 0; i < selectable.length && picks.size() < min; i++) {
            if ("true".equalsIgnoreCase(selectable[i])) {
                picks.add("    " + i);
            }
        }
        if (picks.isEmpty()) {
            return false;
        }
        if (dark) {
            scn.DSDecided(String.join(",", picks));
        } else {
            scn.LSDecided(String.join(",", picks));
        }
        return true;
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
