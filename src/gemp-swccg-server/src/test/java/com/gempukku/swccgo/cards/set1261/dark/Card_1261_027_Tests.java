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

    @Test public void SuspensionRequiresStrictlyMoreBattlegroundsAndUpdatesAsOccupationChanges() {
        var s = ready(true); var didIt = s.GetLSCard("didIt"); table(s, false, didIt);
        s.MoveLocationToTable(s.GetLSCard("cantina")); s.MoveLocationToTable(s.GetLSCard("farm"));
        s.SkipToPhase(Phase.DEPLOY); assertFalse(didIt.isSuspended()); // zero versus zero
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("char1"));
        s.SkipToPhase(Phase.BATTLE); assertTrue(didIt.isSuspended()); // one versus zero
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("char1"));
        s.SkipToPhase(Phase.MOVE); assertFalse(didIt.isSuspended()); // one versus one
        s.MoveCardsToLocation(s.GetLSCard("cantina"), s.GetDSCard("char2"));
        s.SkipToPhase(Phase.DRAW); assertTrue(didIt.isSuspended()); // two versus one
        s.MoveCardsToLocation(s.GetLSCard("farm"), s.GetLSCard("char2"));
        turn(s, true, Phase.CONTROL); assertFalse(didIt.isSuspended()); // two versus two
    }

    @Test public void NonBattlegroundOccupationDoesNotSuspendIDidIt() {
        var s = ready(true); var didIt = s.GetLSCard("didIt"); table(s, false, didIt);
        s.MoveLocationToTable(s.GetLSCard("nonBattleground"));
        s.MoveCardsToLocation(s.GetLSCard("nonBattleground"), s.GetDSCard("char1"));
        s.SkipToPhase(Phase.DEPLOY); assertFalse(didIt.isSuspended());
    }
}
