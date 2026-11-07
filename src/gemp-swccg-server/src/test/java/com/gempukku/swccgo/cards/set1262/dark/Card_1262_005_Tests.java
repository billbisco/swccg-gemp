package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
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

public class Card_1262_005_Tests {
    private static final String REDUCE = "Place out of play to reduce battle damage by 4";
    private static final String ATTEMPT = "Attempt Jedi Test #1";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("luke", "1_19"), Map.entry("obi", "1_21"), Map.entry("yoda", "4_2"),
                Map.entry("test", "4_77"), Map.entry("hut", "4_89"), Map.entry("bog", "4_85"),
                Map.entry("jungle", "4_86"), Map.entry("generator", "13_31"),
                Map.entry("hallway", "14_51"), Map.entry("noooo", "1220_150"), Map.entry("alter", "1_71"))),
                new HashMap<>(Map.of("effect", "1262_5", "effect2", "1262_5", "original", "4_118",
                        "vader", "1_168", "trooper", "1_194")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void BrokenConcentrationPrintedAttributesIconsAndKeywordsMatch() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("Broken Concentration", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("To effectively use the Force, a Jedi is required to learn concentration. A Jedi who gives in to distraction places both himself and his friends in peril.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        assertTrue(GameTextActionId.LEGACY__BROKEN_CONCENTRATION_V__REDUCE_BATTLE_DAMAGE.isPerGame());
    }

    @Test public void BrokenConcentrationNormalDeploymentCancelsBothSidesOfReflectionsThreeNabooSites() {
        var scn = scenario();
        var site = scn.GetLSCard("generator");
        var otherNaboo = scn.GetLSCard("hallway");
        var effect = scn.GetDSCard("effect");
        var obi = scn.GetLSCard("obi");
        var vader = scn.GetDSCard("vader");
        scn.MoveLocationToTable(site);
        scn.MoveLocationToTable(otherNaboo);
        scn.MoveCardsToLocation(site, obi, vader);
        scn.MoveCardsToDSHand(effect);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(7, scn.GetPower(obi));
        assertEquals(7, scn.GetPower(vader));
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(effect));
        scn.DSPlayCard(effect);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        assertEquals(force, scn.GetDSForcePileCount());
        assertEquals(5, scn.GetPower(obi));
        assertEquals(6, scn.GetPower(vader));
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isLocationGameTextCanceledForPlayer(scn.gameState(), site, scn.LS));
        assertTrue(q.isLocationGameTextCanceledForPlayer(scn.gameState(), site, scn.DS));
        assertFalse(q.isLocationGameTextCanceledForPlayer(scn.gameState(), otherNaboo, scn.LS));
        assertFalse(q.isLocationGameTextCanceledForPlayer(scn.gameState(), scn.GetDSStartingLocation(), scn.DS));
    }

    @Test public void BrokenConcentrationIsImmuneToActualNooooAndOriginalIsNot() {
        for (String printing : List.of("effect", "original")) {
            var scn = scenario();
            var effect = scn.GetDSCard(printing);
            var interrupt = scn.GetLSCard("noooo");
            scn.MoveCardsToSideOfTable(effect);
            scn.MoveCardsToLSHand(interrupt);
            scn.SkipToLSTurn(Phase.CONTROL);
            assertEquals(printing.equals("original"), scn.LSCardPlayAvailable(interrupt, "Cancel Broken Concentration"));
            if (printing.equals("original")) {
                scn.LSPlayCard(interrupt, "Cancel Broken Concentration");
                if (scn.LSHasCardChoiceAvailable(effect)) scn.LSChooseCard(effect);
                scn.PassAllResponses();
                assertInZone(Zone.LOST_PILE, effect);
            }
        }
    }

    @Test public void BrokenConcentrationCannotBeChosenByActualAlter() {
        var scn = scenario();
        var effect = scn.GetDSCard("effect");
        var alter = scn.GetLSCard("alter");
        scn.MoveCardsToSideOfTable(effect);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(alter));
        assertInZone(Zone.SIDE_OF_TABLE, effect);
    }

    private void attemptTraining(boolean effect, int rawDestiny, boolean completes) {
        var scn = scenario();
        var hut = scn.GetLSCard("hut");
        var luke = scn.GetLSCard("luke");
        var yoda = scn.GetLSCard("yoda");
        var test = scn.GetLSCard("test");
        scn.MoveLocationToTable(hut);
        scn.MoveLocationToTable(scn.GetLSCard("bog"));
        scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.MoveCardsToLocation(hut, luke, yoda);
        scn.AttachCardsTo(hut, test);
        test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, yoda, Filters.sameCardId(yoda));
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        if (effect) scn.MoveCardsToSideOfTable(scn.GetDSCard("effect"));
        scn.SkipToLSTurn(Phase.CONTROL);
        for (int i = 0; i < 15 && !(scn.LSGetDecision() != null && scn.LSCardActionAvailable(test, ATTEMPT)); i++) {
            assertFalse(scn.AwaitingLSControlPhaseActions());
            if (scn.DSGetDecision() != null) scn.DSPass(); else scn.LSPass();
        }
        assertTrue(scn.LSCardActionAvailable(test, ATTEMPT));
        scn.PrepareLSDestiny(rawDestiny);
        scn.LSUseCardAction(test, ATTEMPT);
        boolean added = false;
        for (int i = 0; i < 30 && !scn.AwaitingLSControlPhaseActions(); i++) {
            var decision = scn.GetCurrentDecision();
            assertTrue("Unexpected training decision: " + decision.getText(), decision instanceof CardActionSelectionDecision);
            if (scn.LSGetDecision() != null) {
                if (scn.LSCardActionAvailable(test, "Add 1 to destiny for each Dagobah site")) {
                    scn.LSUseCardAction(test, "Add 1 to destiny for each Dagobah site");
                    added = true;
                } else scn.LSPass();
            } else scn.DSPass();
        }
        assertTrue(added);
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(rawDestiny));
        assertEquals(completes, GameConditions.isJediTestCompleted(scn.game(), test));
    }

    @Test public void BrokenConcentrationReducesActualTrainingDestinyByApprenticeAbilityMinusTestNumber() {
        attemptTraining(false, 6, true);
        attemptTraining(true, 6, false);
    }

    @Test public void BrokenConcentrationTrainingTestDoesNotPassAtExactThreshold() {
        attemptTraining(false, 5, false);
    }

    private VirtualTableScenario damage(int destiny) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.MoveCardsToSideOfTable(scn.GetDSCard("effect"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(destiny);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        return scn;
    }

    private void reduce(int destiny, int remaining) {
        var scn = damage(destiny);
        var effect = scn.GetDSCard("effect");
        assertEquals(2 + destiny, scn.GetUnpaidDSBattleDamage());
        assertEquals(destiny, scn.GetUnpaidDSAttrition());
        assertTrue(scn.DSCardActionAvailable(effect, REDUCE));
        scn.DSUseCardAction(effect, REDUCE);
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, effect);
        assertEquals(remaining, scn.GetUnpaidDSBattleDamage());
        assertEquals(destiny, scn.GetUnpaidDSAttrition());
    }

    @Test public void BrokenConcentrationReductionFloorsAtZeroForThreeDamage() { reduce(1, 0); }
    @Test public void BrokenConcentrationReductionPaysExactlyFourDamage() { reduce(2, 0); }
    @Test public void BrokenConcentrationReductionLeavesOneDamageFromFive() { reduce(3, 1); }

    @Test public void BrokenConcentrationSecondCopyCannotReduceDamageAfterFirstWasUsed() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var first = scn.GetDSCard("effect");
        var second = scn.GetDSCard("effect2");
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetLSCard("luke"), scn.GetLSCard("obi"));
        scn.MoveCardsToSideOfTable(first);
        scn.MoveCardsToDSHand(second);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(3);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        assertEquals(4, scn.GetUnpaidDSBattleDamage());
        scn.DSUseCardAction(first, REDUCE);
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, first);
        if (scn.AwaitingLSDamageSegmentActions()) scn.LSPass();
        scn.PassDamageSegmentActions();
        if (scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)")) scn.DSPass();
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(second));
        scn.DSPlayCard(second);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, second);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(3);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        assertEquals(4, scn.GetUnpaidDSBattleDamage());
        assertFalse(scn.DSCardActionAvailable(second, REDUCE));
    }

    @Test public void BrokenConcentrationCannotBeUsedOutsideBattleDamage() {
        var scn = scenario();
        scn.MoveCardsToSideOfTable(scn.GetDSCard("effect"));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), REDUCE));
    }
}
