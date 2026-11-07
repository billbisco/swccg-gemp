package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_017_Tests {
    @Test public void VadersMaskPrintedStatsIconsAndKeywordsAreCorrect() {
        equipmentStats("mask", "Vader's Mask", 0, false);
    }

    @Test public void VadersMaskCanCancelItselfForAnActualRegularMoveDuringTheDarkBattlePhase() {
        var s = board("mask");
        var vader = s.GetDSCard("vader");
        var farm = s.GetLSCard("farm");
        var mask = s.GetDSCard("mask");
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.BATTLE);
        s.DSUseCardAction(mask, "Cancel Vader's Mask to move Vader");
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(farm)) s.DSChooseCard(farm);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(farm, vader));
        assertEquals(Zone.STACKED_FACE_DOWN, mask.getZone());
    }

    @Test public void VadersMaskGrantsPowerAndFullAttritionImmunityOnlyWhenDefendingAtAnOpponentsSite() {
        checkDefending(true);
        checkDefending(false);
    }

    private void checkDefending(boolean opponentOwnsSite) {
        var s = board("mask");
        var vader = s.GetDSCard("vader");
        var site = opponentOwnsSite ? s.GetLSCard("farm") : s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, vader, s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.EnsureLSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.LS, site);
        assertEquals(opponentOwnsSite ? 8 : 6, s.GetPower(vader), s.epsilon);
        assertEquals(opponentOwnsSite ? Float.MAX_VALUE : 5f,
                s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), vader), s.epsilon);
    }
}
