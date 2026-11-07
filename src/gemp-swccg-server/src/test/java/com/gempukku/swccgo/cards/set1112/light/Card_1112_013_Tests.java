package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("future", "1112_13");
                    put("card", "1_2");
                    put("extra", "1_128");
                    put("second", "1_3");
                }},
                new HashMap<>() {{
                }},
                10, 10,
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
    public void ItIsTheFutureYouSeeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("future").getBlueprint();

        assertEquals(Title.It_Is_The_Future_You_See, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EPIC_EVENT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EPIC_EVENT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ItIsTheFutureYouSeeIsAStartingInterruptOnlyWhenOneNonCoruscantLocationWasDeployed() {
        var scn = GetScenario();
        var future = scn.GetLSCard("future");
        var extra = scn.GetLSCard("extra");

        scn.StartGame();
        assertTrue(future.getBlueprint().playableAsStartingInterrupt(scn.game(), future));
        scn.MoveLocationToTable(extra);
        assertFalse(future.getBlueprint().playableAsStartingInterrupt(scn.game(), future));
    }

    @Test
    public void ItIsTheFutureYouSeeStacksCardsFromHandAndTurnsCardsFaceDownInsteadOfUsingForce() {
        var scn = GetScenario();
        var future = scn.GetLSCard("future");
        var card = scn.GetLSCard("card");

        scn.StartGame();
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        scn.MoveCardsToLSSideOfTable(future);
        scn.MoveCardsToLSHand(card);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(future, "Stack a card from hand"));
        scn.LSUseCardAction(future, "Stack a card from hand");
        scn.LSChooseCard(card);
        scn.PassAllResponses();
        assertEquals(future, card.getStackedOn());
        assertEquals(Zone.STACKED, card.getZone());
        assertEquals(force, scn.GetLSForcePileCount());

        // "Instead of using X Force, may turn X cards here face down" now applies to any Force used during your
        // turn (Bill 2026-11-07), here the 1 Force cost of stacking the second card.
        var second = scn.GetLSCard("second");
        scn.MoveCardsToLSHand(second);
        if (!scn.LSAnyDecisionsAvailable() && scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertFalse(scn.LSCardActionAvailable(future, "activate 1 Force"));
        scn.LSUseCardAction(future, "Stack a card from hand");
        for (int i = 0; i < 4 && !scn.LSDecisionAvailable("Force - Optional responses"); i++) {
            if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass(); else break;
        }
        assertTrue(scn.LSCardActionAvailable(future, "Turn cards face down instead of using Force"));
        scn.LSUseCardAction(future, "Turn cards face down instead of using Force");
        assertEquals(1, scn.LSGetChoiceMax());
        scn.LSDecided(1);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(second)) scn.LSChooseCard(second);
        scn.PassAllResponses();
        assertEquals(Zone.STACKED_FACE_DOWN, card.getZone());
        assertEquals(Zone.STACKED, second.getZone());
        assertEquals(force, scn.GetLSForcePileCount());

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(Zone.STACKED_FACE_DOWN, card.getZone());
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.STACKED, card.getZone());
    }
}
