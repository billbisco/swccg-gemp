package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_016_Tests {
    @Test public void VadersHelmetPrintedStatsIconsAndKeywordsAreCorrect() {
        equipmentStats("helmet", "Vader's Helmet", 0, false, Icon.DAGOBAH);
    }

    @Test public void VadersHelmetPowerBonusRequiresOpponentsAbilityAboveFourWithVader() {
        var s = board("helmet");
        var vader = s.GetDSCard("vader");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(6, s.GetPower(vader));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("obi"));
        assertEquals(7, s.GetPower(vader));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("obi"));
        assertEquals(6, s.GetPower(vader));
    }

    @Test public void VadersHelmetMakesOpponentLoseForceWhenVaderActuallyMovesToASkywalker() {
        var s = board("helmet");
        var vader = s.GetDSCard("vader");
        var farm = s.GetDSCard("farm");
        s.MoveCardsToLocation(farm, s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.MOVE);
        s.DSMoveCard(vader, farm);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(farm, vader));
        assertTrue(s.LSDecisionAvailable("Choose Force to lose"));
    }
}
