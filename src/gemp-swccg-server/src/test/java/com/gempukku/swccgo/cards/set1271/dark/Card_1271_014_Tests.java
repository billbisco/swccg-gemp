package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_014_Tests {
    @Test public void VadersCapePrintedStatsIconsAndKeywordsAreCorrect() {
        equipmentStats("cape", "Vader's Cape", 3, true, Icon.CLOUD_CITY);
    }

    @Test public void VadersCapeMakesActualLandspeedMoveFree() {
        var s = board("cape");
        var vader = s.GetDSCard("vader");
        var farm = s.GetLSCard("farm");
        s.SkipToDSTurn(Phase.CONTROL);
        s.DrainDSForcePileTo(0);
        s.SkipToPhase(Phase.MOVE);
        s.DSMoveCard(vader, farm);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(farm, vader));
        assertEquals(0, s.GetDSForcePileCount());
    }

    @Test public void VadersCapeDefenseBonusRequiresAJediOrSkywalkerWithVader() {
        var s = board("cape");
        var vader = s.GetDSCard("vader");
        int before = s.GetDefense(vader);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(before + 2, s.GetDefense(vader));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(before, s.GetDefense(vader));
    }

    private void fireAtLuke(boolean cape) {
        var s = board(cape ? "cape" : null);
        var vader = s.GetDSCard("vader");
        var lightsaber = s.GetDSCard("lightsaber");
        var luke = s.GetLSCard("luke");
        var site = s.GetDSStartingLocation();
        s.AttachCardsTo(vader, lightsaber);
        s.MoveCardsToLocation(site, luke);
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, site);
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(1), s.GetDSDestiny(3));
        assertEquals(4, s.GetDefense(luke));
        s.DSUseCardAction(lightsaber, "Fire");
        if (s.DSHasCardChoiceAvailable(luke)) s.DSChooseCard(luke);
        s.PassWeaponFireWithDestinyDraw(2);
        s.PassAllResponses();
        assertEquals(cape, luke.isHit());
    }

    @Test public void VadersCapeTurnsAnActualBoundaryLightsaberSwingIntoAHitAgainstLuke() {
        fireAtLuke(false);
        fireAtLuke(true);
    }
}
