package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_015_Tests {
    @Test public void VadersCyberneticEnhancementsPrintedStatsIconsAndKeywordsAreCorrect() {
        equipmentStats("cybernetic", "Vader's Cybernetic Enhancements", 0, false, Icon.DAGOBAH);
    }

    @Test public void VadersCyberneticEnhancementsAllowsActualLossReplacementForFourForce() {
        var s = prepareBoomaBattle("cybernetic");
        var vader = s.GetDSCard("vader");
        var equipment = s.GetDSCard("cybernetic");
        fireBooma(s);
        assertTrue(s.DSCardActionAvailable(equipment, "Lose 4 Force to take Vader"));
        int lost = s.GetDSLostPileCount();
        s.DSUseCardAction(equipment, "Lose 4 Force to take Vader");
        for (int i = 0; i < 4; i++) {
            s.PassAllResponses();
            assertTrue(s.DSDecisionAvailable("Choose Force to lose"));
            s.DSDecided(s.DSGetADParam("cardId")[0]);
        }
        s.PassAllResponses();
        assertEquals(Zone.HAND, vader.getZone());
        assertEquals(lost + 4, s.GetDSLostPileCount());
        assertEquals(Zone.STACKED_FACE_DOWN, equipment.getZone());
    }
}
