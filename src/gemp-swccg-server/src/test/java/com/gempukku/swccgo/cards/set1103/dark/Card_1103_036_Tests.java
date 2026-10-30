package com.gempukku.swccgo.cards.set1103.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_3");
                }},
                new HashMap<>() {{
                    put("informant", "1103_36");
                    put("undercover", "1210_54");
                    put("spy", "1_163");
                    put("peekown", "1_194");
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
    public void InformantStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("informant").getBlueprint();

        assertEquals("Informant", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void InformantDeploysUndercoverFromReserveDeck() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.MoveCardsToDSHand(informant);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(undercover);

        assertTrue(scn.DSCardPlayAvailable(informant, "Deploy Undercover"));
        scn.DSPlayCard(informant, "Deploy Undercover");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(undercover));
        scn.DSChooseCard(undercover);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(spy)) {
            scn.DSChooseCard(spy);
            scn.PassAllResponses();
        }

        assertEquals(spy, undercover.getAttachedTo());
        assertTrue(spy.isUndercover());
        assertInZone(Zone.USED_PILE, informant);
    }

    @Test
    public void InformantPeeksAtTopXCardsOfOpponentsReserveDeck() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy, luke);
        scn.AttachCardsTo(spy, undercover);
        scn.MakeCardGoUndercover(spy);
        scn.MoveCardsToDSHand(informant);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(leia);

        assertTrue(scn.DSCardPlayAvailable(informant, "opponent's Reserve Deck"));
        scn.DSPlayCard(informant, "opponent's Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Top card"));
        assertTrue(scn.DSHasCardChoiceAvailable(leia));
        scn.DSDismissRevealedCards();
        scn.PassAllResponses();

        assertInZone(Zone.RESERVE_DECK, leia);
        assertInZone(Zone.USED_PILE, informant);
    }

    @Test
    public void InformantPeeksAtTopCardOfYourReserveDeck() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var peekown = scn.GetDSCard("peekown");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.AttachCardsTo(spy, undercover);
        scn.MakeCardGoUndercover(spy);
        scn.MoveCardsToDSHand(informant);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(peekown);

        assertTrue(scn.DSCardPlayAvailable(informant, "Peek at top card of your Reserve Deck"));
        scn.DSPlayCard(informant, "Peek at top card of your Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Top card"));
        assertTrue(scn.DSHasCardChoiceAvailable(peekown));
        scn.DSDismissRevealedCards();
        scn.PassAllResponses();

        assertInZone(Zone.RESERVE_DECK, peekown);
        assertInZone(Zone.USED_PILE, informant);
    }

    @Test
    public void InformantDoesNotOfferOpponentPeekWhenXIsZero() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.AttachCardsTo(spy, undercover);
        scn.MakeCardGoUndercover(spy);
        scn.MoveCardsToDSHand(informant);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardPlayAvailable(informant, "opponent's Reserve Deck"));
        assertTrue(scn.DSCardPlayAvailable(informant, "Peek at top card of your Reserve Deck"));
    }
}
