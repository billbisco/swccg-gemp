package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_108_Tests {
    private VirtualTableScenario fixture(int stack) {
        var ls = new HashMap<>(Map.of("hut", "4_89", "jungle", "4_86", "luke", "1_19", "yoda", "4_2", "han", "1_11",
                "loss", "2_18", "testOne", "4_77", "testFive", "4_78", "backpack", "4_6"));
        return new VirtualTableScenario(ls, new HashMap<>(), 40, 40,
                new StartingSetup() {
                    @Override public HashMap<String, String> Cards() {
                        return new HashMap<>(Map.of("objective", "1261_108", "dagobah", "4_84", "strong", "1261_87"));
                    }
                    @Override public void Setup(VirtualTableScenario scn) {
                        var trace = new ArrayList<String>();
                        for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; i++) {
                            var prompt = scn.GetCurrentDecision().getText();
                            trace.add(prompt);
                            if (prompt.contains("Choose Dagobah system to deploy")) {
                                scn.LSChooseCard(scn.GetLSCard("dagobah"));
                            } else if (prompt.contains("Choose Strong Is Vader to deploy")) {
                                scn.LSChooseCard(scn.GetLSCard("strong"));
                            } else if (prompt.contains("Choose up to six Jedi Tests to stack")) {
                                if (stack == 0) scn.LSPass();
                                else {
                                    PhysicalCardImpl[] chosen = new PhysicalCardImpl[stack];
                                    for (int index = 0; index < stack; index++) chosen[index] = scn.GetLSCard("outside" + index);
                                    scn.LSChooseCards(chosen);
                                }
                            } else if (prompt.startsWith("Verify ") || prompt.toLowerCase().contains("optional response")) {
                                scn.PlayerPass(scn.GetDecidingPlayer());
                            } else break;
                        }
                        assertTrue(trace.toString(), scn.IsCardActive(scn.GetLSCard("strong")));
                    }
                }, StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                new StartingSetup() {
                    @Override public HashMap<String, String> Cards() {
                        return new HashMap<>(Map.of("outside0", "4_77", "outside1", "4_75", "outside2", "4_76",
                                "outside3", "4_79", "outside4", "4_78", "outside5", "9_55", "outside6", "4_75",
                                "outsideOther", "1_66"));
                    }
                    @Override public void Setup(VirtualTableScenario scn) {}
                }, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void strongIsVaderUsesTheExactLegacyEpicEventPrinting() {
        var scn = fixture(0); var b = scn.GetLSCard("strong").getBlueprint();
        assertEquals(Title.Strong_Is_Vader, b.getTitle()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EPIC_EVENT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EPIC_EVENT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void bothObjectiveFacesUseTheFinalLegacyPrinting() {
        var scn = fixture(0); scn.StartGame(); var objective = scn.GetLSCard("objective");
        for (int i = 0; i < 2; i++) {
            var b = objective.getBlueprint();
            assertEquals(i == 0 ? Title.Mind_What_You_Have_Learned : Title.Save_You_It_Can, b.getTitle());
            assertEquals(i == 0 ? 0 : 7, b.getDestiny(), scn.epsilon);
            assertTrue(b.hasVirtualSuffix()); assertEquals(Side.LIGHT, b.getSide());
            assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
            scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.OBJECTIVE)));
            scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.OBJECTIVE, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
            scn.BlueprintKeywordCheck(b, new ArrayList<>());
            if (i == 0) scn.gameState().flipCard(scn.game(), objective, true);
        }
    }

    private void actualStack(int count) {
        var scn = fixture(count);
        var originalOutside = List.copyOf(scn.gameState().getOutsideOfDeck(scn.LS));
        scn.StartGame(); var strong = scn.GetLSCard("strong");
        var dagobah = scn.GetLSCard("dagobah");
        assertTrue(scn.IsCardActive(scn.GetLSCard("objective")));
        assertTrue(scn.IsCardActive(strong)); assertEquals(Zone.LOCATIONS, dagobah.getZone());
        assertTrue(scn.game().getModifiersQuerying().cannotBeConverted(scn.gameState(), dagobah));
        assertEquals(count, scn.gameState().getStackedCards(strong).size());
        for (var card : scn.gameState().getStackedCards(strong)) {
            assertTrue(originalOutside.contains(card));
            assertTrue(Filters.Jedi_Test.accepts(scn.game(), card));
            assertEquals(Zone.STACKED_FACE_DOWN, card.getZone());
            assertEquals(strong, card.getStackedOn());
        }
        assertFalse(scn.gameState().getStackedCards(strong).contains(scn.GetLSCard("outsideOther")));
    }

    @Test public void objectiveStartsStrongAndAllowsZeroTests() { actualStack(0); }
    @Test public void objectiveStartsStrongAndAllowsASubsetOfTests() { actualStack(2); }
    @Test public void objectiveStartsStrongAndAllowsSixTestsWithoutTakingTheSeventh() { actualStack(6); }

    private void completeTest(boolean fifth) {
        var scn = fixture(0); scn.StartGame();
        var hut = scn.GetLSCard("hut"); var strong = scn.GetLSCard("strong");
        var luke = scn.GetLSCard("luke"); var yoda = scn.GetLSCard("yoda");
        var test = scn.GetLSCard(fifth ? "testFive" : "testOne"); var loss = scn.GetLSCard("loss");
        scn.MoveLocationToTable(hut); scn.MoveCardsToLocation(hut, luke, yoda);
        scn.AttachCardsTo(hut, test);
        test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, yoda, Filters.sameCardId(yoda));
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.MoveCardsToTopOfLSLostPile(loss);
        scn.SkipToLSTurn(Phase.DEPLOY);
        var action = new TopLevelGameTextAction(test, scn.LS, test.getCardId());
        action.appendEffect(new CompleteJediTestEffect(action, test));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        var objective = scn.GetLSCard("objective");
        var trace = new ArrayList<String>();
        boolean retrieved = false;
        for (int i = 0; i < 18 && scn.GetCurrentDecision() != null; i++) {
            var prompt = scn.GetCurrentDecision().getText();
            trace.add(prompt + " actions=" + scn.GetLSAvailableActions());
            if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Retrieve 3 Force"))) {
                scn.LSUseCardAction(strong, "Retrieve 3 Force"); retrieved = true;
            } else if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Flip"))) {
                scn.LSChooseAction("Flip");
            } else if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Reveal top two"))) {
                scn.LSUseCardAction(test, "Reveal top two");
            } else if (prompt.toLowerCase().contains("optional response")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else if (fifth && prompt.startsWith("Top cards of Reserve Deck")) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "");
            } else if (fifth && scn.LSGetDecision() != null
                    && prompt.startsWith("Choose destiny card to stack") && !scn.LSGetCardChoices().isEmpty()) {
                scn.LSChooseAnyCard();
            } else break;
        }
        assertEquals(trace.toString(), fifth ? Title.Save_You_It_Can : Title.Mind_What_You_Have_Learned,
                objective.getBlueprint().getTitle());
        assertEquals(Zone.SIDE_OF_TABLE, test.getZone());
        assertTrue(GameConditions.isJediTestCompleted(scn.game(), test));
        assertTrue(trace.toString(), retrieved);
        assertTrue(scn.GetLSUsedPile().contains(loss));
    }

    @Test public void firstTestGoesOnTableAndRetrievesWithoutFlippingTheObjective() { completeTest(false); }
    @Test public void fifthTestFlipsTheObjectiveAndStillLetsStrongRetrieve() { completeTest(true); }
}
