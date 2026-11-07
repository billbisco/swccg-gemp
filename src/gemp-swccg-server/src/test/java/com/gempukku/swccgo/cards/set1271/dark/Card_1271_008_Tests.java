package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_008_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "journal", "10_15")),
            new HashMap<>(Map.of("effect", "1271_8")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void ASithsWeaponPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("effect").getBlueprint();
        assertEquals("A Sith's Weapon", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(4, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.SETUP, Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario board(boolean journalOnTable) {
        var s = scenario();
        s.StartGame();
        var obi = s.GetLSCard("obi");
        var journal = s.GetLSCard("journal");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), obi);
        s.MoveCardsToDSSideOfTable(s.GetDSCard("effect"));
        if (journalOnTable) s.AttachCardsTo(obi, journal);
        else s.MoveCardsToLSHand(journal);
        return s;
    }

    @Test public void ASithsWeaponActuallyPaysItsUsedPileCostToCancelAnAttachedJournal() {
        var s = board(true);
        var effect = s.GetDSCard("effect");
        s.SkipToDSTurn(Phase.CONTROL);
        s.DSUseCardAction(effect, "Cancel Obi-Wan's Journal");
        if (s.DSHasCardChoiceAvailable(s.GetLSCard("journal"))) s.DSChooseCard(s.GetLSCard("journal"));
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("journal")));
        assertTrue(s.GetDSUsedPile().contains(effect));
    }

    @Test public void ASithsWeaponActuallyCancelsAJournalAsItIsDeployed() {
        var s = board(false);
        var effect = s.GetDSCard("effect");
        var journal = s.GetLSCard("journal");
        var obi = s.GetLSCard("obi");
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(journal);
        if (s.LSHasCardChoiceAvailable(obi)) s.LSChooseCard(obi);
        if (s.DSGetDecision() == null && s.LSGetDecision() != null) s.LSPass();
        s.DSUseCardAction(effect, "Cancel");
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(journal));
        assertTrue(s.GetDSUsedPile().contains(effect));
    }

    @Test public void ASithsWeaponDoesNotOfferCancellationWhenNoJournalIsInPlay() {
        var s = board(false);
        s.SkipToDSTurn(Phase.CONTROL);
        assertFalse(s.DSCardActionAvailable(s.GetDSCard("effect"), "Cancel Obi-Wan's Journal"));
    }
}
