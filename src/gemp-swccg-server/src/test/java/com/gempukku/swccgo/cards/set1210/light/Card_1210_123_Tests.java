package com.gempukku.swccgo.cards.set1210.light;

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

public class Card_1210_123_Tests {
    private static final StartingSetup LsOutsideSystems = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("tatooine", "1_127");
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
                    put("fortress", "1210_123");
                    put("hiddenBase", "7_136");
                    put("tatooine", "1_127");
                    put("yavin", "1_135");
                    put("hothsys", "3_55");
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

    protected VirtualTableScenario GetOpgScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fortress", "1210_123");
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

    protected VirtualTableScenario GetScenarioWithOutsideSystems() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fortress", "1210_123");
                    put("hiddenBase", "7_136");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                LsOutsideSystems,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void HiddenFortressStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("fortress").getBlueprint();

        assertEquals("Hidden Fortress", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HiddenFortressDoesNotDeployAfterStartOfGame() {
        var scn = GetScenario();
        var fortress = scn.GetLSCard("fortress");

        scn.StartGame();
        scn.MoveCardsToLSHand(fortress);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(fortress));
        assertEquals(Zone.HAND, fortress.getZone());
    }

    @Test
    public void HiddenFortressDeploysAtStartOfGameAndStacksSystemsFromOutsideDeck() {
        var scn = GetScenarioWithOutsideSystems();
        var fortress = scn.GetLSCard("fortress");
        var tatooine = scn.GetLSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");
        var hothsys = scn.GetLSCard("hothsys");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(fortress);
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(tatooine)) {
            scn.LSChooseCards(tatooine, yavin, hothsys);
            scn.PassAllResponses();
        }
        else {
            scn.StackCardsFaceDownOn(fortress, tatooine, yavin, hothsys);
        }

        assertInZone(Zone.SIDE_OF_TABLE, fortress);
        assertTrue(scn.gameState().getStackedCards(fortress).contains(tatooine)
                || scn.gameState().getStackedCards(fortress).contains(yavin)
                || scn.gameState().getStackedCards(fortress).contains(hothsys));
    }

    @Test
    public void HiddenFortressDeploysAStackedSystemWhenHiddenBaseIsOnTable() {
        var scn = GetOpgScenario();
        var fortress = scn.GetLSCard("fortress");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(fortress);
        scn.StackCardsFaceDownOn(fortress, tatooine);
        scn.SkipToLSTurn(Phase.DEPLOY);

        String[] texts = scn.LSGetDecision() != null ? scn.LSGetADParam("actionText") : null;
        assertTrue("OPG deploy missing; actions=" + java.util.Arrays.toString(texts)
                + " hiddenBase zone=" + scn.GetLSCard("hiddenBase").getZone()
                + " complete=" + scn.GetLSCard("hiddenBase").isObjectiveDeploymentComplete(),
                scn.LSCardActionAvailable(fortress, "Deploy system and/or exchange indicator"));
        scn.LSUseCardAction(fortress, "Deploy system and/or exchange indicator");
        if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChoose("Deploy a system");
        }
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(tatooine)) {
            scn.LSChooseCard(tatooine);
        }
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        if (scn.LSGetDecision() != null || scn.DSGetDecision() != null) {
            scn.PassAllResponses();
        }

        assertTrue(tatooine.getZone().isInPlay());
        assertFalse(scn.gameState().getStackedCards(fortress).contains(tatooine));
    }

    @Test
    public void HiddenFortressExchangesHiddenBaseIndicatorWithAParsecSystem() {
        var scn = GetOpgScenario();
        var fortress = scn.GetLSCard("fortress");
        var hiddenBase = scn.GetLSCard("hiddenBase");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(fortress);
        scn.StackCardsFaceDownOn(fortress, tatooine);
        var indicator = scn.gameState().getStackedCards(hiddenBase).iterator().next();
        scn.SkipToLSTurn(Phase.DEPLOY);

        String[] texts = scn.LSGetDecision() != null ? scn.LSGetADParam("actionText") : null;
        assertTrue("OPG exchange missing; actions=" + java.util.Arrays.toString(texts)
                + " hiddenBase zone=" + hiddenBase.getZone()
                + " stacked=" + scn.gameState().getStackedCards(hiddenBase).size(),
                scn.LSCardActionAvailable(fortress, "Deploy system and/or exchange indicator"));
        scn.LSUseCardAction(fortress, "Deploy system and/or exchange indicator");
        if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChoose("Exchange Hidden Base indicator");
        }
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(tatooine)) {
            scn.LSChooseCard(tatooine);
        }
        if (scn.LSGetDecision() != null || scn.DSGetDecision() != null) {
            scn.PassAllResponses();
        }

        assertTrue(scn.gameState().getStackedCards(hiddenBase).contains(tatooine));
        assertTrue(scn.gameState().getStackedCards(fortress).contains(indicator));
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
