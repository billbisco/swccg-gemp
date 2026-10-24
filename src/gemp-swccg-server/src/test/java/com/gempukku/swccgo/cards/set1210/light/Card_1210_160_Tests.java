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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_160_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("reinforcements", "1210_160");
                    put("reinforcements2", "1210_160");
                    put("trooper", "1_28");
                    put("trooper2", "1_28");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("dsTrooper", "1_194");
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
    public void RebelReinforcementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("reinforcements").getBlueprint();

        assertEquals("Rebel Reinforcements", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
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
    public void RebelReinforcementsRetrievesRebelsAndMayTakeOneIntoHand() {
        var scn = GetScenario();
        var reinforcements = scn.GetLSCard("reinforcements");
        var trooper = scn.GetLSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var dsTrooper = scn.GetDSCard("dsTrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, dsTrooper);
        scn.MoveCardsToLSHand(reinforcements);
        scn.MoveCardsToTopOfLSLostPile(trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(2);

        assertTrue(scn.LSCardPlayAvailable(reinforcements, "Draw destiny"));
        scn.LSPlayCard(reinforcements, "Draw destiny");
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        for (int i = 0; i < 24; i++) {
            if (trooper.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSDecisionAvailable("Choose value for X")) {
                scn.LSDecided(1);
                continue;
            }
            if (chooseIfSelectable(scn, false, trooper)) {
                continue;
            }
            if (scn.LSDecisionAvailable("destiny")) {
                var results = scn.LSGetMultipleChoices();
                if (results != null && !results.isEmpty()) {
                    scn.LSChooseYes();
                    continue;
                }
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && (scn.DSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.DSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && (scn.LSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.LSGetDecision().getText().toLowerCase().contains("or pass"))
                    && !scn.LSDecisionAvailable("take into hand")
                    && !scn.LSDecisionAvailable("Choose a retrieved")
                    && !scn.LSDecisionAvailable("Used Pile")
                    && !scn.LSDecisionAvailable("Lost Pile")
                    && !scn.LSDecisionAvailable("Viewing")
                    && !scn.LSDecisionAvailable("prior to choosing")) {
                scn.LSPass();
                continue;
            }
            scn.PassAllResponses();
        }

        assertInHand(trooper);
        assertInZone(Zone.LOST_PILE, reinforcements);
    }

    @Test
    public void RebelReinforcementsCannotRetrieveTwiceInAGame() {
        var scn = GetScenario();
        var reinforcements = scn.GetLSCard("reinforcements");
        var reinforcements2 = scn.GetLSCard("reinforcements2");
        var trooper = scn.GetLSCard("trooper");
        var trooper2 = scn.GetLSCard("trooper2");
        var vader = scn.GetDSCard("vader");
        var dsTrooper = scn.GetDSCard("dsTrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, dsTrooper);
        scn.MoveCardsToLSHand(reinforcements);
        scn.MoveCardsToTopOfLSLostPile(trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(2);

        assertTrue(scn.LSCardPlayAvailable(reinforcements, "Draw destiny"));
        scn.LSPlayCard(reinforcements, "Draw destiny");
        for (int i = 0; i < 24; i++) {
            if (reinforcements.getZone() == Zone.LOST_PILE || reinforcements.getZone() == Zone.TOP_OF_LOST_PILE) {
                if (!scn.LSDecisionAvailable("take into hand") && !scn.LSHasCardChoiceAvailable(trooper)) {
                    break;
                }
            }
            if (scn.LSDecisionAvailable("Choose value for X")) {
                scn.LSDecided(1);
                continue;
            }
            if (scn.LSDecisionAvailable("Lost Pile") || scn.LSDecisionAvailable("Viewing")) {
                scn.LSDismissRevealedCards();
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(trooper)) {
                scn.LSChooseCard(trooper);
                continue;
            }
            if (scn.LSDecisionAvailable("take into hand") || scn.LSDecisionAvailable("Choose a retrieved")) {
                scn.LSDecided("");
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
            scn.PassAllResponses();
        }

        scn.MoveCardsToTopOfLSLostPile(trooper2);
        scn.MoveCardsToLSHand(reinforcements2);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(reinforcements2, "Draw destiny"));
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
}
