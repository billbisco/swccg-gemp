package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_013_Tests {
    @Test public void VadersBionicLimbsPrintedStatsIconsAndKeywordsAreCorrect() {
        equipmentStats("limbs", "Vader's Bionic Limbs", 0, false);
    }

    @Test public void VadersBionicLimbsIncreasesLandspeedOfItsWearer() {
        var s = board("limbs");
        assertEquals(2, s.GetLandspeed(s.GetDSCard("vader")));
        assertEquals(1, s.GetLandspeed(s.GetDSCard("trooper")));
    }

    @Test public void VadersBionicLimbsTurnsActualLossBeforeDamageIntoAHit() {
        var s = prepareBoomaBattle("limbs");
        fireBooma(s);
        s.PassAllResponses();
        assertTrue(s.GetDSCard("vader").isHit());
        assertTrue(s.GetDSCard("vader").getZone().isInPlay());
    }

    @Test public void VadersBionicLimbsChokesWithDestinyAboveAbility() { choke(5, true); }
    @Test public void VadersBionicLimbsDoesNotChokeWithDestinyEqualToAbility() { choke(4, false); }

    private void choke(int destiny, boolean lost) {
        var s = board("limbs");
        var luke = s.GetLSCard("luke");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), luke);
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(10);
        s.PrepareDSDestiny(destiny);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, s.GetDSStartingLocation());
        s.DSUseCardAction(s.GetDSCard("limbs"), "'Choke' a character");
        if (s.DSHasCardChoiceAvailable(luke)) s.DSChooseCard(luke);
        s.PassAllResponses();
        s.PassDestinyDrawResponses();
        s.PassAllResponses();
        assertEquals(lost, s.GetLSLostPile().contains(luke));
    }
}
