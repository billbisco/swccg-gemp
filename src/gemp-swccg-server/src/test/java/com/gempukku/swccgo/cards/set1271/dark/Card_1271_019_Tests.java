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
}
