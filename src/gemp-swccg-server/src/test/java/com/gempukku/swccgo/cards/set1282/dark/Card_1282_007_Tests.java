package com.gempukku.swccgo.cards.set1282.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("stew", "1_72"); // Beru Stew (Light Interrupt)
                }},
                new HashMap<>() {{
                    put("swindled", "1282_7");
                    put("alter", "1_234");
                    put("c1", "1_194");
                    put("c2", "1_195");
                    put("c3", "1_196");
                    put("c4", "1_197");
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

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean dsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardActionAvailable(card, text);
    }

    @Test
    public void YouSwindledMeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("swindled").getBlueprint();
        assertEquals("You Swindled Me!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.TATOOINE);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YouSwindledMeSplitsTopFourCardsAndOpponentChoosesWhichPileGoesToHand() {
        var scn = GetScenario();
        var swindled = scn.GetDSCard("swindled");
        var c1 = scn.GetDSCard("c1");
        var c2 = scn.GetDSCard("c2");
        var c3 = scn.GetDSCard("c3");
        var c4 = scn.GetDSCard("c4");
        scn.StartGame();
        scn.MoveCardsToHand(swindled);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(c4);
        scn.MoveCardsToTopOfDSReserveDeck(c3);
        scn.MoveCardsToTopOfDSReserveDeck(c2);
        scn.MoveCardsToTopOfDSReserveDeck(c1);

        assertTrue(scn.DSCardPlayAvailable(swindled, "Reveal top four cards of Reserve Deck"));
        scn.DSPlayCard(swindled, "Reveal top four cards of Reserve Deck");
        passUntil(scn, () -> scn.DSDecisionAvailable("Choose the cards for pile 1"));

        // Dark Side separates: pile 1 = c1, c2; pile 2 = c3, c4
        assertTrue(scn.DSDecisionAvailable("Choose the cards for pile 1"));
        assertEquals(4, scn.DSGetSelectableCount());
        scn.DSChooseCards(c1, c2);

        // Light Side puts pile 2 in Dark Side's hand, pile 1 on Used Pile
        assertTrue(scn.LSDecisionAvailable("Choose the pile to place in"));
        assertEquals(2, scn.LSGetChoiceCount());
        scn.LSDecided(1); // pile 2
        // Light Side orders the Used Pile: c2 first, then c1 (on top)
        assertTrue(scn.LSDecisionAvailable("Choose the next card to place on top of"));
        scn.LSChooseCard(c2);
        scn.PassAllResponses();

        assertInHand(c3);
        assertInHand(c4);
        assertInZone(Zone.USED_PILE, c1);
        assertInZone(Zone.USED_PILE, c2);
        // c1 was placed last, so it is above c2 (the played Interrupt itself goes on top afterwards)
        assertTrue(scn.GetDSUsedPile().indexOf(c1) < scn.GetDSUsedPile().indexOf(c2));
        assertInZone(Zone.USED_PILE, swindled);
    }

    @Test
    public void YouSwindledMeAllowsEmptyPile() {
        var scn = GetScenario();
        var swindled = scn.GetDSCard("swindled");
        var c1 = scn.GetDSCard("c1");
        var c2 = scn.GetDSCard("c2");
        var c3 = scn.GetDSCard("c3");
        var c4 = scn.GetDSCard("c4");
        scn.StartGame();
        scn.MoveCardsToHand(swindled);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(c4);
        scn.MoveCardsToTopOfDSReserveDeck(c3);
        scn.MoveCardsToTopOfDSReserveDeck(c2);
        scn.MoveCardsToTopOfDSReserveDeck(c1);

        scn.DSPlayCard(swindled, "Reveal top four cards of Reserve Deck");
        passUntil(scn, () -> scn.DSDecisionAvailable("Choose the cards for pile 1"));
        scn.DSChooseCards(c1, c2, c3, c4);
        // Light Side gives Dark Side the empty pile 2; all four cards go to Used Pile
        scn.LSDecided(1); // pile 2 (empty)
        for (PhysicalCardImpl card : new PhysicalCardImpl[]{c4, c3, c2}) {
            passUntil(scn, () -> scn.LSDecisionAvailable("Choose the next card to place on top of"));
            assertTrue(scn.LSDecisionAvailable("Choose the next card to place on top of"));
            scn.LSChooseCard(card);
        }
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, c1);
        assertInZone(Zone.USED_PILE, c2);
        assertInZone(Zone.USED_PILE, c3);
        assertInZone(Zone.USED_PILE, c4);
        var used = scn.GetDSUsedPile();
        assertTrue(used.indexOf(c1) < used.indexOf(c2));
        assertTrue(used.indexOf(c2) < used.indexOf(c3));
        assertTrue(used.indexOf(c3) < used.indexOf(c4));
    }

    @Test
    public void YouSwindledMeTakesAlterIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var swindled = scn.GetDSCard("swindled");
        var alter = scn.GetDSCard("alter");
        scn.StartGame();
        scn.MoveCardsToHand(swindled);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(alter);

        assertTrue(scn.DSCardPlayAvailable(swindled, "Take Alter into hand from Reserve Deck"));
        scn.DSPlayCard(swindled, "Take Alter into hand from Reserve Deck");
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSGetSelectableCount() > 0);
        scn.DSChooseCard(alter);
        scn.PassAllResponses();

        assertInHand(alter);
        assertInZone(Zone.USED_PILE, swindled);
    }

    @Test
    public void YouSwindledMePlacesOpponentsJustPlayedInterruptOutOfPlay() {
        var scn = GetScenario();
        var swindled = scn.GetDSCard("swindled");
        var stew = scn.GetLSCard("stew");
        scn.StartGame();
        scn.MoveCardsToHand(swindled, stew);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(2);
        scn.EnsureDSForcePile(2);
        int dsForcePile = scn.GetDSForcePileCount();

        assertTrue(scn.LSCardPlayAvailable(stew));
        scn.LSPlayCard(stew);
        passUntil(scn, () -> dsHasAction(scn, swindled, "out of play"));
        assertTrue(dsHasAction(scn, swindled, "out of play"));
        scn.DSPlayCard(swindled, "out of play");
        scn.PassAllResponses();

        assertInZone(Zone.OUT_OF_PLAY, stew);
        assertInZone(Zone.LOST_PILE, swindled);
        // Beru Stew did not resolve: Dark Side activated no Force
        assertEquals(dsForcePile, scn.GetDSForcePileCount());
        assertFalse(scn.IsCardActive(stew));
    }
}
