package com.gempukku.swccgo.cards.set1291.dark;

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

public class Card_1291_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("sate", "1291_7");
                    put("tie", "1_304");
                    put("trooper", "1_194"); // character
                    put("trooper2", "1_194"); // character
                    put("trooper3", "1_194"); // character
                    put("effect", "11_069"); // Fanfare (Effect)
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
    public void SatePestageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sate").getBlueprint();
        assertEquals("Sate Pestage", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
            add(Keyword.ASSASSIN);
            add(Keyword.INFORMATION_BROKER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SatePestageAddsOneToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var sate = scn.GetDSCard("sate");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, sate);
        assertEquals(2, scn.GetPower(tie));
    }

    @Test
    public void SatePestagePlacesSameCardTypeFromHandOutOfPlayToRetrieve1Force() {
        var scn = GetScenario();
        var sate = scn.GetDSCard("sate");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sate);
        scn.MoveCardsToHand(trooper);
        scn.MoveCardsToTopOfDSLostPile(trooper2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(sate, "retrieve 1 Force"));
        scn.DSUseCardAction(sate, "retrieve 1 Force");
        // trooper is the only character in hand, so it is chosen without a decision
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        scn.DSChoose("Hand");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, trooper);
        // the retrieved Force may be taken into hand
        assertInZone(Zone.HAND, trooper2);
    }

    @Test
    public void SatePestageNeedsACardOfTheSameTypeAsTopOfLostPile() {
        var scn = GetScenario();
        var sate = scn.GetDSCard("sate");
        var effect = scn.GetDSCard("effect");
        var trooper2 = scn.GetDSCard("trooper2");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sate);
        scn.MoveCardsToHand(effect);
        scn.MoveCardsToTopOfDSLostPile(trooper2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(sate, "retrieve 1 Force"));
    }

    @Test
    public void SatePestageRetrievalIsOnceDuringYourTurn() {
        var scn = GetScenario();
        var sate = scn.GetDSCard("sate");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sate);
        scn.MoveCardsToHand(trooper);
        scn.MoveCardsToTopOfDSLostPile(trooper2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(sate, "retrieve 1 Force");
        // trooper is the only character in hand, so it is chosen without a decision
        scn.PassAllResponses();
        scn.DSChoose("Used Pile");
        scn.PassAllResponses();
        // set up a second otherwise-legal use
        scn.MoveCardsToHand(scn.GetDSCard("trooper3"));
        scn.MoveCardsToTopOfDSLostPile(trooper2);
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(sate, "retrieve 1 Force"));
    }
}
