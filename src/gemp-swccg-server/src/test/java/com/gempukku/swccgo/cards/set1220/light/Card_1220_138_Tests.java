package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
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

    private static final String FACE_DOWN = "face down instead of using Force to stack a card from hand";

    @Test public void jediMayTurnXCardsFaceDownInsteadOfUsingXForce() {
        var scn = begin(false, null); var event = scn.GetLSCard("future");
        var first = scn.GetLSCard("stackOne"); var second = scn.GetLSCard("stackTwo");
        var third = scn.GetLSCard("stackThree"); var fourth = scn.GetLSCard("ordinary");
        scn.MoveCardsToLSHand(first, second, third, fourth);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(6);
        // Nothing stacked yet: no Force to replace, so the face-down payment is not offered.
        assertFalse(scn.LSCardActionAvailable(event, FACE_DOWN));
        scn.LSUseCardAction(event, "Stack a card from hand face up");
        scn.LSChooseCard(first); scn.PassAllResponses();
        if (scn.DSGetDecision() != null) scn.DSPass();
        // X = 1 and one face-up card here: may turn it face down instead of using 1 Force.
        assertTrue(scn.LSCardActionAvailable(event, FACE_DOWN));
        scn.LSUseCardAction(event, "Stack a card from hand face up");
        scn.PassForceUseResponses();
        scn.LSChooseCard(second); scn.PassAllResponses();
        if (scn.DSGetDecision() != null) scn.DSPass();
        // X = 2 with two face-up cards: turn both face down instead of using 2 Force.
        int before = scn.GetLSForcePileCount();
        assertTrue(scn.GetLSAvailableActions().toString(), scn.LSCardActionAvailable(event, FACE_DOWN));
        scn.LSUseCardAction(event, FACE_DOWN);
        scn.LSChooseCard(third); scn.PassAllResponses();
        assertEquals("No Force used", before, scn.GetLSForcePileCount());
        assertEquals(Zone.STACKED_FACE_DOWN, first.getZone());
        assertEquals(Zone.STACKED_FACE_DOWN, second.getZone());
        assertEquals(Zone.STACKED, third.getZone());
        assertEquals(event, third.getStackedOn());
        if (scn.DSGetDecision() != null) scn.DSPass();
        // X = 3 but only one card here is face up: the face-down payment is not available (Force still is).
        assertFalse(scn.LSCardActionAvailable(event, FACE_DOWN));
        assertTrue(scn.LSCardActionAvailable(event, "Stack a card from hand face up"));
        // Face-down cards are turned face up again at the start of LS's next turn.
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.STACKED, first.getZone());
        assertEquals(Zone.STACKED, second.getZone());
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
