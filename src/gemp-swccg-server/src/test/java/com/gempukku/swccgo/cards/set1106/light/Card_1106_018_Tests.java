package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1106_018_Tests {
    private static final StartingSetup LsHiddenBase = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenBase", "7_136");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetLSCard("hiddenBase"))) {
                scn.LSChooseCard(scn.GetLSCard("hiddenBase"));
            }
            for (int i = 0; i < 30; i++) {
                if (scn.AwaitingDSActivatePhaseActions()) {
                    return;
                }
                if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetLSCard("rendezvous"))) {
                    scn.LSChooseCard(scn.GetLSCard("rendezvous"));
                    continue;
                }
                if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    continue;
                }
                if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetLSCard("hothsys"))) {
                    scn.LSChooseCard(scn.GetLSCard("hothsys"));
                    continue;
                }
                if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetLSCard("yavin"))) {
                    scn.LSChooseCard(scn.GetLSCard("yavin"));
                    continue;
                }
                if (scn.LSDecisionAvailable("You have not activated Force")) {
                    scn.LSChooseYes();
                    continue;
                }
                if (scn.DSDecisionAvailable("You have not activated Force")) {
                    scn.DSChooseYes();
                    continue;
                }
                if (scn.LSGetDecision() != null && scn.LSGetADParam("results") != null) {
                    scn.LSDecided("0");
                    continue;
                }
                if (scn.DSGetDecision() != null && scn.DSGetADParam("results") != null) {
                    scn.DSDecided("0");
                    continue;
                }
                String dsText = scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                        ? scn.DSGetDecision().getText().toLowerCase() : "";
                String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                        ? scn.LSGetDecision().getText().toLowerCase() : "";
                if (dsText.contains("verify") || dsText.contains("pass") || dsText.contains("optional")) {
                    scn.DSPass();
                    continue;
                }
                if (lsText.contains("verify") || lsText.contains("pass") || lsText.contains("optional")) {
                    scn.LSPass();
                    continue;
                }
                break;
            }
            assertTrue("Hidden Base start did not reach DS activate; DS="
                    + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : "null")
                    + " LS=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : "null"),
                    scn.AwaitingDSActivatePhaseActions());
        }
    };

    private static final StartingSetup LsHiddenBaseOutside = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("yavin", "1_135");
                put("hothsys", "3_55");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("planners", "1106_18");
                    put("starting", "13_5");
                    put("tatooine", "1_127");
                    put("rendezvous", "7_123");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                LsHiddenBase,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                LsHiddenBaseOutside,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetScenarioWithoutHiddenBase() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("planners", "1106_18");
                    put("starting", "13_5");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void RebelPlannersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenarioWithoutHiddenBase();
        var card = scn.GetLSCard("planners").getBlueprint();

        assertEquals("Rebel Planners", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void RebelPlannersMayNotBeCanceled() {
        var scn = GetScenarioWithoutHiddenBase();
        var planners = scn.GetLSCard("planners");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(planners);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), planners));
    }

    @Test
    public void RebelPlannersDoesNothingWithoutHiddenBase() {
        var scn = GetScenarioWithoutHiddenBase();
        var planners = scn.GetLSCard("planners");
        var starting = scn.GetLSCard("starting");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(starting, planners);
        scn.StackCardsOn(starting, tatooine);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(planners, "Deploy system from under Starting Effect"));
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null) {
            assertFalse(scn.LSCardActionAvailable(planners, "Exchange Hidden Base indicator"));
        }
    }

    @Test
    public void RebelPlannersDeploysASystemFromUnderStartingEffectOncePerTurn() {
        var scn = GetScenario();
        var planners = scn.GetLSCard("planners");
        var starting = scn.GetLSCard("starting");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(starting, planners);
        scn.StackCardsOn(starting, tatooine);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(planners, "Deploy system from under Starting Effect"));
        scn.LSUseCardAction(planners, "Deploy system from under Starting Effect");
        for (int i = 0; i < 12 && !tatooine.getZone().isInPlay(); i++) {
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(tatooine)) { scn.LSChooseCard(tatooine); continue; }
            if (scn.LSDecisionAvailable("On which side")) { scn.LSChoose("Left"); continue; }
            if (scn.DSGetDecision() != null) { scn.DSPass(); continue; }
            if (scn.LSGetDecision() != null && !scn.AwaitingLSDeployPhaseActions()) { scn.LSPass(); continue; }
            break;
        }

        assertTrue(tatooine.getZone().isInPlay());
        assertFalse(scn.gameState().getStackedCards(starting).contains(tatooine));
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null) {
            assertFalse(scn.LSCardActionAvailable(planners, "Deploy system from under Starting Effect"));
        }
    }

    @Test
    public void RebelPlannersExchangesHiddenBaseIndicatorWithAParsecSystemOncePerGame() {
        var scn = GetScenario();
        var planners = scn.GetLSCard("planners");
        var starting = scn.GetLSCard("starting");
        var hiddenBase = scn.GetLSCard("hiddenBase");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(starting, planners);
        scn.StackCardsFaceDownOn(starting, tatooine);
        var indicator = scn.gameState().getStackedCards(hiddenBase).iterator().next();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(planners, "Exchange Hidden Base indicator"));
        scn.LSUseCardAction(planners, "Exchange Hidden Base indicator");
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(tatooine)) {
            scn.LSChooseCard(tatooine);
        }
        if (scn.LSGetDecision() != null || scn.DSGetDecision() != null) {
            scn.PassAllResponses();
        }

        assertTrue(scn.gameState().getStackedCards(hiddenBase).contains(tatooine));
        assertTrue(scn.gameState().getStackedCards(starting).contains(indicator));
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null) {
            assertFalse(scn.LSCardActionAvailable(planners, "Exchange Hidden Base indicator"));
        }
    }

    private void passStartingInterrupts(VirtualTableScenario scn) {
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
    }

    private void resolvePendingChoices(VirtualTableScenario scn) {
        for (int i = 0; i < 16; i++) {
            if (scn.AwaitingLSDeployPhaseActions() && scn.LSGetADParam("results") == null) {
                return;
            }
            if (scn.DSDecisionAvailable("You have not activated Force")) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.LSDecisionAvailable("You have not activated Force")) {
                scn.LSChooseYes();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetADParam("results") != null) {
                scn.DSDecided("0");
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetADParam("results") != null) {
                scn.LSDecided("0");
                continue;
            }
            String dsText = scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    ? scn.DSGetDecision().getText().toLowerCase() : "";
            String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    ? scn.LSGetDecision().getText().toLowerCase() : "";
            if (dsText.contains("pass") || dsText.contains("optional") || dsText.contains("verify")) {
                scn.DSPass();
                continue;
            }
            if (lsText.contains("pass") || lsText.contains("optional") || lsText.contains("verify")) {
                scn.LSPass();
                continue;
            }
            break;
        }
    }
}
