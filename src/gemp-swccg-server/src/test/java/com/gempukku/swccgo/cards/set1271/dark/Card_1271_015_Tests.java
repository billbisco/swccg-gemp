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
        replaceLoss(false);
    }

    @Test public void VadersCyberneticEnhancementsReturnsTheActualLightsaberWithVader() {
        replaceLoss(true);
    }

    private void replaceLoss(boolean armed) {
        var s = prepareBoomaBattle("cybernetic");
        var vader = s.GetDSCard("vader");
        var equipment = s.GetDSCard("cybernetic");
        var lightsaber = s.GetDSCard("lightsaber");
        if (armed) s.AttachCardsTo(vader, lightsaber);
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
        if (armed && s.DSHasCardChoiceAvailable(lightsaber)) {
            s.DSChooseCard(lightsaber);
            s.PassAllResponses();
        }
        assertEquals(Zone.HAND, vader.getZone());
        assertEquals(armed, s.GetDSHand().contains(lightsaber));
        assertEquals(lost + 4, s.GetDSLostPileCount());
        assertEquals(Zone.STACKED_FACE_DOWN, equipment.getZone());
    }
}
