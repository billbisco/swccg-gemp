package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("trust", "1271_39", "attack", "2_42", "setup", "1271_46", "immediate", "12_38", "luke", "1_19")),
            new HashMap<>(Map.of("epic", "5_129", "trooper", "1_194")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void LukeTrustMePrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetLSCard("trust").getBlueprint();
        assertEquals("Luke, Trust Me", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(3, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isImmuneToCardTitle(Title.Alter));
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario ready(boolean ownEpic) {
        var s = scenario();
        s.StartGame();
        s.MoveCardsToDSSideOfTable(s.GetDSCard("epic"));
        if (ownEpic) s.MoveCardsToLSSideOfTable(s.GetLSCard("attack"));
        s.MoveCardsToLSHand(s.GetLSCard("trust"));
        s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("setup"), s.GetLSCard("immediate"), s.GetLSCard("luke"));
        return s;
    }

    private void deploy(VirtualTableScenario s) {
        var trust = s.GetLSCard("trust");
        s.LSDeployCard(trust);
        s.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, trust.getZone());
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
    }

    @Test public void LukeTrustMeDeploysWhenOnlyOpponentHasAnEpicEventOnTable() {
        deploy(ready(false));
    }

    @Test public void LukeTrustMeCannotDeployWhenItsOwnerHasAnEpicEventOnTable() {
        var s = ready(true);
        assertFalse(s.LSDeployAvailable(s.GetLSCard("trust")));
    }

    @Test public void LukeTrustMeCannotDeployAfterItsOwnersStartingEpicEventLeavesTheTable() {
        var s = new VirtualTableScenario(
            new HashMap<>(Map.of("trust", "1271_39", "interrupt", "216_39", "communing", "216_24", "kenobi", "216_36")),
            new HashMap<>(), 30, 30,
            StartingSetup.LSStartingLocation("216_43"), StartingSetup.DefaultDSSpaceSystem,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var communing = s.GetLSCard("communing");
        var kenobi = s.GetLSCard("kenobi");
        var trust = s.GetLSCard("trust");
        s.MoveCardsToLSHand(trust);
        s.DSDecided("0");
        s.LSDecided("0");
        StartingSetup.DefaultDSSpaceSystem.Setup(s);
        s.LSChooseCard(s.GetLSCard("starting-location"));

        assertTrue("Expected the starting interrupt choice, got " + s.LSGetDecision(),
            s.LSDecisionAvailable("Choose starting interrupt"));
        s.LSChooseCard(s.GetLSCard("interrupt"));
        if (s.LSHasCardChoiceAvailable(communing)) s.LSChooseCard(communing);
        if (s.LSHasCardChoiceAvailable(kenobi)) s.LSChooseCard(kenobi);
        assertEquals(Zone.SIDE_OF_TABLE, communing.getZone());
        assertTrue(s.gameState().hasPlayedStartingEpicEvent(s.LS));

        s.MoveOutOfPlay(communing);
        s.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(Zone.HAND, trust.getZone());
        assertFalse(s.LSDeployAvailable(trust));
    }

    @Test public void LukeTrustMeActuallySearchesForSetupAndReshufflesOnlyOncePerGame() {
        var s = ready(false);
        deploy(s);
        var trust = s.GetLSCard("trust");
        var observer = LegacyShuffleObserver.observe(s);
        s.LSUseCardAction(trust, "Take a Setup card");
        s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("setup"));
        s.PassAllResponses();
        assertEquals(Zone.HAND, s.GetLSCard("setup").getZone());
        assertTrue(observer.sawOwnedShuffle(s.LS, Zone.RESERVE_DECK));
        s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.LSCardActionAvailable(trust, "Take a Setup card"));
    }

    @Test public void LukeTrustMeActuallyPlacesItselfInUsedToSearchForAnImmediateEffect() {
        var s = ready(false);
        deploy(s);
        var trust = s.GetLSCard("trust");
        var observer = LegacyShuffleObserver.observe(s);
        s.LSUseCardAction(trust, "Immediate Effect");
        s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("immediate"));
        s.PassAllResponses();
        assertEquals(Zone.HAND, s.GetLSCard("immediate").getZone());
        assertTrue(s.GetLSUsedPile().contains(trust));
        assertTrue(observer.sawOwnedShuffle(s.LS, Zone.RESERVE_DECK));
    }

    @Test public void LukeTrustMeActuallyPlacesItselfInLostToCancelAForceDrain() {
        var s = ready(false);
        deploy(s);
        var trust = s.GetLSCard("trust");
        var site = s.GetLSStartingLocation();
        s.MoveCardsToLocation(site, s.GetDSCard("trooper"));
        s.SkipToDSTurn(Phase.CONTROL);
        int before = s.GetLSLifeForceRemaining();
        s.DSForceDrainAt(site);
        if (s.DSGetDecision() != null) s.DSPass();
        s.LSUseCardAction(trust, "cancel Force drain");
        s.PassAllResponses();
        assertFalse(s.IsActiveForceDrain());
        assertEquals(before, s.GetLSLifeForceRemaining());
        assertTrue(s.GetLSLostPile().contains(trust));
    }
}
