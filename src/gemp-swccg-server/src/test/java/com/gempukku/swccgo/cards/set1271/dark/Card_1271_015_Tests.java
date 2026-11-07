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

    @Test public void VadersCyberneticEnhancementsCancelsActualUncontrollableFuryWithoutCancelingFriendlyEquipment() {
        var s = board("cybernetic");
        var vader = s.GetDSCard("vader");
        var fury = s.GetLSCard("fury");
        var cape = s.GetDSCard("cape");
        s.AttachCardsTo(vader, cape);
        s.MoveCardsToLSHand(fury);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(fury);
        if (s.LSHasCardChoiceAvailable(vader)) s.LSChooseCard(vader);
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(fury)) s.DSChooseCard(fury);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(fury));
        assertEquals(vader, cape.getAttachedTo());
    }

    @Test public void VadersCyberneticEnhancementsAddsOneToDarkBattleDestinyAtVadersSite() {
        var s = board("cybernetic");
        var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, site);
        s.PrepareDSDestiny(3);
        s.PrepareLSDestiny(2);
        s.SkipToEndOfPowerSegment(true);
        assertEquals(4, s.GetDSTotalDestiny());
        assertEquals(2, s.GetLSTotalDestiny());
    }

    @Test public void VadersCyberneticEnhancementsLetsVaderReactIntoAnActualAdjacentBattle() {
        var s = board("cybernetic");
        var vader = s.GetDSCard("vader");
        var equipment = s.GetDSCard("cybernetic");
        var farm = s.GetLSCard("farm");
        s.MoveCardsToLocation(farm, s.GetLSCard("luke"), s.GetDSCard("trooper"));
        s.SkipToLSTurn(Phase.BATTLE);
        s.DSActivateForceCheat(4);
        s.LSInitiateBattle(farm);
        assertTrue(s.DSDecisionAvailable("Battle just initiated"));
        s.DSUseCardAction(equipment, "Move Vader as a 'react'");
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        if (s.DSHasCardChoiceAvailable(farm)) s.DSChooseCard(farm);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(farm, vader));
        assertTrue(s.gameState().getBattleState().getCardsParticipating(s.DS).contains(vader));
    }
}
