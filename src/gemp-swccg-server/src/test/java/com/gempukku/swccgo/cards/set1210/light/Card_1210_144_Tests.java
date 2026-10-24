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

public class Card_1210_144_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("advance", "1210_144");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("uncertain", "4_151");
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

    protected VirtualTableScenario GetStartingScenario() {
        StartingSetup mboObjective = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("mbo", "111_4");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                for (int i = 0; i < 16; i++) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("yavin4"))) {
                        scn.LSChooseCard(scn.GetLSCard("yavin4"));
                        continue;
                    }
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("dockingbay"))) {
                        scn.LSChooseCard(scn.GetLSCard("dockingbay"));
                        continue;
                    }
                    if (scn.LSDecisionAvailable("On which side") || scn.LSDecisionAvailable("which side")) {
                        scn.LSChoose("Left");
                        continue;
                    }
                    break;
                }
            }
        };
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("advance", "1210_144");
                    put("yavin4", "1_135");
                    put("dockingbay", "1_136");
                    put("warroom", "1_139");
                    put("aimhigh", "8_34");
                    put("battleplan", "8_35");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                mboObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AdvancePreparationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("advance").getBlueprint();

        assertEquals("Advance Preparation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdvancePreparationUsedCancelsUncertainIsTheFuture() {
        var scn = GetScenario();
        var advance = scn.GetLSCard("advance");
        var uncertain = scn.GetDSCard("uncertain");

        scn.StartGame();
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        scn.MoveCardsToLSHand(advance);
        scn.MoveCardsToDSHand(uncertain);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(uncertain));
        scn.DSPlayCard(uncertain);
        boolean canceled = false;
        for (int i = 0; i < 24; i++) {
            if (hasCardActions(scn, false) && (scn.LSCardPlayAvailable(advance)
                    || scn.LSActionAvailable("Cancel Uncertain Is The Future"))) {
                if (scn.LSCardPlayAvailable(advance)) {
                    scn.LSPlayCard(advance);
                } else {
                    scn.LSChooseAction("Cancel Uncertain Is The Future");
                }
                scn.PassAllResponses();
                canceled = true;
                break;
            }
            if (payForce(scn, true) || payForce(scn, false)) {
                continue;
            }
            if (hasCardActions(scn, false) && scn.LSCardPlayAvailable(advance)) {
                continue;
            }
            if (resolveDecision(scn, true) || resolveDecision(scn, false)) {
                continue;
            }
            scn.PassAllResponses();
        }
        assertTrue(canceled);

        assertTrue(uncertain.getZone() == Zone.LOST_PILE || uncertain.getZone() == Zone.TOP_OF_LOST_PILE
                || uncertain.getZone() == Zone.HAND);
        assertTrue(advance.getZone() == Zone.USED_PILE || advance.getZone() == Zone.TOP_OF_USED_PILE);
        assertFalse(uncertain.getZone() == Zone.VOID);
    }

    @Test
    public void AdvancePreparationStartingDeploysMassassiWarRoomAndEffectsWhenMassassiBaseOperationsOnTable() {
        var scn = GetStartingScenario();
        var advance = scn.GetLSCard("advance");
        var warroom = scn.GetLSCard("warroom");
        var aimhigh = scn.GetLSCard("aimhigh");
        var battleplan = scn.GetLSCard("battleplan");
        var mbo = scn.GetLSCard("mbo");

        scn.StartGame();
        assertInZone(Zone.SIDE_OF_TABLE, mbo);
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSChooseCard(advance);
        scn.PassAllResponses();
        for (int i = 0; i < 24; i++) {
            if (advance.getZone() == Zone.HAND && warroom.getZone() == Zone.LOCATIONS
                    && aimhigh.getZone() == Zone.SIDE_OF_TABLE) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(warroom)) {
                scn.LSChooseCard(warroom);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(aimhigh)) {
                scn.LSChooseCard(aimhigh);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(battleplan)) {
                scn.LSChooseCard(battleplan);
                continue;
            }
            if (scn.LSDecisionAvailable("On which side") || scn.LSDecisionAvailable("which side")) {
                scn.LSChoose("Left");
                continue;
            }
            if (scn.LSDecisionAvailable("Choose card to deploy") && !scn.LSHasCardChoiceAvailable(warroom)
                    && !scn.LSHasCardChoiceAvailable(aimhigh) && !scn.LSHasCardChoiceAvailable(battleplan)) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.LOCATIONS, warroom.getZone());
        assertInZone(Zone.SIDE_OF_TABLE, aimhigh);
        assertInHand(advance);
    }

    @Test
    public void AdvancePreparationStartingIsNotOfferedWithoutMassassiBaseOperations() {
        var scn = GetScenario();
        var advance = scn.GetLSCard("advance");

        scn.StartGame();
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            assertFalse(scn.LSHasCardChoiceAvailable(advance));
            scn.LSPass();
        } else {
            assertFalse(scn.LSDecisionAvailable("Choose starting interrupt"));
        }
    }

    private boolean hasCardActions(VirtualTableScenario scn, boolean dark) {
        if (dark) {
            return scn.DSGetADParam("cardId") != null && scn.DSGetADParam("actionText") != null;
        }
        return scn.LSGetADParam("cardId") != null && scn.LSGetADParam("actionText") != null;
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
