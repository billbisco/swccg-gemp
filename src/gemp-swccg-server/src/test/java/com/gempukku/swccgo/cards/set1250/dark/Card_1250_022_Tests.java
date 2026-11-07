package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_022_Tests {
    private static final String DRAW = "Draw 2 cards from Reserve Deck";
    private static final String RELOCATE = "Relocate to a Rebel or Jedi at a battleground";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "luke", "1_19", "droid", "1_5")),
                new HashMap<>(Map.of("applause", "1250_22", "sidious", "1116_46", "senate", "12_167",
                        "ice", "3_148", "vader", "1_168", "saber", "1_324", "sniper", "2_139",
                        "draw1", "1_194", "draw2", "1_194")),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("senate"));
        scn.MoveLocationToTable(scn.GetDSCard("ice"));
        scn.MoveCardsToDSHand(scn.GetDSCard("applause"));
        return scn;
    }

    @Test public void WithThunderousApplausePrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("applause").getBlueprint();
        assertEquals("With Thunderous Applause", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("With enough political support, any number of devious plans can be put into motion.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.CORUSCANT, Icon.SIDIOUS,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void WithThunderousApplauseActuallyDeploysOnlyOnSidious() {
        var scn = scenario();
        var effect = scn.GetDSCard("applause");
        var sidious = scn.GetDSCard("sidious");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(effect);
        if (scn.DSHasCardChoiceAvailable(sidious)) {
            assertFalse(scn.DSHasCardChoiceAvailable(vader));
            scn.DSChooseCard(sidious);
        }
        scn.PassAllResponses();
        assertSame(sidious, effect.getAttachedTo());
    }

    @Test public void WithThunderousApplauseMayDrawTwoActualReserveCardsAtOwnTurnStartAtSenate() {
        drawAtStart(true, true);
        drawAtStart(true, false);
        drawAtStart(false, false);
    }

    private void drawAtStart(boolean atSenate, boolean accept) {
        var scn = scenario();
        var effect = scn.GetDSCard("applause");
        var sidious = scn.GetDSCard("sidious");
        var first = scn.GetDSCard("draw1");
        var second = scn.GetDSCard("draw2");
        scn.SkipToLSTurn(Phase.DRAW);
        scn.MoveCardsToLocation(atSenate ? scn.GetDSCard("senate") : scn.GetDSStartingLocation(), sidious);
        scn.AttachCardsTo(sidious, effect);
        scn.MoveCardsToTopOfDSReserveDeck(first, second);
        int handBefore = scn.GetDSHandCount();
        scn.PassDrawActions();
        scn.PassResponses("RECIRCULATED");
        boolean offered = false;
        for (int i = 0; i < 35 && !scn.AwaitingDSActivatePhaseActions(); i++) {
            if (scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.DSCardActionAvailable(effect, DRAW)) {
                offered = true;
                if (accept) scn.DSUseCardAction(effect, DRAW); else scn.DSPass();
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected start-of-turn decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        assertEquals(atSenate, offered);
        assertEquals(accept ? 2 : 0, scn.GetDSHandCount() - handBefore);
        assertInZone(accept ? Zone.HAND : Zone.RESERVE_DECK, first);
        assertInZone(accept ? Zone.HAND : Zone.RESERVE_DECK, second);
    }

    @Test public void WithThunderousApplauseRelocatesOnceAndRemovesOnlyAttritionImmunity() {
        var scn = scenario();
        var effect = scn.GetDSCard("applause");
        var sidious = scn.GetDSCard("sidious");
        var obi = scn.GetLSCard("obi");
        var luke = scn.GetLSCard("luke");
        var droid = scn.GetLSCard("droid");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.AttachCardsTo(sidious, effect);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), obi, luke, droid);
        var q = scn.game().getModifiersQuerying();
        var state = scn.gameState();
        float power = q.getPower(state, obi);
        float forfeit = q.getForfeit(state, obi);
        assertTrue(q.hasAnyImmunityToAttrition(state, obi));
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(effect, RELOCATE));
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSCardActionAvailable(effect, RELOCATE));
        scn.DSUseCardAction(effect, RELOCATE);
        assertTrue(scn.DSHasCardChoiceAvailable(obi));
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        assertFalse(scn.DSHasCardChoiceAvailable(droid));
        scn.DSChooseCard(obi);
        scn.PassAllResponses();
        if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
        assertSame(obi, effect.getAttachedTo());
        assertFalse(q.hasAnyImmunityToAttrition(state, obi));
        assertEquals(power, q.getPower(state, obi), scn.epsilon);
        assertEquals(forfeit, q.getForfeit(state, obi), scn.epsilon);
        assertFalse(scn.DSCardActionAvailable(effect, RELOCATE));
    }

    @Test public void WithThunderousApplauseMustRelocateAtEndOfMoveButOnlyAtBattleground() {
        for (boolean battleground : List.of(false, true)) {
            var scn = scenario();
            var effect = scn.GetDSCard("applause");
            var sidious = scn.GetDSCard("sidious");
            var luke = scn.GetLSCard("luke");
            scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
            scn.AttachCardsTo(sidious, effect);
            scn.MoveCardsToLocation(battleground ? scn.GetDSStartingLocation() : scn.GetDSCard("ice"), luke);
            scn.SkipToPhase(Phase.MOVE);
            assertEquals(battleground, scn.DSCardActionAvailable(effect, RELOCATE));
            scn.DSPass();
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            for (int i = 0; i < 35 && !scn.AwaitingDSDrawPhaseActions(); i++) {
                if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Move end decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSDrawPhaseActions());
            assertSame(battleground ? luke : sidious, effect.getAttachedTo());
        }
    }

    @Test public void WithThunderousApplauseRelocatesAfterActualLossOnlyIfSidiousOnTable() {
        for (boolean sidiousOnTable : List.of(false, true)) {
            var scn = scenario();
            var effect = scn.GetDSCard("applause");
            var sidious = scn.GetDSCard("sidious");
            var vader = scn.GetDSCard("vader");
            var luke = scn.GetLSCard("luke");
            var saber = scn.GetDSCard("saber");
            var sniper = scn.GetDSCard("sniper");
            if (sidiousOnTable) scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, luke);
            scn.AttachCardsTo(luke, effect);
            scn.AttachCardsTo(vader, saber);
            scn.MoveCardsToDSHand(sniper);
            scn.EnsureDSForcePile(4);
            scn.SkipToPhase(Phase.CONTROL);
            scn.PrepareDSDestiny(5);
            scn.PrepareDSDestiny(6);
            scn.DSPlayCard(sniper);
            for (int i = 0; i < 90 && !scn.AwaitingDSControlPhaseActions(); i++) {
                if (scn.DSHasCardChoiceAvailable(saber)) scn.DSChooseCard(saber);
                else if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
                else if (scn.DSHasCardChoiceAvailable(effect)) scn.DSChooseCard(effect);
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Sniper or relocation decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSControlPhaseActions());
            assertInZone(Zone.LOST_PILE, luke);
            if (sidiousOnTable) assertSame(sidious, effect.getAttachedTo());
            else assertInZone(Zone.LOST_PILE, effect);
        }
    }
}
