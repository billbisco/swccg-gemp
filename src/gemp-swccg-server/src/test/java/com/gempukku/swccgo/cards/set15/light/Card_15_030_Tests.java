package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_030_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("effect", "15_30");
        cards.put("warrior", "1_28");
        cards.put("blaster", "1_152");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void CrackShotAndQuickDrawStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("effect").getBlueprint();
        assertEquals("Crack Shot & Quick Draw", card.getTitle());
        assertEquals(List.of("Crack Shot", "Quick Draw"), card.getTitles());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(2f, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertFalse(card.isHorizontal());
        assertEquals("", card.getLore());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        scn.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    @Test
    public void CrackShotAndQuickDrawDeploysFreeAndHitCardsCannotSatisfyAttrition() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        var own = scn.GetLSCard("warrior");
        var opponent = scn.GetDSFiller(1);
        scn.StartGame();
        scn.MoveCardsToHand(effect);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), own, opponent);
        own.setHit(true);
        opponent.setHit(true);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertFalse(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), own));
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        assertEquals(force, scn.GetLSForcePileCount());
        assertTrue(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), own));
        assertTrue(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), opponent));
    }
    @Test
    public void CrackShotAndQuickDrawDeploysLostBlasterForTwoForceOnlyOnceInDrawPhase() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        var warrior = scn.GetLSCard("warrior");
        var blaster = scn.GetLSCard("blaster");
        scn.StartGame();
        scn.MoveCardsToHand(effect);
        scn.MoveCardsToTopOfLSLostPile(blaster);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), warrior);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.DRAW);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(effect, "Deploy blaster");
        scn.PassAllResponses();
        scn.LSChooseCard(blaster);
        scn.PassAllResponses(); // Pile-removal responses resolve and the sole legal warrior is selected automatically.
        assertEquals(warrior, blaster.getAttachedTo());
        assertEquals(force - 2, scn.GetLSForcePileCount());
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(effect));
    }
}
