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
        var farm = s.GetDSCard("farm");
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
}
