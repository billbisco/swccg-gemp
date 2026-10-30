package com.gempukku.swccgo.cards.set1102.dark;

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

public class Card_1102_037_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("slug", "1_053");
                }},
                new HashMap<>() {{
                    put("reinforcements", "1102_37");
                    put("reinforcements2", "1102_37");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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
    public void ImperialReinforcementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("reinforcements").getBlueprint();

        assertEquals("Imperial Reinforcements", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
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
    public void ImperialReinforcementsRetrievesImperialsAndMayTakeOneIntoHand() {
        var scn = GetScenario();
        var reinforcements = scn.GetDSCard("reinforcements");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToDSHand(reinforcements);
        scn.MoveCardsToTopOfDSLostPile(trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(2);

        assertTrue(scn.DSCardPlayAvailable(reinforcements, "Draw destiny"));
        scn.DSPlayCard(reinforcements, "Draw destiny");
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        for (int i = 0; i < 24; i++) {
            if (trooper.getZone() == Zone.HAND) {
                break;
            }
            if (scn.DSDecisionAvailable("Viewing") || scn.DSDecisionAvailable("prior to choosing")) {
                scn.DSDecided("");
                continue;
            }
            if (scn.DSDecisionAvailable("Choose value for X")) {
                scn.DSDecided(1);
                continue;
            }
            if (chooseIfSelectable(scn, true, trooper)) {
                continue;
            }
            if (scn.DSDecisionAvailable("destiny")) {
                var results = scn.DSGetMultipleChoices();
                if (results != null && !results.isEmpty()) {
                    scn.DSChooseYes();
                    continue;
                }
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && (scn.LSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.LSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && (scn.DSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.DSGetDecision().getText().toLowerCase().contains("or pass"))
                    && !scn.DSDecisionAvailable("take into hand")
                    && !scn.DSDecisionAvailable("Choose a retrieved")
                    && !scn.DSDecisionAvailable("Used Pile")
                    && !scn.DSDecisionAvailable("Lost Pile")
                    && !scn.DSDecisionAvailable("Viewing")
                    && !scn.DSDecisionAvailable("prior to choosing")) {
                scn.DSPass();
                continue;
            }
            scn.PassAllResponses();
        }

        assertInHand(trooper);
        assertInZone(Zone.LOST_PILE, reinforcements);
    }

    @Test
    public void ImperialReinforcementsCannotRetrieveTwiceInAGame() {
        var scn = GetScenario();
        var reinforcements = scn.GetDSCard("reinforcements");
        var reinforcements2 = scn.GetDSCard("reinforcements2");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToDSHand(reinforcements);
        scn.MoveCardsToTopOfDSLostPile(trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(2);

        assertTrue(scn.DSCardPlayAvailable(reinforcements, "Draw destiny"));
        scn.DSPlayCard(reinforcements, "Draw destiny");
        for (int i = 0; i < 24; i++) {
            if (reinforcements.getZone() == Zone.LOST_PILE || reinforcements.getZone() == Zone.TOP_OF_LOST_PILE) {
                if (!scn.DSDecisionAvailable("take into hand") && !scn.DSHasCardChoiceAvailable(trooper)) {
                    break;
                }
            }
            if (scn.DSDecisionAvailable("Choose value for X")) {
                scn.DSDecided(1);
                continue;
            }
            if (scn.DSDecisionAvailable("Lost Pile") || scn.DSDecisionAvailable("Viewing")) {
                scn.DSDismissRevealedCards();
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(trooper)) {
                scn.DSChooseCard(trooper);
                continue;
            }
            if (scn.DSDecisionAvailable("take into hand") || scn.DSDecisionAvailable("Choose a retrieved")) {
                scn.DSDecided("");
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
            scn.PassAllResponses();
        }

        scn.MoveCardsToTopOfDSLostPile(trooper2);
        scn.MoveCardsToDSHand(reinforcements2);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardPlayAvailable(reinforcements2, "Draw destiny"));
    }

    @Test
    public void ImperialReinforcementsCancelsKlorslug() {
        var scn = GetScenario();
        var reinforcements = scn.GetDSCard("reinforcements");
        var slug = scn.GetLSCard("slug");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(slug);
        scn.MoveCardsToDSHand(reinforcements);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(reinforcements, "Cancel"));
        scn.DSPlayCard(reinforcements, "Cancel");
        if (scn.DSHasCardChoiceAvailable(slug)) {
            scn.DSChooseCard(slug);
        }
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, slug);
        assertInZone(Zone.LOST_PILE, reinforcements);
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
