package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_047_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("recall", "1102_47");
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
    public void TacticalReCallStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("recall").getBlueprint();

        assertEquals("Tactical Re-Call", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TacticalReCallDeploysOnOpponentsSiteCostingTwoForce() {
        var scn = GetScenario();
        var recall = scn.GetDSCard("recall");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(dsSite, leia);
        scn.MoveCardsToDSHand(recall);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);

        assertEquals(2, scn.GetDeployCost(recall));
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(recall));
        scn.DSPlayCard(recall);
        if (scn.DSHasCardChoiceAvailable(lsSite)) {
            scn.DSChooseCard(lsSite);
        }
        if (scn.DSHasCardChoiceAvailable(leia)) {
            scn.DSChooseCard(leia);
        }
        scn.PassAllResponses();

        assertEquals(lsSite, recall.getAttachedTo());
        assertEquals(force - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void TacticalReCallCancelsTargetLeadersGameText() {
        var scn = GetScenario();
        var recall = scn.GetDSCard("recall");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(dsSite, leia);
        scn.AttachCardsTo(lsSite, recall);
        recall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.leader);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
        assertInZone(Zone.ATTACHED, recall);
    }

    @Test
    public void TacticalReCallIsCanceledWhenReachedByTarget() {
        var scn = GetScenario();
        var recall = scn.GetDSCard("recall");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(dsSite, leia);
        scn.AttachCardsTo(lsSite, recall);
        recall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.leader);

        scn.MoveCardsToLocation(lsSite, leia);
        try {
            scn.SkipToLSTurn(Phase.CONTROL);
        }
        catch (RuntimeException ignored) {
        }
        completeTacticalReCallCancel(scn);

        assertInZone(Zone.LOST_PILE, recall);
    }

    @Test
    public void TacticalReCallDoesNotCancelUntilReachedByTarget() {
        var scn = GetScenario();
        var recall = scn.GetDSCard("recall");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(dsSite, leia);
        scn.AttachCardsTo(lsSite, recall);
        recall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.leader);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();

        assertInZone(Zone.ATTACHED, recall);
        assertEquals(lsSite, recall.getAttachedTo());
        assertEquals(dsSite, leia.getAtLocation());
    }

    /**
     * MoveCardsToLocation does not emit TABLE_CHANGED. Advance until START_OF_PHASE/TURN
     * fires the required cancel. Do not call ActionAvailable when pending is null (that throws).
     */
    private boolean chooseCancelIfOffered(VirtualTableScenario scn, String player) {
        if (scn.GetAwaitingDecision(player) == null) {
            return false;
        }
        String[] texts = scn.GetADParam(player, "actionText");
        if (texts == null) {
            return false;
        }
        for (String text : texts) {
            if (text != null && text.toLowerCase().contains("cancel")) {
                if (player.equals(scn.DS)) {
                    scn.DSChooseAction("Cancel");
                }
                else {
                    scn.LSChooseAction("Cancel");
                }
                return true;
            }
        }
        return false;
    }

    private void completeTacticalReCallCancel(VirtualTableScenario scn) {
        for (int i = 0; i < 24; i++) {
            if (chooseCancelIfOffered(scn, scn.DS) || chooseCancelIfOffered(scn, scn.LS)) {
                continue;
            }
            if (scn.AwaitingDSActivatePhaseActions()) {
                scn.DSActivateMaxForceAndPass();
                continue;
            }
            if (scn.AwaitingLSActivatePhaseActions()) {
                scn.LSActivateMaxForceAndPass();
                continue;
            }
            if (scn.GetAwaitingDecision(scn.DS) != null || scn.GetAwaitingDecision(scn.LS) != null) {
                scn.PassAllResponses();
                continue;
            }
            if (scn.AwaitingDSControlPhaseActions() || scn.AwaitingLSControlPhaseActions()) {
                break;
            }
            if (scn.GetAwaitingDecision(scn.DS) != null) {
                scn.DSPass();
            }
            else if (scn.GetAwaitingDecision(scn.LS) != null) {
                scn.LSPass();
            }
            else {
                break;
            }
        }
        chooseCancelIfOffered(scn, scn.DS);
        chooseCancelIfOffered(scn, scn.LS);
        scn.PassAllResponses();
    }
}
