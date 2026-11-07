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
        var farm = s.GetLSCard("farm");
        s.MoveCardsToLocation(farm, s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.MOVE);
        s.DSMoveCard(vader, farm);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(farm, vader));
        assertTrue(s.LSDecisionAvailable("Choose Force to lose"));
    }

    @Test public void VadersHelmetRaisesOneBattleDestinyToExactlyTwoAgainstHigherAbility() {
        checkBattleDestiny("obi", 2);
        checkBattleDestiny("luke", 1);
    }

    private void checkBattleDestiny(String opponent, int expectedDraws) {
        var s = board("helmet");
        var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetLSCard(opponent));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, site);
        s.SkipToPowerSegment();
        s.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(s.DSDecisionAvailable("Do you want to draw " + expectedDraws + " battle destiny?"));
    }

    @Test public void VadersHelmetPlacesAnActualJournalAtSameSiteOnTheBottomOfLostPile() {
        var s = board("helmet");
        var obi = s.GetLSCard("obi");
        var journal = s.GetLSCard("journal");
        var priorLost = s.GetLSCard("luke");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), obi);
        s.AttachCardsTo(obi, journal);
        s.MoveCardsToTopOfLSLostPile(priorLost);
        s.SkipToDSTurn(Phase.CONTROL);
        s.DSUseCardAction(s.GetDSCard("helmet"), "Place Obi-Wan's Journal on bottom");
        if (s.DSHasCardChoiceAvailable(journal)) s.DSChooseCard(journal);
        s.PassAllResponses();
        assertEquals(priorLost, s.GetLSLostPile().getFirst());
        assertEquals(journal, s.GetLSLostPile().getLast());
    }
}
