package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1220_138_Tests {
    private VirtualTableScenario fixture(String start, String purge) { return fixture(start, purge, false); }

    private VirtualTableScenario fixture(String start, String purge, boolean arrangeZones) {
        var ls = new HashMap<>(Map.of("future", "1220_138", "battlePlan", "8_35", "doNot", "4_21",
                "immune", "9_47", "ordinary", "1_66", "luke", "1_19", "site", "3_63",
                "stackOne", "2_18", "stackTwo", "2_23", "stackThree", "2_27"));
        ls.putAll(Map.of("jess", "6_21", "beggar", "1_44"));
        if (arrangeZones) ls.putAll(Map.of("tableMaintenance", "13_9", "handWeapon", "108_3", "forceRevolution", "1_62"));
        if (purge != null) ls.put("purge", purge);
        var ds = new HashMap<>(Map.of("darkInsert", "1_208", "darkWeapon", "108_6", "vader", "1_168", "darkMaintenance", "13_59"));
        return new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.LSStartingLocation(start),
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void epicEventUsesFinalBlockTwoPrinting() {
        var scn = fixture("5_79", null); var b = scn.GetLSCard("future").getBlueprint();
        assertEquals(Title.It_Is_The_Future_You_See, b.getTitle()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EPIC_EVENT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EPIC_EVENT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_2)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void startingOptionRejectsAllThreeExcludedSetsAndTwoLocations() {
        for (String start : List.of("12_74", "13_31", "7_129")) {
            var scn = fixture(start, null);
            scn.StartGame();
            assertFalse(start, scn.LSDecisionAvailable("Choose starting interrupt"));
        }
        var scn = fixture("5_79", null);
        var event = scn.GetLSCard("future");
        assertFalse(event.getBlueprint().playableAsStartingInterrupt(scn.game(), event));
        scn.StartGame();
        assertTrue(scn.LSHasCardChoiceAvailable(event));
        scn.LSPass();
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        assertFalse(event.getBlueprint().playableAsStartingInterrupt(scn.game(), event));
        scn.MoveCardsToLSHand(event); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(event));
    }

    private VirtualTableScenario begin(boolean agent, String purge) { return begin(agent, purge, false); }

    private VirtualTableScenario begin(boolean agent, String purge, boolean arrangeZones) {
        var scn = fixture("5_79", purge, arrangeZones);
        scn.StartGame();
        if (arrangeZones) {
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("tableMaintenance"));
            scn.MoveCardsToLSHand(scn.GetLSCard("handWeapon"), scn.GetLSCard("ordinary"));
            scn.MoveCardsToTopOfLSForcePile(scn.GetLSCard("forceRevolution"));
            scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("darkInsert"));
            scn.MoveCardsToDSHand(scn.GetDSCard("darkMaintenance"), scn.GetDSCard("darkWeapon"));
        }
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSChooseCard(scn.GetLSCard("future"));
        var needed = new ArrayList<>(List.of("battlePlan", "doNot", "immune"));
        if (agent) needed.addAll(List.of("luke", "site"));
        var trace = new ArrayList<String>();
        boolean choseMode = false;
        for (int i = 0; i < 25 && !scn.IsCardActive(scn.GetLSCard("future")); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) break;
            var prompt = decision.getText();
            trace.add(prompt);
            needed.removeIf(key -> scn.IsCardActive(scn.GetLSCard(key)) || scn.GetLSHand().contains(scn.GetLSCard(key)));
            if (prompt.startsWith("Choose It Is The Future")) {
                scn.LSChooseOption(agent ? "Agent of Evil" : "Jedi"); choseMode = true;
            } else if (prompt.startsWith("Verify ")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else if (!needed.isEmpty() && scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetLSCard(needed.getFirst()))) {
                if ("immune".equals(needed.getFirst())) {
                    int ordinary = scn.LSGetBPChoices().indexOf("1_66");
                    var selectable = scn.LSGetADParamAsList("selectable");
                    assertTrue(ordinary < 0 || !Boolean.parseBoolean(selectable.get(ordinary)));
                }
                scn.LSChooseCard(scn.GetLSCard(needed.removeFirst()));
            } else if (prompt.toLowerCase().contains("optional response")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else {
                break;
            }
        }
        assertTrue(trace.toString(), choseMode);
        needed.removeIf(key -> scn.IsCardActive(scn.GetLSCard(key)) || scn.GetLSHand().contains(scn.GetLSCard(key)));
        assertTrue(trace + " remaining=" + needed, needed.isEmpty());
        var event = scn.GetLSCard("future");
        assertEquals(trace.toString(), Zone.SIDE_OF_TABLE, event.getZone());
        assertEquals(agent ? scn.DS : scn.LS, event.getZoneOwner());
        assertEquals(scn.LS, scn.GetCurrentPlayer());
        for (String effect : List.of("battlePlan", "doNot", "immune")) assertTrue(effect, scn.IsCardActive(scn.GetLSCard(effect)));
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("darkInsert")));
        assertFalse(scn.GetDSLostPile().contains(scn.GetDSCard("darkWeapon")));
        if (purge != null) assertTrue(purge, scn.GetLSLostPile().contains(scn.GetLSCard("purge")));
        return scn;
    }

    @Test public void agentTakesLukeAndSiteAndDeploysToTheDarkSide() {
        var scn = begin(true, "1_62");
        assertTrue(scn.GetLSHand().contains(scn.GetLSCard("luke")));
        assertTrue(scn.GetLSHand().contains(scn.GetLSCard("site")));
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Stack a card from hand")));
    }

    @Test public void jediPurgeUsesPrintedInsertAndIconScope() {
        begin(false, "1_42");
        begin(false, "13_9");
        begin(false, "108_3");
    }

    @Test public void jediStacksForZeroThenOneAndRefreshesOnlyAtTheStartOfOwnTurn() {
        var scn = begin(false, null); var event = scn.GetLSCard("future");
        var first = scn.GetLSCard("stackOne"); var second = scn.GetLSCard("stackTwo");
        scn.MoveCardsToLSHand(first, second);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(4);
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(event, "Stack a card from hand face up");
        scn.LSChooseCard(first); scn.PassAllResponses();
        assertEquals(Zone.STACKED, first.getZone());
        assertEquals(before, scn.GetLSForcePileCount());
        if (scn.DSGetDecision() != null) scn.DSPass();
        scn.LSUseCardAction(event, "Stack a card from hand face up");
        scn.PassForceUseResponses();
        scn.LSChooseCard(second); scn.PassAllResponses();
        assertEquals(Zone.STACKED, second.getZone());
        assertEquals(before - 1, scn.GetLSForcePileCount());
        scn.gameState().relocateCardAsStacked(first, event, true, false);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(Zone.STACKED_FACE_DOWN, first.getZone());
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.STACKED, first.getZone());
        assertEquals(event, first.getStackedOn());
        assertEquals(event, second.getStackedOn());
    }
    private void battleDestiny(boolean agent) {
        var scn = begin(agent, null);
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(3); scn.PrepareDSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(3, scn.GetLSTotalDestiny());
        // Native Premiere Vader adds one to the draw, while the Agent adds two to the total.
        assertEquals(agent ? 6 : 4, scn.GetDSTotalDestiny());
    }

    @Test public void actualAgentBattleAddsTwoToDarkSidesTotalOnly() { battleDestiny(true); }
    @Test public void actualJediModeDoesNotModifyEitherSidesBattleDestiny() { battleDestiny(false); }
    @Test public void startupPurgeSpansTableHandForceAndUsedWithExactOwnerScope() {
        var scn = begin(false, null, true);
        for (String key : List.of("tableMaintenance", "handWeapon", "forceRevolution")) {
            assertTrue(key, scn.GetLSLostPile().contains(scn.GetLSCard(key)));
        }
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("darkInsert")));
        assertTrue(scn.GetDSHand().contains(scn.GetDSCard("darkMaintenance")));
        assertTrue(scn.GetDSHand().contains(scn.GetDSCard("darkWeapon")));
        assertTrue(scn.GetLSHand().contains(scn.GetLSCard("ordinary")));
    }
    // --- "Instead of using X Force, may turn X cards here face down" (Bill 2026-11-07: any Force used during your turn) ---

    private void stackFaceUp(VirtualTableScenario scn, String... keys) {
        var event = scn.GetLSCard("future");
        for (String key : keys) scn.gameState().relocateCardAsStacked(scn.GetLSCard(key), event, false, false);
    }

    private void setForcePile(VirtualTableScenario scn, String player, int size) {
        while (scn.GetForcePile(player).size() > size)
            scn.MoveCardsToTopOfReserveDeck(player, (PhysicalCardImpl) scn.GetForcePile(player).getFirst());
        if (scn.GetForcePile(player).size() < size) scn.EnsureForcePile(player, size);
        assertEquals(size, scn.GetForcePile(player).size());
    }

    private long faceDown(VirtualTableScenario scn) {
        return scn.gameState().getStackedCards(scn.GetLSCard("future")).stream().filter(c -> c.getZone() == Zone.STACKED_FACE_DOWN).count();
    }

    private void startJessDeploy(VirtualTableScenario scn) {
        var jess = scn.GetLSCard("jess"); var site = scn.GetLSStartingLocation();
        scn.LSDeployCard(jess);
        if (scn.LSHasCardChoiceAvailable(site)) scn.LSChooseCard(site);
    }

    /** Pass the opponent through the "Use N Force - Optional responses" window until Light Side holds it. */
    private void toLSForceUseWindow(VirtualTableScenario scn) {
        for (int i = 0; i < 4 && !scn.LSDecisionAvailable("Force - Optional responses"); i++) {
            if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass(); else break;
        }
        assertTrue(scn.GetCurrentDecision().getText(), scn.LSDecisionAvailable("Force - Optional responses"));
    }

    private void finishDeploy(VirtualTableScenario scn) {
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("jess")));
    }

    private VirtualTableScenario jediDeployTurn(int forcePile, String... stacked) { return jediDeployTurn(-1, forcePile, stacked); }

    /** dsForcePile >= 0 also attaches Beggar and sets the Dark Side Force Pile. */
    private VirtualTableScenario jediDeployTurn(int dsForcePile, int forcePile, String... stacked) {
        var scn = begin(false, null);
        scn.MoveCardsToLSHand(scn.GetLSCard("jess"));
        scn.SkipToLSTurn(Phase.CONTROL);
        stackFaceUp(scn, stacked);
        setForcePile(scn, scn.LS, forcePile);
        if (dsForcePile >= 0) {
            scn.AttachCardsTo(scn.GetDSStartingLocation(), scn.GetLSCard("beggar"));
            setForcePile(scn, scn.DS, dsForcePile);
        }
        scn.SkipToPhase(Phase.DEPLOY); // fresh action list for the arranged state
        return scn;
    }

    @Test public void deployCostPaidEntirelyByTurningCardsFaceDown() {
        var scn = jediDeployTurn(4, "stackOne", "stackTwo", "stackThree");
        assertEquals(4 + 3, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.LS));
        startJessDeploy(scn);
        toLSForceUseWindow(scn);
        var event = scn.GetLSCard("future");
        assertTrue(scn.LSCardActionAvailable(event, "Turn cards face down instead of using Force"));
        scn.LSUseCardAction(event, "Turn cards face down instead of using Force");
        assertTrue(scn.LSDecisionAvailable("turn face down instead of using Force"));
        assertEquals(1, scn.LSGetChoiceMin());
        assertEquals(3, scn.LSGetChoiceMax()); // Jess deploys for 3
        scn.LSDecided(3);
        finishDeploy(scn);
        assertEquals(4, scn.GetLSForcePileCount());
        assertEquals(0, scn.GetLSUsedPileCount());
        assertEquals(3, faceDown(scn));
    }

    @Test public void deployCostPaidPartlyByTurningCardsFaceDown() {
        var scn = jediDeployTurn(4, "stackOne", "stackTwo", "stackThree");
        startJessDeploy(scn);
        toLSForceUseWindow(scn);
        scn.LSUseCardAction(scn.GetLSCard("future"), "Turn cards face down instead of using Force");
        scn.LSDecided(1);
        finishDeploy(scn);
        assertEquals(2, scn.GetLSForcePileCount());
        assertEquals(2, scn.GetLSUsedPileCount());
        assertEquals(1, faceDown(scn));
    }

    @Test public void faceUpCardsMakeAnUnaffordableDeployPlayableAndAreRequiredForTheShortfall() {
        var scn = jediDeployTurn(1, "stackOne", "stackTwo", "stackThree");
        assertTrue(scn.LSDeployAvailable(scn.GetLSCard("jess")));
        startJessDeploy(scn);
        assertTrue(scn.LSDecisionAvailable("turn face down instead of using Force"));
        assertEquals(2, scn.LSGetChoiceMin()); // 3 cost - 1 Force in Force Pile
        assertEquals(3, scn.LSGetChoiceMax());
        scn.LSDecided(2);
        finishDeploy(scn);
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(2, faceDown(scn));
    }

    @Test public void notOfferedWithoutFaceUpCardsAndUnaffordableDeployStaysUnavailable() {
        for (int forcePile : new int[]{1, 4}) {
            var scn = begin(false, null); var event = scn.GetLSCard("future");
            scn.MoveCardsToLSHand(scn.GetLSCard("jess"));
            scn.SkipToLSTurn(Phase.CONTROL);
            stackFaceUp(scn, "stackOne", "stackTwo");
            for (var card : scn.gameState().getStackedCards(event)) scn.gameState().relocateCardAsStacked(card, event, true, false);
            setForcePile(scn, scn.LS, forcePile);
            scn.SkipToPhase(Phase.DEPLOY);
            assertEquals(0, scn.game().getModifiersQuerying().getForceUseSubstitutesAvailable(scn.gameState(), scn.LS));
            if (forcePile == 1) {
                assertFalse(scn.LSDeployAvailable(scn.GetLSCard("jess")));
                continue;
            }
            startJessDeploy(scn);
            toLSForceUseWindow(scn);
            assertFalse(scn.LSCardActionAvailable(event, "Turn cards face down instead of using Force"));
            finishDeploy(scn);
            assertEquals(1, scn.GetLSForcePileCount());
        }
    }

    @Test public void notOfferedOnOpponentsTurnAndCardsTurnFaceUpAtStartOfOwnTurn() {
        var scn = jediDeployTurn(4, "stackOne", "stackTwo", "stackThree");
        var event = scn.GetLSCard("future");
        var querying = scn.game().getModifiersQuerying();
        assertEquals(scn.GetLSForcePileCount() + 3, querying.getForceAvailableToUse(scn.gameState(), scn.LS));
        startJessDeploy(scn);
        toLSForceUseWindow(scn);
        scn.LSUseCardAction(event, "Turn cards face down instead of using Force");
        scn.LSDecided(2);
        finishDeploy(scn);
        assertEquals(2, faceDown(scn));

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(2, faceDown(scn)); // only at the start of *your* turn
        assertEquals(0, querying.getForceUseSubstitutesAvailable(scn.gameState(), scn.LS));
        assertEquals(scn.GetLSForcePileCount(), querying.getForceAvailableToUse(scn.gameState(), scn.LS));
        var useForce = new UseForceEffect(new TopLevelGameTextAction(event, scn.LS, event.getCardId()), scn.LS, 1);
        assertTrue(scn.game().getActionsEnvironment().getOptionalBeforeTriggers(scn.LS, useForce).stream().noneMatch(a -> a.getActionSource() == event));
        assertTrue(scn.game().getActionsEnvironment().getRequiredBeforeTriggers(useForce).stream().noneMatch(a -> a.getActionSource() == event));

        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(0, faceDown(scn));
        assertEquals(3, querying.getForceUseSubstitutesAvailable(scn.gameState(), scn.LS));
    }

    @Test public void stackingCostCanItselfBePaidByTurningCardsFaceDown() {
        var scn = begin(false, null); var event = scn.GetLSCard("future");
        scn.MoveCardsToLSHand(scn.GetLSCard("stackThree"));
        scn.SkipToLSTurn(Phase.CONTROL);
        stackFaceUp(scn, "stackOne", "stackTwo");
        setForcePile(scn, scn.LS, 0);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(event, "Stack a card from hand face up"));
        scn.LSUseCardAction(event, "Stack a card from hand face up");
        assertTrue(scn.LSDecisionAvailable("turn face down instead of using Force"));
        assertEquals(2, scn.LSGetChoiceMin());
        scn.LSDecided(2);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("stackThree"))) scn.LSChooseCard(scn.GetLSCard("stackThree"));
        scn.PassAllResponses();
        assertEquals(Zone.STACKED, scn.GetLSCard("stackThree").getZone());
        assertEquals(2, faceDown(scn));
    }

    private VirtualTableScenario beggarTurn(int lsForce, int dsForce, String... stacked) {
        return jediDeployTurn(dsForce, lsForce, stacked);
    }

    @Test public void beggarAndFaceDownCardsCombineInTheOptionalWindow() {
        var scn = beggarTurn(4, 3, "stackOne", "stackTwo");
        startJessDeploy(scn);
        toLSForceUseWindow(scn);
        scn.LSUseCardAction(scn.GetLSCard("beggar"), "opponent's Force");
        assertEquals(3, scn.LSGetChoiceMax());
        scn.LSDecided(1);
        toLSForceUseWindow(scn);
        scn.LSUseCardAction(scn.GetLSCard("future"), "Turn cards face down instead of using Force");
        assertEquals(2, scn.LSGetChoiceMax()); // 3 cost - 1 opponent's Force
        scn.LSDecided(2);
        finishDeploy(scn);
        assertEquals(4, scn.GetLSForcePileCount());
        assertEquals(1, scn.GetDSUsedPileCount());
        assertEquals(2, scn.GetDSForcePileCount());
        assertEquals(2, faceDown(scn));
    }

    @Test public void shortfallLetsPlayerSplitBetweenFaceDownCardsAndBeggar() {
        var scn = beggarTurn(1, 3, "stackOne", "stackTwo");
        startJessDeploy(scn);
        // Shortfall 2 could be covered by Beggar, so turning cards face down is optional (0 allowed)
        assertTrue(scn.LSDecisionAvailable("turn face down instead of using Force"));
        assertEquals(0, scn.LSGetChoiceMin());
        assertEquals(2, scn.LSGetChoiceMax());
        scn.LSDecided(1);
        assertTrue(scn.LSDecisionAvailable("turn face down instead of using Force"));
        assertEquals(0, scn.LSGetChoiceMin());
        scn.LSDecided(0); // decline: the rest must come from Beggar
        assertTrue(scn.LSDecisionAvailable("opponent's Force to use"));
        assertEquals(1, scn.LSGetChoiceMin());
        scn.LSDecided(1);
        finishDeploy(scn);
        assertEquals(1, faceDown(scn));
        assertEquals(1, scn.GetDSUsedPileCount());
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(1, scn.GetLSUsedPileCount());
    }

    @Test public void beggarWithoutFaceUpCardsStillRequiresOpponentsForce() {
        var scn = beggarTurn(1, 3);
        startJessDeploy(scn);
        assertTrue(scn.LSDecisionAvailable("opponent's Force to use"));
        assertEquals(2, scn.LSGetChoiceMin());
        scn.LSDecided(2);
        finishDeploy(scn);
        assertEquals(2, scn.GetDSUsedPileCount());
        assertEquals(0, faceDown(scn));
    }

    private float duelTotalModifiers(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard character) {
        float total = 0;
        for (var modifier : scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.DUEL_TOTAL, character)) {
            total += modifier.getValue(scn.gameState(), scn.game().getModifiersQuerying(), character);
        }
        return total;
    }

    @Test public void agentAddsTwoToOpponentsDuelTotalOnly() {
        var scn = begin(true, null);
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        assertEquals(2, duelTotalModifiers(scn, scn.GetDSCard("vader")), scn.epsilon);
        assertEquals(0, duelTotalModifiers(scn, scn.GetLSCard("luke")), scn.epsilon);
    }

    @Test public void jediModeDoesNotModifyDuelTotals() {
        var scn = begin(false, null);
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        assertEquals(0, duelTotalModifiers(scn, scn.GetDSCard("vader")), scn.epsilon);
        assertEquals(0, duelTotalModifiers(scn, scn.GetLSCard("luke")), scn.epsilon);
    }
}
