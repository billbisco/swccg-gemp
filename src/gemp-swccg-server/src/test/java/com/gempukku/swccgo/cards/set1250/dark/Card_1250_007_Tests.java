package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_007_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("obi", "1_21"), Map.entry("luke", "1_19"), Map.entry("leia", "601_227"),
                Map.entry("ovsLeia", "1103_12"), Map.entry("senate", "12_75"), Map.entry("senator", "12_28"),
                Map.entry("effect", "1_66"), Map.entry("savRip", "1_55"))),
                new HashMap<>(Map.ofEntries(
                        Map.entry("sidious", "1250_7"), Map.entry("vader", "1_168"), Map.entry("maul", "14_77"),
                        Map.entry("senate", "12_167"), Map.entry("senator", "12_110"), Map.entry("trooper", "1_194"),
                        Map.entry("loss1", "1_194"), Map.entry("loss2", "1_194"), Map.entry("loss3", "1_194"))),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void LordSidiousPrintedAttributesIconsAndKeywordsMatch() {
        var scn = scenario();
        var b = scn.GetDSCard("sidious").getBlueprint();
        assertEquals("Lord Sidious", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.hasPersona(Persona.SIDIOUS));
        assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(6, b.getDeployCost(), scn.epsilon);
        assertEquals(5, b.getPower(), scn.epsilon);
        assertEquals(7, b.getAbility(), scn.epsilon);
        assertEquals(8, b.getForfeit(), scn.epsilon);
        assertEquals("Leader. Trade Federation.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.DARK_JEDI_MASTER)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.DARK_JEDI_MASTER, Icon.DEATH_STAR_II,
                Icon.EPISODE_I, Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.LEADER, Keyword.TRADE_FEDERATION)));
    }

    private VirtualTableScenario senate(boolean own) {
        var scn = scenario();
        var senate = own ? scn.GetDSCard("senate") : scn.GetLSCard("senate");
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetDSCard("sidious"));
        return scn;
    }

    @Test public void LordSidiousHasFourPoliticsAtOwnSenateWithoutOwnSenator() {
        var scn = senate(true);
        var sidious = scn.GetDSCard("sidious");
        var q = scn.game().getModifiersQuerying();
        assertEquals(4f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), scn.GetLSCard("senator"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("senator"));
        assertEquals(4f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), scn.GetDSCard("senator"));
        assertEquals(0f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
    }

    @Test public void LordSidiousHasNoPoliticsBonusAtOpponentsSenateOrAway() {
        var scn = senate(false);
        var sidious = scn.GetDSCard("sidious");
        var q = scn.game().getModifiersQuerying();
        assertEquals(0f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sidious);
        assertEquals(0f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
    }

    @Test public void LordSidiousResistsStandardGameTextCancellationOnlyAtOwnSenateWithoutOwnSenator() {
        var scn = senate(true);
        var sidious = scn.GetDSCard("sidious");
        var effect = scn.GetLSCard("effect");
        var q = scn.game().getModifiersQuerying();
        scn.MoveCardsToSideOfTable(effect);
        // Model an already active game-text cancellation using the engine's normal modifier.
        scn.ApplyAdHocModifier(new CancelsGameTextModifier(effect, sidious));
        assertTrue(q.isProhibitedFromHavingGameTextCanceled(scn.gameState(), sidious));
        assertFalse(q.isGameTextCanceled(scn.gameState(), sidious));
        assertEquals(4f, q.getPolitics(scn.gameState(), sidious), scn.epsilon);
        var senator = scn.GetDSCard("senator");
        var senate = scn.GetDSCard("senate");
        scn.MoveCardsToDSHand(senator);
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(senator));
        scn.DSPlayCard(senator);
        if (scn.DSHasCardChoiceAvailable(senate)) scn.DSChooseCard(senate);
        scn.PassAllResponses();
        assertEquals(senate, senator.getAtLocation());
        assertFalse(q.isProhibitedFromHavingGameTextCanceled(scn.gameState(), sidious));
        assertTrue(q.isGameTextCanceled(scn.gameState(), sidious));
    }

    private void resolveLeiaRemoval(VirtualTableScenario scn, PhysicalCardImpl target) {
        for (int i = 0; i < 25 && target.getZone() != Zone.OUT_OF_PLAY; i++) {
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayRemainingForceLossFromReserveDeck();
            } else if (scn.LSHasCardChoiceAvailable(target)) {
                scn.LSChooseCard(target);
            } else {
                var decision = scn.GetCurrentDecision();
                assertTrue("Expected a live Leia response: " + decision.getText(),
                        decision instanceof CardActionSelectionDecision
                                && !scn.AwaitingLSControlPhaseActions() && !scn.AwaitingDSControlPhaseActions());
                if (scn.LSGetDecision() != null) scn.LSPass();
                else scn.DSPass();
            }
        }
        assertInZone(Zone.OUT_OF_PLAY, target);
    }

    @Test public void LordSidiousCannotBeChosenByActualBlockOneLeiaButNormalTargetCan() {
        var scn = senate(true);
        var senate = scn.GetDSCard("senate");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(senate, leia, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(leia, "and other character out of play"));
        scn.LSUseCardAction(leia, "and other character out of play");
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("sidious")));
        resolveLeiaRemoval(scn, trooper);
        assertInZone(Zone.AT_LOCATION, scn.GetDSCard("sidious"));
    }

    @Test public void LordSidiousIsNotImmuneToDifferentPrintingOfLeia() {
        var scn = senate(true);
        var leia = scn.GetLSCard("ovsLeia");
        var sidious = scn.GetDSCard("sidious");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), leia);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(leia, "and other character out of play"));
        scn.LSUseCardAction(leia, "and other character out of play");
        resolveLeiaRemoval(scn, sidious);
    }

    private void forfeit(String character, int darkJediCount, int expectedRetrieval) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var enemy = scn.GetLSCard(character);
        scn.MoveCardsToLocation(site, scn.GetDSCard("sidious"), enemy);
        if (darkJediCount >= 2) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        if (darkJediCount >= 3) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("maul"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("loss1"), scn.GetDSCard("loss2"), scn.GetDSCard("loss3"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(7);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        if (scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)")) scn.DSPass();
        assertTrue(scn.LSHasCardChoiceAvailable(enemy));
        scn.LSChooseCard(enemy);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, enemy);
        assertEquals(3 - expectedRetrieval, scn.GetDSLostPileCount());
    }

    @Test public void LordSidiousRetrievesTwoWhenJediActuallyForfeitedWithExactlyTwoOwnDarkJedi() {
        forfeit("obi", 2, 2);
    }
    @Test public void LordSidiousDoesNotRetrieveWhenOnlyOneOwnDarkJedi() {
        forfeit("obi", 1, 0);
    }
    @Test public void LordSidiousDoesNotRetrieveWhenThreeOwnDarkJedi() {
        forfeit("obi", 3, 0);
    }
    @Test public void LordSidiousDoesNotRetrieveWhenForfeitedCharacterIsNotJedi() {
        forfeit("luke", 2, 0);
    }

    @Test public void LordSidiousSurvivesActualSevenAttritionWithoutBattleDamage() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var sidious = scn.GetDSCard("sidious");
        scn.MoveCardsToLocation(site, sidious, scn.GetLSCard("obi"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(7);
        scn.PrepareLSDestiny(7);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        assertEquals(7, scn.GetUnpaidDSAttrition());
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
        scn.DSPass();
        assertInZone(Zone.AT_LOCATION, sidious);
    }
}
