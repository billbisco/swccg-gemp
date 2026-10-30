package com.gempukku.swccgo.cards.set1102.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("panic", "1102_17");
                    put("hydro", "1210_117");
                    put("blaster", "1_152");
                    put("saber", "1_157");
                    put("xwing", "2_70");
                    put("wedge", "2_23");
                    put("lsXwing", "1_146");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void PanicStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("panic").getBlueprint();

        assertEquals("Panic", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PanicTakesDeviceFromReserveDeck() {
        var scn = GetScenario();
        var panic = scn.GetLSCard("panic");
        var hydro = scn.GetLSCard("hydro");

        scn.StartGame();
        scn.MoveCardsToLSHand(panic);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(hydro);

        assertTrue(scn.LSCardPlayAvailable(panic, "Take device or weapon"));
        scn.LSPlayCard(panic, "Take device or weapon");
        for (int i = 0; i < 20; i++) {
            if (hydro.getZone() == Zone.HAND) {
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
            break;
        }
        finishUsedInterrupt(scn, panic);

        assertInHand(hydro);
        assertInZone(Zone.USED_PILE, panic);
    }

    @Test
    public void PanicTakesNonLightsaberWeaponAndDoesNotOfferLightsaber() {
        var scn = GetScenario();
        var panic = scn.GetLSCard("panic");
        var blaster = scn.GetLSCard("blaster");
        var saber = scn.GetLSCard("saber");

        scn.StartGame();
        scn.MoveCardsToLSHand(panic);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(blaster, saber);

        scn.LSPlayCard(panic, "Take device or weapon");
        for (int i = 0; i < 20; i++) {
            if (isSelectable(scn, false, blaster) || isSelectable(scn, false, saber)) {
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
        assertTrue(isSelectable(scn, false, blaster));
        assertFalse(isSelectable(scn, false, saber));
        scn.LSChooseCard(blaster);
        for (int i = 0; i < 10; i++) {
            if (blaster.getZone() == Zone.HAND) {
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
        finishUsedInterrupt(scn, panic);

        assertInHand(blaster);
        assertTrue(saber.getZone() == Zone.RESERVE_DECK || saber.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertInZone(Zone.USED_PILE, panic);
    }

    @Test
    public void PanicDeploysUnpilotedRebelStarfighterAndPilotAsReactToSystemBattle() {
        var scn = GetScenario();
        var panic = scn.GetLSCard("panic");
        var xwing = scn.GetLSCard("xwing");
        var wedge = scn.GetLSCard("wedge");
        var lsXwing = scn.GetLSCard("lsXwing");
        var tie = scn.GetDSCard("tie");
        var tibrin = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(panic);
        scn.MoveCardsToLocation(tibrin, lsXwing, tie);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(xwing, wedge);

        scn.DSInitiateBattle(tibrin);
        scn.PassForceUseResponses();
        assertTrue(hasCardActions(scn, false));
        assertTrue(scn.LSCardPlayAvailable(panic) || scn.LSActionAvailable("Deploy Rebel"));
        if (scn.LSCardPlayAvailable(panic, "Deploy Rebel")) {
            scn.LSPlayCard(panic, "Deploy Rebel");
        } else if (scn.LSCardPlayAvailable(panic)) {
            scn.LSPlayCard(panic);
        } else {
            scn.LSChooseAction("Deploy Rebel");
        }
        for (int i = 0; i < 24; i++) {
            if (xwing.getAtLocation() == tibrin && (wedge.getAttachedTo() == xwing || wedge.getAtLocation() == tibrin)) {
                break;
            }
            if (chooseIfSelectable(scn, false, wedge) || chooseIfSelectable(scn, false, xwing)
                    || chooseIfSelectable(scn, false, tibrin)) {
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
        finishUsedInterrupt(scn, panic);

        assertEquals(tibrin, xwing.getAtLocation());
        assertEquals(xwing, wedge.getAttachedTo());
        assertInZone(Zone.USED_PILE, panic);
        assertTrue(scn.IsParticipatingInBattle(xwing));
    }

    @Test
    public void PanicReactIsNotOfferedForSiteBattle() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("panic", "1102_17");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
        var panic = scn.GetLSCard("panic");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToLSHand(panic);
        scn.SkipToDSTurn(Phase.BATTLE);

        scn.DSInitiateBattle(site);
        scn.PassForceUseResponses();
        assertFalse(hasCardActions(scn, false) && (scn.LSCardPlayAvailable(panic, "Deploy Rebel")
                || scn.LSActionAvailable("Deploy Rebel") || scn.LSCardPlayAvailable(panic)));
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl interrupt) {
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
