package com.gempukku.swccgo.cards.set1210.dark;

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
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_057_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ywing", "1_147");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("emergency", "1210_57");
                    put("scope", "1_199");
                    put("rifle", "1_317");
                    put("saber", "1_324");
                    put("tie", "9_159");
                    put("pilot", "1_180");
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
    public void EmergencyDeploymentStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("emergency").getBlueprint();

        assertEquals("Emergency Deployment", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void EmergencyDeploymentTakesDeviceFromReserveDeck() {
        var scn = GetScenario();
        var emergency = scn.GetDSCard("emergency");
        var scope = scn.GetDSCard("scope");

        scn.StartGame();
        scn.MoveCardsToDSHand(emergency);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scope);

        assertTrue(scn.DSCardPlayAvailable(emergency, "Take device or weapon"));
        scn.DSPlayCard(emergency, "Take device or weapon");
        for (int i = 0; i < 20; i++) {
            if (scope.getZone() == Zone.HAND) {
                break;
            }
            if (chooseIfSelectable(scn, true, scope)) {
                continue;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            break;
        }
        finishUsedInterrupt(scn, emergency);

        assertInHand(scope);
        assertInZone(Zone.USED_PILE, emergency);
    }

    @Test
    public void EmergencyDeploymentTakesNonLightsaberWeaponAndDoesNotOfferLightsaber() {
        var scn = GetScenario();
        var emergency = scn.GetDSCard("emergency");
        var rifle = scn.GetDSCard("rifle");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToDSHand(emergency);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(rifle, saber);

        scn.DSPlayCard(emergency, "Take device or weapon");
        for (int i = 0; i < 20; i++) {
            if (isSelectable(scn, true, rifle) || isSelectable(scn, true, saber)) {
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
        assertTrue(isSelectable(scn, true, rifle));
        assertFalse(isSelectable(scn, true, saber));
        scn.DSChooseCard(rifle);
        for (int i = 0; i < 10; i++) {
            if (rifle.getZone() == Zone.HAND) {
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
        finishUsedInterrupt(scn, emergency);

        assertInHand(rifle);
        assertTrue(saber.getZone() == Zone.RESERVE_DECK || saber.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertInZone(Zone.USED_PILE, emergency);
    }

    @Test
    public void EmergencyDeploymentDeploysUnpilotedTieAndPilotAsReactToSystemBattle() {
        var scn = GetScenario();
        var emergency = scn.GetDSCard("emergency");
        var tie = scn.GetDSCard("tie");
        var pilot = scn.GetDSCard("pilot");
        var dsTie = scn.GetDSCard("dsTie");
        var ywing = scn.GetLSCard("ywing");
        var tibrin = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(emergency);
        scn.MoveCardsToLocation(tibrin, ywing, dsTie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(tie, pilot);

        scn.LSInitiateBattle(tibrin);
        scn.PassForceUseResponses();
        assertTrue(hasCardActions(scn, true));
        assertTrue(scn.DSCardPlayAvailable(emergency) || scn.DSActionAvailable("Deploy TIE"));
        if (scn.DSCardPlayAvailable(emergency, "Deploy TIE")) {
            scn.DSPlayCard(emergency, "Deploy TIE");
        } else if (scn.DSCardPlayAvailable(emergency)) {
            scn.DSPlayCard(emergency);
        } else {
            scn.DSChooseAction("Deploy TIE");
        }
        for (int i = 0; i < 24; i++) {
            if (tie.getAtLocation() == tibrin && (pilot.getAttachedTo() == tie || pilot.getAtLocation() == tibrin)) {
                break;
            }
            if (chooseIfSelectable(scn, true, pilot) || chooseIfSelectable(scn, true, tie)
                    || chooseIfSelectable(scn, true, tibrin)) {
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
        finishUsedInterrupt(scn, emergency);

        assertEquals(tibrin, tie.getAtLocation());
        assertEquals(tie, pilot.getAttachedTo());
        assertInZone(Zone.USED_PILE, emergency);
        assertTrue(scn.IsParticipatingInBattle(tie));
    }

    @Test
    public void EmergencyDeploymentReactIsNotOfferedForSiteBattle() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("emergency", "1210_57");
                    put("trooper", "1_194");
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
        var emergency = scn.GetDSCard("emergency");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(emergency);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassForceUseResponses();

        assertFalse(hasCardActions(scn, true) && scn.DSCardPlayAvailable(emergency));
    }

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        if (dark) {
            return scn.DSGetADParam("cardId") != null && scn.DSGetADParam("actionText") != null;
        }
        return scn.LSGetADParam("cardId") != null && scn.LSGetADParam("actionText") != null;
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            return;
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
