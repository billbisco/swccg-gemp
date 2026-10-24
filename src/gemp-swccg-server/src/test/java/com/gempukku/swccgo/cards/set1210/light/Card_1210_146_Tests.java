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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_146_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("balanced", "1210_146");
                    put("corvette", "1_140");
                    put("cruiser", "9_79");
                    put("frigate", "9_80");
                    put("yavin4", "1_135");
                }},
                new HashMap<>() {{
                    put("maneuvers", "1_241");
                    put("dsTie", "1_304");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BalancedAttackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("balanced").getBlueprint();

        assertEquals("Balanced Attack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
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
    public void BalancedAttackDeploysFrigateMinusTwoToSameSystemAsCorvetteAndCruiser() {
        var scn = GetScenario();
        var balanced = scn.GetLSCard("balanced");
        var corvette = scn.GetLSCard("corvette");
        var cruiser = scn.GetLSCard("cruiser");
        var frigate = scn.GetLSCard("frigate");
        var yavin4 = scn.GetLSCard("yavin4");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveCardsToLocation(yavin4, corvette, cruiser);
        scn.MoveCardsToLSHand(balanced);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        scn.MoveCardsToTopOfLSReserveDeck(frigate);

        assertTrue(scn.LSCardPlayAvailable(balanced, "Deploy frigate"));
        scn.LSPlayCard(balanced, "Deploy frigate");
        for (int i = 0; i < 20; i++) {
            if (frigate.getAtLocation() == yavin4) {
                break;
            }
            if (chooseIfSelectable(scn, false, frigate) || chooseIfSelectable(scn, false, yavin4)) {
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
        for (int i = 0; i < 10; i++) {
            if (balanced.getZone() == Zone.USED_PILE) {
                break;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            break;
        }

        assertAtLocation(yavin4, frigate);
        assertInZone(Zone.USED_PILE, balanced);
    }

    @Test
    public void BalancedAttackDeploysCruiserMinusThreeToSameSystemAsCorvetteAndFrigate() {
        var scn = GetScenario();
        var balanced = scn.GetLSCard("balanced");
        var corvette = scn.GetLSCard("corvette");
        var cruiser = scn.GetLSCard("cruiser");
        var frigate = scn.GetLSCard("frigate");
        var yavin4 = scn.GetLSCard("yavin4");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveCardsToLocation(yavin4, corvette, frigate);
        scn.MoveCardsToLSHand(balanced);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(cruiser);

        assertTrue(scn.LSCardPlayAvailable(balanced, "Deploy cruiser"));
        scn.LSPlayCard(balanced, "Deploy cruiser");
        for (int i = 0; i < 20; i++) {
            if (cruiser.getAtLocation() == yavin4) {
                break;
            }
            if (chooseIfSelectable(scn, false, cruiser) || chooseIfSelectable(scn, false, yavin4)) {
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
        for (int i = 0; i < 10; i++) {
            if (balanced.getZone() == Zone.USED_PILE) {
                break;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            break;
        }

        assertAtLocation(yavin4, cruiser);
        assertInZone(Zone.USED_PILE, balanced);
    }

    @Test
    public void BalancedAttackCancelsOpponentInterruptDuringBattleWithCorvetteCruiserAndFrigate() {
        var scn = GetScenario();
        var balanced = scn.GetLSCard("balanced");
        var corvette = scn.GetLSCard("corvette");
        var cruiser = scn.GetLSCard("cruiser");
        var frigate = scn.GetLSCard("frigate");
        var yavin4 = scn.GetLSCard("yavin4");
        var maneuvers = scn.GetDSCard("maneuvers");
        var dsTie = scn.GetDSCard("dsTie");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveCardsToLocation(yavin4, corvette, cruiser, frigate, dsTie);
        scn.MoveCardsToLSHand(balanced);
        scn.MoveCardsToDSHand(maneuvers);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(yavin4);
        scn.PassForceUseResponses();

        boolean canceled = false;
        for (int i = 0; i < 28; i++) {
            if (hasCardActions(scn, true) && scn.DSCardPlayAvailable(maneuvers)) {
                scn.DSPlayCard(maneuvers);
                continue;
            }
            if (hasCardActions(scn, false) && (scn.LSCardPlayAvailable(balanced)
                    || scn.LSActionAvailable("Cancel Dark Maneuvers"))) {
                if (scn.LSCardPlayAvailable(balanced)) {
                    scn.LSPlayCard(balanced);
                } else {
                    scn.LSChooseAction("Cancel Dark Maneuvers");
                }
                scn.PassAllResponses();
                canceled = true;
                break;
            }
            if (scn.DSHasCardChoiceAvailable(dsTie)) {
                scn.DSChooseCard(dsTie);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            if (scn.AwaitingDSWeaponsSegmentActions() || scn.AwaitingLSWeaponsSegmentActions()) {
                if (scn.AwaitingDSWeaponsSegmentActions() && hasCardActions(scn, true)
                        && scn.DSCardPlayAvailable(maneuvers)) {
                    continue;
                }
                if (scn.AwaitingDSWeaponsSegmentActions()) {
                    scn.DSPass();
                    continue;
                }
                scn.LSPass();
                continue;
            }
            scn.PassAllResponses();
        }
        assertTrue(canceled);

        assertTrue(maneuvers.getZone() == Zone.LOST_PILE || maneuvers.getZone() == Zone.TOP_OF_LOST_PILE
                || maneuvers.getZone() == Zone.USED_PILE || maneuvers.getZone() == Zone.TOP_OF_USED_PILE
                || maneuvers.getZone() == Zone.HAND);
        assertTrue(balanced.getZone() == Zone.USED_PILE || balanced.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void BalancedAttackCancelIsNotOfferedWithoutAllThreeShipTypes() {
        var scn = GetScenario();
        var balanced = scn.GetLSCard("balanced");
        var corvette = scn.GetLSCard("corvette");
        var cruiser = scn.GetLSCard("cruiser");
        var yavin4 = scn.GetLSCard("yavin4");
        var maneuvers = scn.GetDSCard("maneuvers");
        var dsTie = scn.GetDSCard("dsTie");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveCardsToLocation(yavin4, corvette, cruiser, dsTie);
        scn.MoveCardsToLSHand(balanced);
        scn.MoveCardsToDSHand(maneuvers);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(yavin4);
        scn.PassForceUseResponses();

        boolean playedManeuvers = false;
        for (int i = 0; i < 20; i++) {
            if (hasCardActions(scn, true) && scn.DSCardPlayAvailable(maneuvers)) {
                scn.DSPlayCard(maneuvers);
                playedManeuvers = true;
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(dsTie)) {
                scn.DSChooseCard(dsTie);
                continue;
            }
            if (hasCardActions(scn, false) && (scn.LSCardPlayAvailable(balanced)
                    || scn.LSActionAvailable("Cancel Dark Maneuvers"))) {
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            if (scn.AwaitingDSWeaponsSegmentActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(playedManeuvers);
        assertFalse(hasCardActions(scn, false) && scn.LSCardPlayAvailable(balanced));
        assertFalse(hasCardActions(scn, false) && scn.LSActionAvailable("Cancel Dark Maneuvers"));
    }

    @Test
    public void BalancedAttackFrigateDeployIsNotOfferedWithoutCruiser() {
        var scn = GetScenario();
        var balanced = scn.GetLSCard("balanced");
        var corvette = scn.GetLSCard("corvette");
        var frigate = scn.GetLSCard("frigate");
        var yavin4 = scn.GetLSCard("yavin4");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveCardsToLocation(yavin4, corvette);
        scn.MoveCardsToLSHand(balanced);
        scn.MoveCardsToTopOfLSReserveDeck(frigate);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(balanced, "Deploy frigate"));
    }

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        if (dark) {
            return scn.DSGetADParam("cardId") != null && scn.DSGetADParam("actionText") != null;
        }
        return scn.LSGetADParam("cardId") != null && scn.LSGetADParam("actionText") != null;
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
        if (!text.contains("force")) {
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
