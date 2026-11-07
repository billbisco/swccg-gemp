package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.set1261.LegacyInsertPodraceShieldTests;
import com.gempukku.swccgo.common.Phase;
import org.junit.Test;
import static org.junit.Assert.*;

public class Card_1261_027_Tests extends LegacyInsertPodraceShieldTests {
    protected boolean dark() { return true; }
    protected String shieldId() { return "1261_27"; }
    protected String primaryId() { return "1261_27"; }
    protected String conversionId() { return "1261_28"; }
    protected String title() { return "You Cannot Hide Forever"; }

    @Test public void YouCannotHideForeverSuspensionRequiresStrictlyMoreBattlegroundsAndUpdatesAsOccupationChanges() {
        var s = ready(true); var didIt = s.GetLSCard("didIt"); table(s, false, didIt);
        s.MoveLocationToTable(s.GetLSCard("cantina")); s.MoveLocationToTable(s.GetLSCard("farm"));
        assertFalse(didIt.isSuspended()); // zero versus zero
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("char1"));
        s.SkipToPhase(Phase.DEPLOY); assertTrue(didIt.isSuspended()); // one versus zero
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("char1"));
        s.SkipToPhase(Phase.BATTLE); assertFalse(didIt.isSuspended()); // one versus one
        s.MoveCardsToLocation(s.GetLSCard("cantina"), s.GetDSCard("char2"));
        s.SkipToPhase(Phase.MOVE); assertTrue(didIt.isSuspended()); // two versus one
        s.MoveCardsToLocation(s.GetLSCard("farm"), s.GetLSCard("char2"));
        s.SkipToPhase(Phase.DRAW); assertFalse(didIt.isSuspended()); // two versus two
    }

    @Test public void YouCannotHideForeverNonBattlegroundOccupationDoesNotSuspendIDidIt() {
        var s = ready(true); var didIt = s.GetLSCard("didIt"); table(s, false, didIt);
        s.MoveLocationToTable(s.GetLSCard("nonBattleground"));
        s.MoveCardsToLocation(s.GetLSCard("nonBattleground"), s.GetDSCard("char1"));
        s.SkipToPhase(Phase.DEPLOY); assertFalse(didIt.isSuspended());
    }
    @Test public void YouCannotHideForeverFinalPrintedStatsIconsAndKeywordsAreCorrect() { checkFinalPrintedStatsIconsAndKeywordsAreCorrect(); }
    @Test public void YouCannotHideForeverBothPrintingLookupsRetainPhysicalImageIdsAndOneCanonicalRuleClass() { checkBothPrintingLookupsRetainPhysicalImageIdsAndOneCanonicalRuleClass(); }
    @Test public void YouCannotHideForeverShieldActuallyPlaysFromTheStartingEffectForFree() { checkShieldActuallyPlaysFromTheStartingEffectForFree(); }
    @Test public void YouCannotHideForeverASecondPrintingCannotBePlayedAlongsideTheSameShieldTitle() { checkASecondPrintingCannotBePlayedAlongsideTheSameShieldTitle(); }
    @Test public void YouCannotHideForeverBothPrintingsCountTowardTheExistingOutsideDeckShieldCap() throws com.gempukku.swccgo.game.DeckInvalidException { checkBothPrintingsCountTowardTheExistingOutsideDeckShieldCap(); }
    @Test public void YouCannotHideForeverAnOpponentsActualInsertPlayIsCanceled() { checkAnOpponentsActualInsertPlayIsCanceled(); }
    @Test public void YouCannotHideForeverAnOwnersActualInsertPlayIsAlsoCanceled() { checkAnOwnersActualInsertPlayIsAlsoCanceled(); }
    @Test public void YouCannotHideForeverCancelingARevealedInsertAllowsActualActivationToContinue() { checkCancelingARevealedInsertAllowsActualActivationToContinue(); }
    @Test public void YouCannotHideForeverShieldDoesNotCancelAnOrdinaryCharacterDeployment() { checkShieldDoesNotCancelAnOrdinaryCharacterDeployment(); }
    @Test public void YouCannotHideForeverActualPodraceFromOwnersCardProtectsLossAndOpponentsRetrieval() { checkActualPodraceFromOwnersCardProtectsLossAndOpponentsRetrieval(); }
    @Test public void YouCannotHideForeverActualPodraceFromOpponentsCardAlsoProtectsLossAndRetrieval() { checkActualPodraceFromOpponentsCardAlsoProtectsLossAndRetrieval(); }
    @Test public void YouCannotHideForeverWithoutTheShieldTheSameRaceLosesForceAndRetrievalCanBeCanceled() { checkWithoutTheShieldTheSameRaceLosesForceAndRetrievalCanBeCanceled(); }
}
