package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_1273_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("usetheforce", "1273_34");
                    put("communing", "601_41");
                    put("stacked1", "1_028");
                    put("stacked2", "1_028");
                    put("lost", "1_028");
                }},
                new HashMap<>(),
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

    private static void setUp(VirtualTableScenario scn, int stacked) {
        var communing = scn.GetLSCard("communing");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(communing);
        if (stacked >= 1)
            scn.StackCardsOn(communing, scn.GetLSCard("stacked1"));
        if (stacked >= 2)
            scn.StackCardsOn(communing, scn.GetLSCard("stacked2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("usetheforce"));
        scn.SkipToLSTurn(Phase.CONTROL);
    }

    @Test
    public void UseTheForceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("usetheforce").getBlueprint();

        assertEquals("Use The Force", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UseTheForceNotPlayableWithoutCardsStackedOnCommuning() {
        var scn = GetScenario();
        setUp(scn, 0);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("usetheforce"), "Choose up to"));
    }

    @Test
    public void UseTheForceChoosesUpToXOptionsRetrieveAndActivateX() {
        var scn = GetScenario();
        var usetheforce = scn.GetLSCard("usetheforce");
        var lost = scn.GetLSCard("lost");
        setUp(scn, 2);
        scn.MoveCardsToTopOfLSLostPile(lost);
        int force = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(usetheforce, "Choose up to"));
        scn.LSUseCardAction(usetheforce, "Choose up to");
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Retrieve 1 Force")));
        assertEquals(6, scn.LSGetMultipleChoices().size());
        scn.LSChooseOption("Retrieve 1 Force");
        Vb81RestTestSupport.passUntil(scn, () -> scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Activate X Force"));
        // An option already chosen is not offered again
        assertFalse(scn.LSGetMultipleChoices().contains("Retrieve 1 Force"));
        scn.LSChooseOption("Activate X Force");
        scn.PassAllResponses();
        assertFalse(Vb81RestTestSupport.holdsDecision(scn, scn.LS, "Choose an option"));
        assertEquals(force + 2, scn.GetLSForcePileCount());
        assertTrue(Vb81RestTestSupport.inZone(lost, Zone.USED_PILE) || Vb81RestTestSupport.inZone(lost, Zone.RESERVE_DECK) || Vb81RestTestSupport.inZone(lost, Zone.FORCE_PILE));
    }

    @Test
    public void UseTheForceDrawsTwoCardsFromReserveDeck() {
        var scn = GetScenario();
        var usetheforce = scn.GetLSCard("usetheforce");
        setUp(scn, 1);
        int hand = scn.GetLSHandCount();

        scn.LSUseCardAction(usetheforce, "Choose up to");
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Draw 2 cards from Reserve Deck")));
        scn.LSChooseOption("Draw 2 cards from Reserve Deck");
        scn.PassAllResponses();
        // X = 1: only one option; Use The Force left hand, two cards were drawn
        assertEquals(hand - 1 + 2, scn.GetLSHandCount());
    }
}
