package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.set1261.LegacyInsertPodraceShieldTests;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;
import static org.junit.Assert.*;

public class Card_1261_073_Tests extends LegacyInsertPodraceShieldTests {
    protected boolean dark() { return false; }
    protected String shieldId() { return "1261_73"; }
    protected String primaryId() { return "1261_73"; }
    protected String conversionId() { return "1261_75"; }
    protected String title() { return "Your Insight Serves You Well"; }

    @Test public void WattosBoxSuspensionBeginsAtThreeBattlegroundsIncludesFourAndEndsBelowThree() {
        var s = ready(true); var box = s.GetDSCard("wattos"); table(s, false, box);
        s.MoveLocationToTable(s.GetLSCard("cantina")); s.MoveLocationToTable(s.GetLSCard("farm"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("char1"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("char2"));
        s.SkipToPhase(Phase.DEPLOY); assertFalse(box.isSuspended());
        s.MoveCardsToLocation(s.GetLSCard("cantina"), s.GetLSCard("char3"));
        s.SkipToPhase(Phase.BATTLE); assertTrue(box.isSuspended());
        s.MoveCardsToLocation(s.GetLSCard("farm"), s.GetLSCard("trooper"));
        s.SkipToPhase(Phase.MOVE); assertTrue(box.isSuspended());
        hand(s, true, s.GetLSCard("trooper"), s.GetLSCard("char3"));
        s.SkipToPhase(Phase.DRAW); assertFalse(box.isSuspended());
    }

    @Test public void TwoBattlegroundsPlusANonBattlegroundDoNotSuspendWattosBox() {
        var s = ready(true); var box = s.GetDSCard("wattos"); table(s, false, box);
        s.MoveLocationToTable(s.GetLSCard("nonBattleground"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("char1"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("char2"));
        s.MoveCardsToLocation(s.GetLSCard("nonBattleground"), s.GetLSCard("char3"));
        s.SkipToPhase(Phase.DEPLOY); assertFalse(box.isSuspended());
    }

    @Test public void ScanningCrewIsActuallyCanceledBeforeItCanMoveTheRebelFromHand() {
        var s = ready(true); var scanner = s.GetDSCard("scanner"); var luke = s.GetLSCard("char1");
        hand(s, true, luke); hand(s, false, scanner); turn(s, false, Phase.DEPLOY); force(s, false, 5);
        int before = force(s, false); s.DSPlayCard(scanner); s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(scanner)); assertEquals(Zone.HAND, luke.getZone());
        assertEquals(before - 1, force(s, false));
    }
}
