package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("eitherway", "1282_24");
                    put("control", "4_48"); // Control
                    put("a", "1_105");
                    put("b", "1_105");
                    put("c", "1_105");
                    put("d", "1_105");
                }},
                new HashMap<>() {{
                    put("watch", "12_158"); // Watch Your Back! (an Interrupt to place out of play)
                    put("slave", "5_177"); // a unique unpiloted starfighter for Watch Your Back!
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
    public void EitherWayYouWinVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("eitherway").getBlueprint();

        assertEquals("Either Way, You Win", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals("'Deal!'", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EitherWayYouWinVSeparatesTopFourIntoPilesOpponentPlacesThem() {
        var scn = GetScenario();
        var eitherway = scn.GetLSCard("eitherway");
        var a = scn.GetLSCard("a");
        var b = scn.GetLSCard("b");
        var c = scn.GetLSCard("c");
        var d = scn.GetLSCard("d");

        scn.StartGame();
        scn.MoveCardsToLSHand(eitherway);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(a, b, c, d);

        assertTrue(scn.LSCardPlayAvailable(eitherway, "Reveal top four"));
        scn.LSPlayCard(eitherway, "Reveal top four");
        scn.PassAllResponses();

        // You make the piles: a and b, then c and d
        assertTrue(scn.LSDecisionAvailable("first pile"));
        scn.LSChooseCards(a, b);
        // Opponent places the second pile (c, d) in your hand, the first on Used Pile
        assertTrue(scn.DSDecisionAvailable("Choose the pile"));
        scn.DSChoose("1");
        assertTrue(scn.DSDecisionAvailable("Used Pile"));
        scn.DSChooseCards(b);
        scn.DSChooseCards(a);
        scn.PassAllResponses();

        assertInHand(c);
        assertInHand(d);
        assertEquals(Zone.USED_PILE, b.getZone());
        // Used Pile from top: Either Way, You Win (V) itself, then a (placed last), then b
        assertEquals(eitherway, scn.GetLSUsedPile().get(0));
        assertEquals(a, scn.GetLSUsedPile().get(1));
        assertEquals(b, scn.GetLSUsedPile().get(2));
    }

    @Test
    public void EitherWayYouWinVTakesControlIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var eitherway = scn.GetLSCard("eitherway");
        var control = scn.GetLSCard("control");

        scn.StartGame();
        scn.MoveCardsToLSHand(eitherway);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToBottomOfLSReserveDeck(control);

        assertTrue(scn.LSCardPlayAvailable(eitherway, "Control"));
        scn.LSPlayCard(eitherway, "Control");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(control));
        scn.LSChooseCard(control);
        scn.PassAllResponses();

        assertInHand(control);
    }

    @Test
    public void EitherWayYouWinVPlacesOpponentsJustPlayedInterruptOutOfPlay() {
        var scn = GetScenario();
        var eitherway = scn.GetLSCard("eitherway");
        var watch = scn.GetDSCard("watch");

        scn.StartGame();
        scn.MoveCardsToLSHand(eitherway);
        scn.MoveCardsToDSHand(watch);
        scn.SkipToDSTurn(Phase.CONTROL);

        scn.DSPlayUsedInterrupt(watch);
        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, eitherway, "out of play"));
        scn.LSPlayCard(eitherway, "out of play");
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("slave"))) {
            scn.DSChooseCard(scn.GetDSCard("slave"));
            scn.PassAllResponses();
        }

        assertEquals(Zone.OUT_OF_PLAY, watch.getZone());
        assertEquals(Zone.LOST_PILE, eitherway.getZone() == Zone.TOP_OF_LOST_PILE ? Zone.LOST_PILE : eitherway.getZone());
    }
}
