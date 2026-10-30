package com.gempukku.swccgo.cards.set1103.light;

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

public class Card_1103_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tremor", "1103_1");
                }},
                new HashMap<>() {{
                    put("signal", "6_160");
                    put("podracer", "11_96");
                }},
                20, 20,
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
    public void ATremorInTheForceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tremor").getBlueprint();

        assertEquals("A Tremor In The Force", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ATremorInTheForceDeploysOnTable() {
        var scn = GetScenario();
        var tremor = scn.GetLSCard("tremor");

        scn.StartGame();
        scn.MoveCardsToLSHand(tremor);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(3);

        assertTrue(scn.LSCardPlayAvailable(tremor));
        scn.LSPlayCard(tremor);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, tremor);
    }

    @Test
    public void ATremorInTheForceRetrievesOneForceAfterVerifyingOpponentsReserveDeck() {
        var scn = GetScenario();
        var tremor = scn.GetLSCard("tremor");
        var lost = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(tremor);
        scn.MoveCardsToTopOfLSLostPile(lost);
        int lostBefore = scn.GetLSLostPileCount();
        int usedBefore = scn.GetLSUsedPileCount();

        dsFailsOwnReserveSearchSoLsVerifies(scn);
        chooseLsOptionalAction(scn, "Retrieve 1 Force");
        scn.PassAllResponses();

        assertEquals(lostBefore - 1, scn.GetLSLostPileCount());
        assertEquals(usedBefore + 1, scn.GetLSUsedPileCount());
        assertInZone(Zone.USED_PILE, lost);
    }

    @Test
    public void ATremorInTheForcePlacesAPodracerGameTextCardOutOfPlayFromOpponentsReserveDeck() {
        var scn = GetScenario();
        var tremor = scn.GetLSCard("tremor");
        var podracer = scn.GetDSCard("podracer");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(tremor);

        dsFailsOwnReserveSearchSoLsVerifies(scn);
        scn.MoveCardsToTopOfDSReserveDeck(podracer);
        chooseLsOptionalAction(scn, "Place podracer card out of play");
        for (int i = 0; i < 10; i++) {
            if (podracer.getZone() == Zone.OUT_OF_PLAY) {
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(podracer)) {
                scn.LSChooseCard(podracer);
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        scn.PassAllResponses();

        assertEquals(Zone.OUT_OF_PLAY, podracer.getZone());
    }

    private void dsFailsOwnReserveSearchSoLsVerifies(VirtualTableScenario scn) {
        var signal = scn.GetDSCard("signal");
        scn.MoveCardsToDSHand(signal);
        passStartingInterrupts(scn);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.CONTROL);
        if (scn.LSGetDecision() != null && !lsHasCardActions(scn)) {
            scn.LSPass();
        }
        scn.EnsureDSForcePile(3);
        assertTrue(scn.DSPlayUsedInterruptAvailable(signal) || scn.DSCardPlayAvailable(signal));
        if (scn.DSPlayUsedInterruptAvailable(signal)) {
            scn.DSPlayUsedInterrupt(signal);
        } else {
            scn.DSPlayCard(signal);
        }
        scn.PassCardAndForceUseResponses();
        for (int i = 0; i < 24; i++) {
            if (lsHasTremorOptional(scn)) {
                break;
            }
            if (scn.DSDecisionAvailable("Verify")) {
                scn.DSPass();
                continue;
            }
            if (scn.LSDecisionAvailable("Verify")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && !lsHasTremorOptional(scn)
                    && (scn.LSDecisionAvailable("Playing") || scn.LSDecisionAvailable("USE_FORCE")
                    || scn.LSDecisionAvailable("Just looked"))) {
                scn.LSPass();
                continue;
            }
            break;
        }
    }

    private void chooseLsOptionalAction(VirtualTableScenario scn, String text) {
        PhysicalCardImpl tremor = scn.GetLSCard("tremor");
        for (int i = 0; i < 20; i++) {
            if (lsHasCardActions(scn)) {
                try {
                    if (scn.LSCardActionAvailable(tremor, text)) {
                        scn.LSUseCardAction(tremor, text);
                        return;
                    }
                    var actions = scn.LSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains(text.toLowerCase())) {
                                scn.LSChooseAction(text);
                                return;
                            }
                        }
                    }
                } catch (IllegalStateException ignored) {
                    // Pile chooser is not a card-action window.
                }
            }
            if (decisionText(scn, true).toLowerCase().contains(text.toLowerCase())) {
                if (scn.LSChoiceAvailable("Yes")) {
                    scn.LSChooseYes();
                    return;
                }
                scn.LSChooseAction(text);
                return;
            }
            if (scn.DSDecisionAvailable("Verify")) {
                scn.DSPass();
                continue;
            }
            if (scn.LSDecisionAvailable("Verify")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && !lsHasTremorOptional(scn)) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(lsHasTremorOptional(scn) || lsHasCardActions(scn));
        if (lsHasCardActions(scn) && scn.LSCardActionAvailable(tremor, text)) {
            scn.LSUseCardAction(tremor, text);
        } else if (scn.LSChoiceAvailable("Yes")) {
            scn.LSChooseYes();
        } else {
            scn.LSChooseAction(text);
        }
    }

    private boolean lsHasTremorOptional(VirtualTableScenario scn) {
        if (lsHasCardActions(scn)) {
            PhysicalCardImpl tremor = scn.GetLSCard("tremor");
            try {
                return scn.LSCardActionAvailable(tremor, "Retrieve")
                        || scn.LSCardActionAvailable(tremor, "Place podracer")
                        || scn.LSCardActionAvailable(tremor, "podracer");
            } catch (IllegalStateException ignored) {
                return false;
            }
        }
        String text = decisionText(scn, true).toLowerCase();
        return text.contains("retrieve") || text.contains("podracer") || text.contains("tremor");
    }

    private String decisionText(VirtualTableScenario scn, boolean light) {
        var decision = light ? scn.LSGetDecision() : scn.DSGetDecision();
        return decision != null && decision.getText() != null ? decision.getText() : "";
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }

    private void passStartingInterrupts(VirtualTableScenario scn) {
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
    }
}
