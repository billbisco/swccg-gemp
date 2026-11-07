package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_019_Tests {
    @Test public void RiseOfTheDarkLordPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("rise").getBlueprint();
        assertEquals("Rise Of The Dark Lord", b.getTitle());
        assertEquals(0, b.getDestiny(), s.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EPIC_EVENT));
        s.BlueprintKeywordCheck(b, List.of());
        s.BlueprintIconCheck(b, List.of(Icon.EPIC_EVENT, Icon.SETUP, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_7));
    }

    @Test public void RiseOfTheDarkLordDoesNotDeployInAnOrdinaryDeployPhase() {
        var s = scenario();
        s.StartGame();
        s.SkipToDSTurn(Phase.CONTROL);
        var rise = s.GetDSCard("rise");
        s.MoveCardsToDSHand(rise);
        s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.DSDeployAvailable(rise));
    }

    @Test public void RiseOfTheDarkLordActuallyDeploysItsStackedEffectButRejectsHandAndRepeatDeployment() {
        var s = board(null);
        var rise = s.GetDSCard("rise");
        var vader = s.GetDSCard("vader");
        var limbs = s.GetDSCard("limbs");
        var cape = s.GetDSCard("cape");
        s.StackCardsOn(rise, true, limbs);
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToDSHand(cape);
        s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.DSDeployAvailable(cape));
        s.DSUseCardAction(rise, "Deploy a Vader Effect from here");
        if (s.DSHasCardChoiceAvailable(limbs)) s.DSChooseCard(limbs);
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        s.PassAllResponses();
        assertEquals(vader, limbs.getAttachedTo());
        if (s.DSGetDecision() == null && s.LSGetDecision() != null) s.LSPass();
        assertTrue("Expected cancellation action; DS=" + s.DSGetDecision() + ", LS=" + s.LSGetDecision(),
            s.DSAnyActionsAvailable() && s.DSCardActionAvailable(rise, "Cancel your Vader Effect"));
        s.DSUseCardAction(rise, "Cancel your Vader Effect");
        if (s.DSHasCardChoiceAvailable(limbs)) s.DSChooseCard(limbs);
        s.PassAllResponses();
        assertEquals(Zone.STACKED_FACE_DOWN, limbs.getZone());
        assertEquals(rise, limbs.getStackedOn());
        if (s.DSGetDecision() == null && s.LSGetDecision() != null) s.LSPass();
        assertFalse(s.DSAnyActionsAvailable() && s.DSCardActionAvailable(rise, "Deploy a Vader Effect from here"));
    }

    @Test public void RiseOfTheDarkLordStopsAnActualDarkJediPresenceFromDoublingAnImperialsPower() {
        checkDarkJediPresence(true);
        checkDarkJediPresence(false);
    }

    private void checkDarkJediPresence(boolean withRise) {
        var s = board(null);
        if (!withRise) s.MoveOutOfPlay(s.GetDSCard("rise"));
        var site = s.GetDSStartingLocation();
        var trooper = s.GetDSCard("trooper");
        var vader = s.GetDSCard("vader");
        var interrupt = s.GetDSCard("darkJediPresence");
        s.MoveCardsToLocation(site, trooper, s.GetLSCard("luke"));
        s.MoveCardsToDSHand(interrupt);
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, site);
        float initialPower = s.GetPower(trooper);
        s.DSPlayCard(interrupt);
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        s.PassAllResponses();
        assertEquals(withRise ? initialPower : initialPower * 2, s.GetPower(trooper), s.epsilon);
        assertTrue(s.GetDSLostPile().contains(interrupt));
    }

    @Test public void RiseOfTheDarkLordLetsDarkTakeAnActualVaderIconCardDrawnForDestiny() {
        var s = board(null);
        var site = s.GetDSStartingLocation();
        var rise = s.GetDSCard("rise");
        var cape = s.GetDSCard("cape");
        s.MoveCardsToLocation(site, s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(8);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, site);
        s.MoveCardsToTopOfDSReserveDeck(cape);
        s.SkipToPowerSegment();
        s.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        s.DSChooseYes();
        String take = "Take Vader destiny card into hand";
        for (int i = 0; i < 16 && !(s.DSAnyActionsAvailable() && s.DSCardActionAvailable(rise, take)); i++) {
            if (!s.GetCurrentDecision().getText().toLowerCase(java.util.Locale.ROOT).contains("optional response")) break;
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue(s.DSCardActionAvailable(rise, take));
        s.DSUseCardAction(rise, take);
        s.PassAllResponses();
        assertTrue(s.GetDSHand().contains(cape));
    }
}
