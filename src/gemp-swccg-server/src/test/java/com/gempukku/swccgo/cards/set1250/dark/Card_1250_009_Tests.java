package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_009_Tests {
    private static final String LOSE = "Lose Keder and opposing Political Effect or senator";

    private VirtualTableScenario scenario(boolean atSenate) {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("effect", "12_40", "senator", "12_28", "away", "12_29",
                        "leia", "1_17", "luke", "1_19", "ordinary", "1_66", "enemyGuard", "12_3")),
                new HashMap<>(Map.ofEntries(Map.entry("keder", "1250_9"), Map.entry("senate", "12_167"),
                        Map.entry("sidious", "14_78"), Map.entry("delegates", "12_159"),
                        Map.entry("guard", "12_100"), Map.entry("awayGuard", "12_100"),
                        Map.entry("friendlyEffect", "12_127"), Map.entry("friendlySenator", "12_110"),
                        Map.entry("lost1", "1_194"), Map.entry("lost2", "1_194"), Map.entry("lost3", "1_194"))),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var senate = scn.GetDSCard("senate");
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(atSenate ? senate : scn.GetDSStartingLocation(), scn.GetDSCard("keder"));
        return scn;
    }

    @Test public void KederTheBlackPrintedAttributesAndLoreMatch() {
        var scn = scenario(true);
        var b = scn.GetDSCard("keder").getBlueprint();
        assertEquals("Keder The Black", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon);
        assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals("A highly paid assassin and spy, Keder has infiltrated the Senate disguised as a Coruscant Guard. The identity of his target and his employer remains a mystery to all but him.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REPUBLIC)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REPUBLIC, Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.SPY, Keyword.CORUSCANT_GUARD, Keyword.ASSASSIN)));
    }

    @Test public void KederTheBlackAddsExactlyThreeToSidiousPoliticsOnlyWhileAtSenate() {
        var scn = scenario(true);
        var senate = scn.GetDSCard("senate");
        var sidious = scn.GetDSCard("sidious");
        var senator = scn.GetDSCard("friendlySenator");
        var keder = scn.GetDSCard("keder");
        scn.MoveCardsToLocation(senate, sidious, senator);
        var querying = scn.game().getModifiersQuerying();
        assertEquals(sidious.getBlueprint().getPolitics() + 3, querying.getPolitics(scn.gameState(), sidious), scn.epsilon);
        assertEquals(senator.getBlueprint().getPolitics(), querying.getPolitics(scn.gameState(), senator), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), keder);
        assertEquals(sidious.getBlueprint().getPolitics(), querying.getPolitics(scn.gameState(), sidious), scn.epsilon);
    }

    @Test public void KederTheBlackDoesNotRequireSidiousToBeAtSenateWithHim() {
        var scn = scenario(true);
        var sidious = scn.GetDSCard("sidious");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sidious);
        assertEquals(sidious.getBlueprint().getPolitics() + 3, scn.game().getModifiersQuerying().getPolitics(scn.gameState(), sidious), scn.epsilon);
    }

    private void delegateRetrieval(boolean atSenate, int retrieved) {
        var scn = scenario(atSenate);
        var senate = scn.GetDSCard("senate");
        scn.MoveCardsToLocation(senate, scn.GetDSCard("guard"), scn.GetDSCard("friendlySenator"), scn.GetLSCard("enemyGuard"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("awayGuard"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost1"), scn.GetDSCard("lost2"), scn.GetDSCard("lost3"));
        var delegates = scn.GetDSCard("delegates");
        scn.MoveCardsToDSHand(delegates);
        scn.SkipToPhase(Phase.DEPLOY);
        int before = scn.GetDSLostPileCount();
        assertTrue(scn.DSCardPlayAvailable(delegates, "Retrieve " + retrieved + " Force"));
        scn.DSPlayCard(delegates, "Retrieve " + retrieved + " Force");
        scn.PassAllResponses();
        assertEquals(before - retrieved, scn.GetDSLostPileCount());
    }

    @Test public void KederTheBlackAtSenateMakesActualDelegatesRetrieveForTwoOwnGuards() {
        delegateRetrieval(true, 2);
    }
    @Test public void KederTheBlackAwayFromSenateLeavesActualDelegatesRetrievalForSenator() {
        delegateRetrieval(false, 1);
    }

    private void payAndResolve(VirtualTableScenario scn, PhysicalCardImpl target) {
        var keder = scn.GetDSCard("keder");
        var before = scn.GetDSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(keder, LOSE));
        scn.DSUseCardAction(keder, LOSE);
        if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
        scn.PassAllResponses();
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, keder);
        assertInZone(Zone.LOST_PILE, target);
        assertTrue(scn.gameState().getLostPile(scn.DS).contains(keder));
        assertTrue(scn.gameState().getLostPile(scn.LS).contains(target));
        assertEquals(before - 1, scn.GetDSLifeForceRemaining());
    }

    @Test public void KederTheBlackLosesOneForceAndBothCardsForOpposingPoliticalEffect() {
        var scn = scenario(true);
        var effect = scn.GetLSCard("effect");
        scn.MoveCardsToSideOfTable(effect);
        scn.SkipToPhase(Phase.CONTROL);
        payAndResolve(scn, effect);
    }

    @Test public void KederTheBlackLosesOneForceAndBothCardsForOpposingCoruscantSenatorHere() {
        var scn = scenario(true);
        var senator = scn.GetLSCard("senator");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), senator);
        scn.SkipToPhase(Phase.CONTROL);
        payAndResolve(scn, senator);
    }

    @Test public void KederTheBlackCanChooseOneOfTwoPrintedTargetTypes() {
        var scn = scenario(true);
        var effect = scn.GetLSCard("effect");
        var senator = scn.GetLSCard("senator");
        scn.MoveCardsToSideOfTable(effect);
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), senator);
        scn.SkipToPhase(Phase.CONTROL);
        payAndResolve(scn, effect);
        assertInZone(Zone.AT_LOCATION, senator);
    }

    @Test public void KederTheBlackAtSenateCannotChooseFriendlyEffectsOrSenators() {
        var scn = scenario(true);
        scn.MoveCardsToSideOfTable(scn.GetDSCard("friendlyEffect"));
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), scn.GetDSCard("friendlySenator"));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("keder"), LOSE));
    }

    @Test public void KederTheBlackCannotChooseDistantOrNonCoruscantSenatorsAndOrdinaryEffect() {
        var scn = scenario(true);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("away"));
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), scn.GetLSCard("leia"), scn.GetLSCard("luke"));
        scn.MoveCardsToSideOfTable(scn.GetLSCard("ordinary"));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("keder"), LOSE));
    }

    @Test public void KederTheBlackAwayFromSenateCannotMakeOpposingPoliticalEffectLost() {
        var scn = scenario(false);
        scn.MoveCardsToSideOfTable(scn.GetLSCard("effect"));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("keder"), LOSE));
    }

    @Test public void KederTheBlackMayDeclineAssassinationKeepingCardsAndForce() {
        var scn = scenario(true);
        var effect = scn.GetLSCard("effect");
        var keder = scn.GetDSCard("keder");
        scn.MoveCardsToSideOfTable(effect);
        scn.SkipToPhase(Phase.CONTROL);
        int before = scn.GetDSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(keder, LOSE));
        scn.DSPass();
        assertInZone(Zone.AT_LOCATION, keder);
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        assertEquals(before, scn.GetDSLifeForceRemaining());
    }
}
