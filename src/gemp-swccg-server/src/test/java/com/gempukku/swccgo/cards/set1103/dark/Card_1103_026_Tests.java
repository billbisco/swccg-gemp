package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("signal", "6_77");
                    put("podracer", "11_47");
                }},
                new HashMap<>() {{
                    put("disturbance", "1103_26");
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
    public void ADisturbanceInTheForceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("disturbance").getBlueprint();

        assertEquals("A Disturbance In The Force", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void ADisturbanceInTheForceDeploysOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>() {{
                    put("disturbance", "1103_26");
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
        var disturbance = scn.GetDSCard("disturbance");

        scn.StartGame();
        scn.MoveCardsToDSHand(disturbance);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(disturbance));
        scn.DSPlayCard(disturbance);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, disturbance);
    }

    @Test
    public void ADisturbanceInTheForceRetrievesOneForceAfterVerifyingOpponentsReserveDeck() {
        var scn = GetScenario();
        var disturbance = scn.GetDSCard("disturbance");
        var lost = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(disturbance);
        scn.MoveCardsToTopOfDSLostPile(lost);
        int lostBefore = scn.GetDSLostPileCount();
        int usedBefore = scn.GetDSUsedPileCount();

        lsFailsOwnReserveSearchSoDsVerifies(scn);
        chooseDsOptionalAction(scn, "Retrieve 1 Force");
        scn.PassAllResponses();

        assertEquals(lostBefore - 1, scn.GetDSLostPileCount());
        assertEquals(usedBefore + 1, scn.GetDSUsedPileCount());
        assertInZone(Zone.USED_PILE, lost);
    }

    @Test
    public void ADisturbanceInTheForcePlacesAPodracerGameTextCardOutOfPlayFromOpponentsReserveDeck() {
        var scn = GetScenario();
        var disturbance = scn.GetDSCard("disturbance");
        var podracer = scn.GetLSCard("podracer");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(disturbance);

        lsFailsOwnReserveSearchSoDsVerifies(scn);
        scn.MoveCardsToTopOfLSReserveDeck(podracer);
        chooseDsOptionalAction(scn, "Place podracer card out of play");
        for (int i = 0; i < 10; i++) {
            if (podracer.getZone() == Zone.OUT_OF_PLAY) {
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(podracer)) {
                scn.DSChooseCard(podracer);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        scn.PassAllResponses();

        assertEquals(Zone.OUT_OF_PLAY, podracer.getZone());
    }

    private void lsFailsOwnReserveSearchSoDsVerifies(VirtualTableScenario scn) {
        var signal = scn.GetLSCard("signal");
        scn.MoveCardsToLSHand(signal);
        passStartingInterrupts(scn);
        scn.SkipToLSTurn(Phase.CONTROL);
        if (scn.DSGetDecision() != null && !dsHasCardActions(scn)) {
            scn.DSPass();
        }
        scn.EnsureLSForcePile(1);
        assertTrue(scn.LSPlayUsedInterruptAvailable(signal) || scn.LSCardPlayAvailable(signal));
        if (scn.LSPlayUsedInterruptAvailable(signal)) {
            scn.LSPlayUsedInterrupt(signal);
        } else {
            scn.LSPlayCard(signal);
        }
        scn.PassCardAndForceUseResponses();
        for (int i = 0; i < 24; i++) {
            if (dsHasDisturbanceOptional(scn)) {
                break;
            }
            if (scn.LSDecisionAvailable("Verify")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("Verify")) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasDisturbanceOptional(scn)) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }

    private void chooseDsOptionalAction(VirtualTableScenario scn, String text) {
        PhysicalCardImpl disturbance = scn.GetDSCard("disturbance");
        for (int i = 0; i < 20; i++) {
            if (dsHasCardActions(scn)) {
                try {
                    if (scn.DSCardActionAvailable(disturbance, text)) {
                        scn.DSUseCardAction(disturbance, text);
                        return;
                    }
                    var actions = scn.DSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains(text.toLowerCase())) {
                                scn.DSChooseAction(text);
                                return;
                            }
                        }
                    }
                } catch (IllegalStateException ignored) {
                }
            }
            String dt = decisionText(scn);
            if (dt.toLowerCase().contains(text.toLowerCase())) {
                if (scn.DSChoiceAvailable("Yes")) {
                    scn.DSChooseYes();
                    return;
                }
                scn.DSChooseAction(text);
                return;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasDisturbanceOptional(scn)) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(dsHasDisturbanceOptional(scn) || dsHasCardActions(scn));
        if (dsHasCardActions(scn) && scn.DSCardActionAvailable(disturbance, text)) {
            scn.DSUseCardAction(disturbance, text);
        } else if (scn.DSChoiceAvailable("Yes")) {
            scn.DSChooseYes();
        } else {
            scn.DSChooseAction(text);
        }
    }

    private boolean dsHasDisturbanceOptional(VirtualTableScenario scn) {
        if (dsHasCardActions(scn)) {
            PhysicalCardImpl disturbance = scn.GetDSCard("disturbance");
            try {
                return scn.DSCardActionAvailable(disturbance, "Retrieve")
                        || scn.DSCardActionAvailable(disturbance, "Place podracer")
                        || scn.DSCardActionAvailable(disturbance, "podracer");
            } catch (IllegalStateException ignored) {
                return false;
            }
        }
        String text = decisionText(scn).toLowerCase();
        return text.contains("retrieve") || text.contains("podracer") || text.contains("disturbance");
    }

    private String decisionText(VirtualTableScenario scn) {
        var decision = scn.DSGetDecision();
        return decision != null && decision.getText() != null ? decision.getText() : "";
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
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
