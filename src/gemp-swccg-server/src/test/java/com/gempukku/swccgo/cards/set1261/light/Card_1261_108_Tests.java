package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_108_Tests {
    private VirtualTableScenario fixture(int stack) {
        var ls = new HashMap<>(Map.of("hut", "4_89", "jungle", "4_86", "luke", "1_19", "yoda", "4_2", "han", "1_11",
                "loss", "2_18", "testOne", "4_77", "testFive", "4_78", "backpack", "4_12", "commander", "601_52"));
        ls.putAll(Map.of("trainingArea", "4_88", "anoat", "4_80", "asteroid", "4_81",
                "maintenance", "13_9", "permanentWeapon", "108_3", "otherYoda", "12_35", "lossTwo", "2_23", "lossThree", "2_27", "lossFour", "2_33", "lightAlter", "1_71"));
        ls.put("ordinaryEffect", "1_66");
        ls.put("hothOnlyLuke", "3_3");
        ls.put("hope", "4_44");
        return new VirtualTableScenario(ls, new HashMap<>(Map.of("cantina", "1_290", "vader", "1_168",
                "bad", "4_116", "darkAlter", "1_234", "sense", "1_267", "darkDagobah", "7_279")), 40, 40,
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

    private VirtualTableScenario completeTest(boolean fifth) { return completeTest(fifth, 1, true); }

    private VirtualTableScenario completeTest(boolean fifth, int available, boolean accept) {
        var scn = fixture(0); scn.StartGame();
        var hut = scn.GetLSCard("hut"); var strong = scn.GetLSCard("strong");
        var luke = scn.GetLSCard("luke"); var yoda = scn.GetLSCard("yoda");
        var test = scn.GetLSCard(fifth ? "testFive" : "testOne"); var loss = scn.GetLSCard("loss");
        scn.MoveLocationToTable(hut); scn.MoveCardsToLocation(hut, luke, yoda);
        scn.AttachCardsTo(hut, test);
        test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, yoda, Filters.sameCardId(yoda));
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        if (available == 4) scn.MoveCardsToTopOfLSLostPile(loss, scn.GetLSCard("lossTwo"),
                scn.GetLSCard("lossThree"), scn.GetLSCard("lossFour"));
        else scn.MoveCardsToTopOfLSLostPile(loss);
        scn.SkipToLSTurn(Phase.DEPLOY);
        var action = new TopLevelGameTextAction(test, scn.LS, test.getCardId());
        action.appendEffect(new CompleteJediTestEffect(action, test));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        var objective = scn.GetLSCard("objective");
        var trace = new ArrayList<String>();
        boolean offered = false;
        for (int i = 0; i < 18 && scn.GetCurrentDecision() != null; i++) {
            var prompt = scn.GetCurrentDecision().getText();
            trace.add(prompt + " actions=" + scn.GetLSAvailableActions());
            if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Retrieve 3 Force"))) {
                offered = true;
                if (accept) scn.LSUseCardAction(strong, "Retrieve 3 Force"); else scn.LSPass();
            } else if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Flip"))) {
                scn.LSChooseAction("Flip");
            } else if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Reveal top two"))) {
                scn.LSChooseAction("Reveal top two");
            } else if (prompt.toLowerCase().contains("optional response")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else if (fifth && prompt.startsWith("Top cards of Reserve Deck")) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "");
            } else if (fifth && scn.LSGetDecision() != null
                    && prompt.startsWith("Choose destiny card to stack") && !scn.LSGetCardChoices().isEmpty()) {
                scn.PlayerDecided(scn.LS, scn.LSGetCardChoices().getFirst());
            } else break;
        }
        assertEquals(trace.toString(), fifth ? Title.Save_You_It_Can : Title.Mind_What_You_Have_Learned,
                objective.getBlueprint().getTitle());
        assertEquals(Zone.SIDE_OF_TABLE, test.getZone());
        assertTrue(GameConditions.isJediTestCompleted(scn.game(), test));
        assertTrue(trace.toString(), offered);
        assertEquals(accept ? Math.max(0, available - 3) : available, scn.GetLSLostPileCount());
        if (available == 1 && accept) assertTrue(scn.GetLSUsedPile().contains(loss));
        if (fifth) {
            assertEquals(1, scn.gameState().getStackedCards(test).size());
            assertTrue(scn.gameState().getStackedCards(luke).isEmpty());
        }
        return scn;
    }

    @Test public void firstTestGoesOnTableAndRetrievesWithoutFlippingTheObjective() { completeTest(false); }
    @Test public void fifthTestFlipsTheObjectiveAndStillLetsStrongRetrieve() { completeTest(true); }
    private void apprenticeLeaves(boolean crossesOver) {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("hut");
        scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site, luke);
        scn.gameState().flipCard(scn.game(), objective, true);
        scn.SkipToDSTurn(Phase.CONTROL);
        var source = scn.GetDSStartingLocation();
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        if (crossesOver) action.appendEffect(new CrossOverCharacterEffect(action, luke));
        else action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, luke));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        if (crossesOver) {
            if (scn.GetCurrentDecision().getText().contains("Required responses")) {
                scn.ChooseAction(scn.GetDecidingPlayer(), "Place out of play");
                scn.PassAllResponses();
            }
            assertTrue(luke.isCrossedOver());
            assertEquals(scn.DS, luke.getOwner());
            assertTrue(scn.game().getModifiersQuerying().isCrossedOver(scn.gameState(), Persona.LUKE));
            assertFalse(Filters.Luke.accepts(scn.game(), luke));
        } else assertEquals(Zone.OUT_OF_PLAY, luke.getZone());
        assertEquals(scn.GetCurrentDecision().getText() + " " + scn.gameState().getLastMessages(), Zone.OUT_OF_PLAY, objective.getZone());
    }

    @Test public void realLukeCrossoverPlacesSaveYouItCanOutOfPlay() { apprenticeLeaves(true); }
    @Test public void placingLukeOutOfPlayAlsoRemovesSaveYouItCan() { apprenticeLeaves(false); }

    @Test public void paidMovePhaseReturnPutsLukesAttachmentInUsedPile() {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective"); var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("hut"); var backpack = scn.GetLSCard("backpack");
        scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site, luke); scn.AttachCardsTo(luke, backpack);
        scn.gameState().flipCard(scn.game(), objective, true);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.MOVE);
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(objective, "Take Luke into hand");
        if (scn.LSHasCardChoiceAvailable(luke)) scn.LSChooseCard(luke);
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(luke));
        assertTrue(scn.GetLSUsedPile().contains(backpack));
        assertEquals(before - 3, scn.GetLSForcePileCount());
        assertTrue(scn.IsCardActive(objective));
    }
    private void trainingChoice(boolean chooseHigh) {
        var scn = fixture(0); scn.StartGame();
        var site = scn.GetLSCard("hut"); var luke = scn.GetLSCard("commander");
        var yoda = scn.GetLSCard("yoda"); var test = scn.GetLSCard("testOne");
        scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site, luke, yoda);
        scn.AttachCardsTo(site, test);
        test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, yoda, Filters.sameCardId(yoda));
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.SkipToLSTurn(Phase.CONTROL);
        var low = scn.GetLSDestiny(0); var high = scn.GetLSDestiny(7);
        scn.MoveCardsToTopOfLSReserveDeck(low, high);
        scn.LSUseCardAction(test, "Attempt Jedi Test #1");
        scn.PassAllResponses();
        var trace = new ArrayList<String>();
        for (int i = 0; i < 30 && !scn.LSDecisionAvailable("Choose 1 destiny"); i++) {
            var d = scn.GetCurrentDecision();
            if (d == null) break;
            trace.add(d.getText());
            if (!d.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(trace.toString(), scn.LSDecisionAvailable("Choose 1 destiny"));
        var ids = scn.LSGetBPChoices();
        int lowIndex = ids.indexOf(low.getBlueprintId(true)); int highIndex = ids.indexOf(high.getBlueprintId(true));
        assertTrue(ids.toString(), lowIndex >= 0 && highIndex >= 0 && ids.size() == 2);
        scn.PlayerDecided(scn.LS, scn.LSGetCardChoices().get(chooseHigh ? highIndex : lowIndex));
        scn.PassAllResponses();
        assertEquals(chooseHigh, GameConditions.isJediTestCompleted(scn.game(), test));
    }

    @Test public void strongOffersTwoActualTrainingCardsAndHighChoiceCompletesTheTest() { trainingChoice(true); }
    @Test public void choosingTheLowTrainingCardDoesNotCompleteTheTest() { trainingChoice(false); }

    private void apprenticeLeavesTraining(boolean completed) {
        var scn = completed ? completeTest(false) : fixture(0);
        var site = scn.GetLSCard("hut"); var luke = scn.GetLSCard("luke"); var test = scn.GetLSCard("testOne");
        if (!completed) {
            scn.StartGame();
            scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site, luke, scn.GetLSCard("yoda"));
            scn.AttachCardsTo(site, test);
            test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, scn.GetLSCard("yoda"), Filters.sameCardId(scn.GetLSCard("yoda")));
            test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        var source = scn.GetDSStartingLocation();
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, luke));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(luke));
        if (completed) {
            assertEquals(Zone.SIDE_OF_TABLE, test.getZone());
            assertFalse(scn.IsCardActive(test));
            var returnSite = scn.GetDSCard("cantina");
            scn.MoveLocationToTable(returnSite);
            scn.MoveCardsToLSHand(luke);
            scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.DEPLOY);
            scn.LSDeployCard(luke);
            scn.PassAllResponses();
            if (scn.LSHasCardChoiceAvailable(returnSite)) scn.LSChooseCard(returnSite);
            scn.PassForceUseResponses(); scn.PassAllResponses();
            assertEquals(scn.GetCurrentDecision().getText(), returnSite, luke.getAtLocation());
            assertEquals(luke, test.getTargetedCard(scn.gameState(), TargetId.JEDI_TEST_APPRENTICE));
            assertTrue(scn.IsCardActive(test));
        } else assertTrue(scn.GetLSLostPile().contains(test));
    }

    @Test public void strongSuspendsACompletedTestUntilTheApprenticeReturns() { apprenticeLeavesTraining(true); }
    @Test public void unfinishedTestIsStillLostWhenTheApprenticeLeaves() { apprenticeLeavesTraining(false); }
    private void strongRestrictions(boolean strongOnTable) {
        var scn = fixture(0); scn.StartGame();
        var strong = scn.GetLSCard("strong"); var training = scn.GetLSCard("trainingArea");
        var cantina = scn.GetDSCard("cantina"); var anoat = scn.GetLSCard("anoat");
        scn.MoveLocationToTable(training); scn.MoveLocationToTable(cantina); scn.MoveLocationToTable(anoat);
        scn.MoveCardsToLocation(training, scn.GetLSCard("yoda"));
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("han"));
        if (!strongOnTable) scn.MoveOutOfPlay(strong);
        scn.MoveCardsToLSHand(scn.GetLSCard("asteroid"), scn.GetLSCard("maintenance"),
                scn.GetLSCard("permanentWeapon"), scn.GetLSCard("luke"));
        scn.LSActivateForceCheat(12); scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(!strongOnTable, scn.LSForceDrainAvailable(training));
        assertTrue(scn.LSForceDrainAvailable(cantina));
        scn.SkipToPhase(Phase.DEPLOY);
        for (String key : List.of("asteroid", "maintenance", "permanentWeapon")) {
            assertEquals(key + " " + scn.GetLSAvailableActions(), !strongOnTable, scn.LSDeployAvailable(scn.GetLSCard(key)));
        }
        assertTrue(scn.LSDeployAvailable(scn.GetLSCard("luke")));
    }

    @Test public void strongBlocksDagobahDrainAndOnlyTheThreePrintedDeploymentKinds() { strongRestrictions(true); }
    @Test public void theSameDrainAndCardDeploymentsAreLegalWithoutStrong() { strongRestrictions(false); }
    @Test public void frontDownloadsDagobahYodaOutsideDeployAndAppliesThePrintedDiscount() {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective"); var yoda = scn.GetLSCard("yoda");
        var otherYoda = scn.GetLSCard("otherYoda"); var hut = scn.GetLSCard("hut");
        scn.MoveLocationToTable(hut);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(yoda, otherYoda);
        scn.gameState().markCardsRevealedFromPile(List.of(otherYoda));
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(objective, "Deploy card to Dagobah from Reserve Deck");
        var trace = new ArrayList<String>();
        for (int i = 0; i < 12 && yoda.getZone() != Zone.AT_LOCATION; i++) {
            var prompt = scn.GetCurrentDecision().getText(); trace.add(prompt);
            if (scn.LSGetDecision() != null && scn.LSGetBPChoices().contains(yoda.getBlueprintId(true))) {
                int wrong = scn.LSGetBPChoices().indexOf(otherYoda.getBlueprintId(true));
                assertTrue(wrong < 0 || !Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(wrong)));
                scn.LSChooseCard(yoda);
            } else if (scn.LSHasCardChoiceAvailable(hut)) scn.LSChooseCard(hut);
            else if (prompt.toLowerCase().contains("optional response")) scn.PlayerPass(scn.GetDecidingPlayer());
            else break;
        }
        scn.PassAllResponses();
        assertEquals(trace.toString(), hut, yoda.getAtLocation());
        assertEquals(2, before - scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(otherYoda));
    }
    @Test public void completingATestRetrievesExactlyThreeEvenWithFourAvailable() { completeTest(false, 4, true); }
    @Test public void thePlayerCanDeclineAllThreeAfterCompletingATest() { completeTest(false, 4, false); }

    private void actualCancelPermissions(boolean back) {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective"); var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation(); var vader = scn.GetDSCard("vader");
        var darkEffect = scn.GetDSCard("bad"); var lightEffect = scn.GetLSCard("ordinaryEffect");
        var lsAlter = scn.GetLSCard("lightAlter"); var dsAlter = scn.GetDSCard("darkAlter");
        scn.MoveCardsToLSSideOfTable(lightEffect, darkEffect);
        scn.MoveCardsToLocation(site, luke, vader);
        if (back) scn.gameState().flipCard(scn.game(), objective, true);
        scn.MoveCardsToDSHand(dsAlter, scn.GetDSCard("sense")); scn.MoveCardsToLSHand(lsAlter);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(!back, scn.DSCardPlayAvailable(dsAlter, "Draw destiny to cancel Effect"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(1);
        scn.LSPlayCard(lsAlter, "Draw destiny to cancel Effect");
        if (scn.LSHasCardChoiceAvailable(darkEffect)) scn.LSChooseCard(darkEffect);
        if (scn.LSHasCardChoiceAvailable(luke)) scn.LSChooseCard(luke);
        boolean senseOffered = scn.GetDSAvailableActions().stream()
                .anyMatch(action -> action.contains("Draw destiny to cancel") && action.contains("Alter"));
        assertEquals(scn.GetCurrentDecision().getText(), !back, senseOffered);
        if (!back) {
            scn.DSPlayCard(scn.GetDSCard("sense"), "Draw destiny to cancel");
            if (scn.DSHasCardChoiceAvailable(vader)) scn.DSChooseCard(vader);
            scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
            assertTrue(scn.gameState().getLastMessages().toString(), scn.gameState().getLastMessages().stream()
                    .anyMatch(msg -> msg.equals("Destiny: 5")));
            assertTrue(scn.GetLSLostPile().contains(lsAlter));
        }
    }

    @Test public void frontAllowsActualDarkSideSenseAndAlter() { actualCancelPermissions(false); }
    @Test public void backBansActualDarkSideSenseAndAlterButAllowsLightAlter() { actualCancelPermissions(true); }

    private void actualAlterBonus(boolean lightPlayer) {
        var scn = fixture(0); scn.StartGame();
        var site = scn.GetDSStartingLocation(); var luke = scn.GetLSCard("luke"); var vader = scn.GetDSCard("vader");
        var lightEffect = scn.GetLSCard("ordinaryEffect"); var darkEffect = scn.GetDSCard("bad");
        var alter = lightPlayer ? scn.GetLSCard("lightAlter") : scn.GetDSCard("darkAlter");
        var effect = lightPlayer ? darkEffect : lightEffect;
        var character = lightPlayer ? luke : vader;
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSSideOfTable(lightEffect, darkEffect);
        if (lightPlayer) {
            scn.MoveCardsToLSHand(alter); scn.SkipToLSTurn(Phase.CONTROL); scn.PrepareLSDestiny(1);
            scn.LSPlayCard(alter, "Draw destiny to cancel Effect");
            if (scn.LSHasCardChoiceAvailable(effect)) scn.LSChooseCard(effect);
            if (scn.LSHasCardChoiceAvailable(character)) scn.LSChooseCard(character);
        } else {
            scn.MoveCardsToDSHand(alter); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(1);
            scn.DSPlayCard(alter, "Draw destiny to cancel Effect");
            if (scn.DSHasCardChoiceAvailable(effect)) scn.DSChooseCard(effect);
            if (scn.DSHasCardChoiceAvailable(character)) scn.DSChooseCard(character);
        }
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertTrue(scn.gameState().getLastMessages().toString(), scn.gameState().getLastMessages().stream()
                .anyMatch(msg -> msg.equals("Destiny: 5")));
        if (lightPlayer) assertTrue(scn.IsCardActive(darkEffect));
        else assertTrue(scn.GetLSLostPile().contains(lightEffect));
    }

    @Test public void frontAddsFourToTheActualLightAlterDraw() { actualAlterBonus(true); }
    @Test public void frontAddsFourToTheActualDarkAlterDraw() { actualAlterBonus(false); }

    private void retrievalContribution(boolean onDagobah) {
        var scn = fixture(0); scn.StartGame();
        var hut = scn.GetLSCard("hut"); var other = scn.GetDSStartingLocation(); var luke = scn.GetLSCard("luke");
        var source = scn.GetLSCard("ordinaryEffect"); var lost = scn.GetLSCard("loss");
        scn.MoveLocationToTable(hut); scn.MoveCardsToLocation(onDagobah ? hut : other, luke);
        scn.MoveCardsToLSSideOfTable(source); scn.MoveCardsToTopOfLSLostPile(lost);
        scn.SkipToLSTurn(Phase.CONTROL);
        var action = new TopLevelGameTextAction(source, scn.LS, source.getCardId());
        action.appendEffect(new RetrieveForceEffect(action, scn.LS, 1) {
            @Override public java.util.Collection<com.gempukku.swccgo.game.PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                return List.of(luke);
            }
        });
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action); scn.PassAllResponses();
        assertEquals(!onDagobah, scn.GetLSUsedPile().contains(lost));
        assertEquals(onDagobah, scn.GetLSLostPile().contains(lost));
    }

    @Test public void frontPreventsActualRetrievalWhenItsContributorIsOnDagobah() { retrievalContribution(true); }
    @Test public void frontAllowsActualRetrievalWithTheSameContributorOffDagobah() { retrievalContribution(false); }

    private void handDeployment(boolean back) {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective"); var luke = scn.GetLSCard("hothOnlyLuke");
        var cantina = scn.GetDSCard("cantina");
        scn.MoveLocationToTable(cantina); scn.MoveCardsToLocation(cantina, scn.GetLSCard("han"));
        if (back) scn.gameState().flipCard(scn.game(), objective, true);
        scn.MoveCardsToLSHand(luke); scn.LSActivateForceCheat(10); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(back, scn.LSDeployAvailable(luke));
        if (!back) return;
        scn.LSDeployCard(luke);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(cantina)) scn.LSChooseCard(cantina);
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertEquals(cantina, luke.getAtLocation());
    }

    @Test public void backLetsCommanderLukeDeployOutsideHothFromHand() { handDeployment(true); }
    @Test public void frontLeavesCommandersOwnHothOnlyRequirementInPlaceFromHand() { handDeployment(false); }


    private void actualSystemConversion(boolean objectiveInSetup) {
        var scn = objectiveInSetup ? fixture(0) : new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("darkDagobah", "7_279")), 40, 40, StartingSetup.LSStartingLocation("4_84"),
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        var original = objectiveInSetup ? scn.GetLSCard("dagobah") : scn.GetLSStartingLocation();
        var replacement = scn.GetDSCard("darkDagobah");
        if (objectiveInSetup) {
            var objective = scn.GetLSCard("objective");
            scn.gameState().flipCard(scn.game(), objective, true);
            assertTrue(objective.isFlipped());
        }
        scn.MoveCardsToDSHand(replacement); scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(!objectiveInSetup, scn.DSDeployAvailable(replacement));
        if (!objectiveInSetup) {
            scn.DSDeployCard(replacement);
            if (scn.DSHasCardChoiceAvailable(original)) scn.DSChooseCard(original);
            scn.PassAllResponses();
            assertEquals(Zone.LOCATIONS, replacement.getZone());
            assertTrue(scn.gameState().getConvertedLocationsUnderTopLocation(replacement).contains(original));
        } else {
            assertEquals(Zone.LOCATIONS, original.getZone());
            assertEquals(Zone.HAND, replacement.getZone());
        }
    }

    @Test public void nativeDarkDagobahCannotConvertTheObjectiveSystemAfterTheFlip() { actualSystemConversion(true); }
    @Test public void nativeDarkDagobahCanNormallyConvertTheSameLightSystem() { actualSystemConversion(false); }

    @Test public void frontDownloadsLukeForZeroThenBackpackAndHopeOnlyToDagobah() {
        var scn = fixture(0); scn.StartGame();
        var objective = scn.GetLSCard("objective"); var hut = scn.GetLSCard("hut");
        var luke = scn.GetLSCard("luke"); var backpack = scn.GetLSCard("backpack");
        var hope = scn.GetLSCard("hope"); var han = scn.GetLSCard("han");
        scn.MoveLocationToTable(hut);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), han);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(luke, backpack, hope);
        int before = scn.GetLSForcePileCount();
        resolveDownload(scn, objective, luke, hut, han, Zone.AT_LOCATION);
        assertEquals(hut, luke.getAtLocation());
        assertEquals(before, scn.GetLSForcePileCount());
        resolveDownload(scn, objective, backpack, luke, han, Zone.ATTACHED);
        assertEquals(luke, backpack.getAttachedTo());
        resolveDownload(scn, objective, hope, luke, han, Zone.ATTACHED);
        assertEquals(luke, hope.getAttachedTo());
    }

    private void resolveDownload(VirtualTableScenario scn, PhysicalCardImpl objective, PhysicalCardImpl card,
                                 PhysicalCardImpl target, PhysicalCardImpl offSystem, Zone destination) {
        scn.LSUseCardAction(objective, "Deploy card to Dagobah from Reserve Deck");
        var trace = new ArrayList<String>();
        for (int i = 0; i < 16 && card.getZone() != destination; i++) {
            var prompt = scn.GetCurrentDecision().getText(); trace.add(prompt);
            if (scn.LSGetDecision() != null && scn.LSGetBPChoices().contains(card.getBlueprintId(true))) {
                scn.LSChooseCard(card);
            } else if (scn.LSHasCardChoiceAvailable(target)) {
                assertFalse("Off-system target offered: " + trace, scn.LSHasCardChoiceAvailable(offSystem));
                scn.LSChooseCard(target);
            } else if (prompt.toLowerCase().contains("optional response")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else break;
        }
        scn.PassAllResponses();
        assertEquals(trace.toString(), destination, card.getZone());
        if (scn.DSGetDecision() != null) scn.DSPass();
    }

}
