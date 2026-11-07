package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_011_Tests {
    private static final String TAKE = "Take Rise, My Friend into hand from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "9_24", "ben", "1_21", "alter", "1_71", "escape", "11_36")),
                new HashMap<>(Map.of("effect", "1262_11", "rise", "9_140", "vader", "1_168", "emperor", "9_109",
                        "trooper", "1_194", "maul", "11_54")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void SoBeItJediPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("So Be It, Jedi", b.getTitle());
        assertEquals("\"Now, young Skywalker ... you will die.\"", b.getLore());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void SoBeItJediActuallyDeploysAndTakesRiseMyFriendOnlyOncePerGame() {
        var scn = scenario();
        var effect = scn.GetDSCard("effect");
        var rise = scn.GetDSCard("rise");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToDSHand(effect);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(effect);
        settleDeploy(scn);
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        scn.MoveCardsToTopOfDSReserveDeck(rise, trooper);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(rise);
        scn.gameState().getRandom().setSeed(4096);
        scn.DSUseCardAction(effect, TAKE);
        int index = scn.DSGetBPChoices().indexOf(trooper.getBlueprintId(true));
        assertTrue(index >= 0);
        assertEquals("false", scn.GetADParam(scn.DS, "selectable")[index]);
        scn.DSChooseCard(rise);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, rise);
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());
        scn.MoveCardsToTopOfDSReserveDeck(rise);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(effect, TAKE));
    }

    @Test public void SoBeItJediActuallyStacksAfterOpponentsAlterOnlyIfRebellionIsOnTable() {
        for (boolean rebellionOnTable : List.of(false, true)) {
            var scn = scenario();
            var effect = scn.GetDSCard("effect");
            var rebellion = scn.GetDSCard("rebellion");
            var alter = scn.GetLSCard("alter");
            var ben = scn.GetLSCard("ben");
            scn.MoveCardsToDSSideOfTable(effect);
            if (!rebellionOnTable) scn.MoveCardsToDSHand(rebellion);
            scn.MoveCardsToLSHand(alter);
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ben);
            scn.SkipToLSTurn(Phase.CONTROL);
            scn.PrepareLSDestiny(1);
            scn.LSPlayCard(alter, "cancel Effect");
            assertTrue(scn.LSHasCardChoiceAvailable(effect));
            scn.LSChooseCard(effect);
            assertTrue(scn.LSHasCardChoiceAvailable(ben));
            scn.LSChooseCard(ben);
            scn.PassAllResponses();
            assertInZone(rebellionOnTable ? Zone.STACKED : Zone.LOST_PILE, effect);
            if (rebellionOnTable) assertSame(rebellion, effect.getStackedOn());
        }
    }

    @Test public void SoBeItJediBlocksRealCancellationOfAnActualObjectiveDuel() {
        for (boolean effectOnTable : List.of(false, true)) {
            var scn = scenario();
            var objective = scn.GetDSCard("bhbm");
            var throne = scn.GetDSCard("throne");
            var luke = scn.GetLSCard("luke");
            var escape = scn.GetLSCard("escape");
            scn.MoveCardsToLocation(throne, scn.GetDSCard("vader"), scn.GetDSCard("emperor"), luke);
            if (effectOnTable) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
            scn.SkipToPhase(Phase.CONTROL);
            assertTrue(objective.isFlipped());
            assertTrue("The front of the Objective really captured Luke", luke.isCaptive());
            scn.MoveCardsToLSHand(escape);
            scn.ApplyAdHocAction(new AbstractActionProxy() {
                @Override
                public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result) {
                    if (!scn.LS.equals(playerId) || result.getType() != EffectResult.Type.DUEL_INITIATED) return null;
                    var action = new OptionalGameTextTriggerAction(escape, playerId, escape.getCardId());
                    action.setText("Attempt to cancel duel");
                    action.appendEffect(new CancelDuelEffect(action));
                    return List.of(action);
                }
            });
            scn.DSChooseAction("Initiate a Luke/Vader duel");
            for (int i = 0; i < 25 && !offeredCancel(scn, escape, "Attempt to cancel duel"); i++) {
                if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else fail("Unexpected Objective duel decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(offeredCancel(scn, escape, "Attempt to cancel duel"));
            var duel = scn.gameState().getDuelState();
            assertSame(objective, duel.getCardInitiatedDuel());
            // Standard cancellation at the live response. Jedi Escape's printed action
            // is unavailable against captive Luke regardless of So Be It, Jedi.
            scn.LSUseCardAction(escape, "Attempt to cancel duel");
            assertEquals(!effectOnTable, duel.isCanceled());
        }
    }
    @Test public void SoBeItJediStillAllowsActualJediEscapeAgainstMaulsDuel() {
        var scn = scenario();
        var maul = scn.GetDSCard("maul");
        var ben = scn.GetLSCard("ben");
        var escape = scn.GetLSCard("escape");
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        scn.MoveCardsToLSHand(escape);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), maul, ben);
        scn.EnsureLSForcePile(2);
        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(maul, "Duel a Jedi");
        for (int i = 0; i < 40 && !offeredCancel(scn, escape, "Cancel duel"); i++) {
            if (scn.AwaitingDSForceLossPayment()) scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            else if (scn.DSHasCardChoiceAvailable(ben)) scn.DSChooseCard(ben);
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Maul duel decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(offeredCancel(scn, escape, "Cancel duel"));
        var duel = scn.gameState().getDuelState();
        assertSame(maul, duel.getCardInitiatedDuel());
        scn.LSPlayCard(escape, "Cancel duel");
        scn.PassAllResponses();
        assertTrue(duel.isCanceled());
        assertInZone(Zone.LOST_PILE, escape);
    }

    private boolean offeredCancel(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl source, String text) {
        return scn.LSGetDecision() instanceof CardActionSelectionDecision && scn.LSCardActionAvailable(source, text);
    }

    private void settleDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 35 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected So Be It, Jedi deployment: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }
}
