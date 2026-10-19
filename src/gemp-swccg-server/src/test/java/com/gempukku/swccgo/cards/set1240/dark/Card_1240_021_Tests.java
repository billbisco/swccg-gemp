package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ywing", "1_147");
                }},
                new HashMap<>() {{
                    put("blown", "1240_21");
                    put("evax", "1240_12");
                    put("aloo", "1230_18");
                    put("yularen", "1240_4");
                    put("motti", "1_164");
                    put("room", "1240_51");
                    put("tatooine", "1_289");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BlownClearStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("blown").getBlueprint();

        assertEquals("Blown Clear", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BlownClearStacksCouncilMemberAndSubtractsStarshipForfeit() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var blown = scn.GetDSCard("blown");
        var evax = scn.GetDSCard("evax");
        var motti = scn.GetDSCard("motti");
        var room = scn.GetDSCard("room");
        var ywing = scn.GetLSCard("ywing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToDSSideOfTable(blown);
        scn.MoveCardsToLocation(room, evax);
        scn.MoveCardsToDSSideOfTable(ywing);
        scn.StackCardsOn(blown, motti);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);

        assertEquals(Zone.STACKED, motti.getZone());
        assertTrue(scn.HasKeyword(motti, Keyword.ADMIRAL));
        int forfeitBefore = scn.GetForfeit(ywing);
        assertTrue(scn.DSCardActionAvailable(blown, "Subtract influence from a starship"));
        scn.DSUseCardAction(blown, "Subtract influence from a starship");
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(forfeitBefore - 1, scn.GetForfeit(ywing));
    }

    @Test
    public void BlownClearPlacesCouncilMemberFromHand() {
        var scn = GetScenario();
        var blown = scn.GetDSCard("blown");
        var evax = scn.GetDSCard("evax");
        var yularen = scn.GetDSCard("yularen");

        scn.StartGame();
        scn.MoveCardsToDSHand(blown, evax, yularen);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(blown));
        scn.DSPlayCard(blown);
        scn.LSPass();
        scn.PassAllResponses();
        scn.LSPass();
        assertTrue("expected deploy actions after playing Blown Clear, ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision() + " zone=" + blown.getZone(),
                scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(blown, "Place an Imperial Council Member"));
        scn.DSUseCardAction(blown, "Place an Imperial Council Member");
        assertTrue(scn.DSHasCardChoiceAvailable(evax));
        assertTrue(scn.DSHasCardChoiceAvailable(yularen));
        scn.DSChooseCard(evax);
        scn.PassAllResponses();
        assertEquals(Zone.STACKED, evax.getZone());
    }

    @Test
    public void BlownClearDoesNotSubtractWithNothingStacked() {
        var scn = GetScenario();
        var blown = scn.GetDSCard("blown");
        var room = scn.GetDSCard("room");
        var ywing = scn.GetLSCard("ywing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToDSSideOfTable(blown);
        scn.MoveCardsToDSSideOfTable(ywing);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(blown, "Subtract influence from a starship"));
    }

    @Test
    public void BlownClearLogisticsAgendaAllowsSubtractAndCapturesXWhenOffered() {
        var scn = GetScenario();
        var blown = scn.GetDSCard("blown");
        var evax = scn.GetDSCard("evax");
        var aloo = scn.GetDSCard("aloo");
        var room = scn.GetDSCard("room");
        var ywing = scn.GetLSCard("ywing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToDSSideOfTable(blown);
        scn.MoveCardsToLocation(room, aloo);
        scn.MoveCardsToDSSideOfTable(ywing);
        scn.StackCardsOn(blown, evax);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ForfeitModifier(ywing, 10));
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier(ywing, 8));
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);

        int x = (int) scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room);
        assertEquals(3, x);
        int forfeitBefore = scn.GetForfeit(ywing);
        float immunityBefore = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ywing);
        assertTrue(forfeitBefore > x);
        assertTrue(immunityBefore > x);
        assertTrue(scn.DSCardActionAvailable(blown, "Subtract influence from a starship"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), aloo);
        scn.DSUseCardAction(blown, "Subtract influence from a starship");
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(forfeitBefore - x, scn.GetForfeit(ywing));
        assertEquals(immunityBefore - x,
                scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ywing), scn.epsilon);
        if (scn.LSGetDecision() != null && scn.DSGetDecision() == null) {
            scn.LSPass();
        }
        assertTrue("DS must be deciding Control actions so the once-per-turn check can fail. decision=" + scn.DSGetDecision(),
                scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(blown, "Subtract influence from a starship"));
    }
}
