package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1220_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("piett", "1220_6");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("filler1", "1_194");
                    put("filler2", "1_186");
                    put("filler3", "1_173");
                    put("draw1", "1_187");
                    put("draw2", "1_179");
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
    public void CaptainPiettStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("piett").getBlueprint();

        assertEquals("Captain Piett", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.PIETT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainPiettAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var piett = scn.GetDSCard("piett");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, piett);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void CaptainPiettPlacesThreeCardsOnForcePileToDrawTwoDuringOpponentsTurn() {
        var scn = GetScenario();
        var piett = scn.GetDSCard("piett");
        var filler1 = scn.GetDSCard("filler1");
        var filler2 = scn.GetDSCard("filler2");
        var filler3 = scn.GetDSCard("filler3");
        var draw1 = scn.GetDSCard("draw1");
        var draw2 = scn.GetDSCard("draw2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, piett);
        scn.MoveCardsToDSHand(filler1, filler2, filler3);
        for (var card : new java.util.ArrayList<>(scn.GetDSHand())) {
            if (card != filler1 && card != filler2 && card != filler3) {
                scn.MoveCardsToTopOfDSReserveDeck((com.gempukku.swccgo.game.PhysicalCardImpl) card);
            }
        }
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.MoveCardsToTopOfDSReserveDeck(draw2);
        scn.MoveCardsToTopOfDSReserveDeck(draw1);
        assertEquals(Zone.HAND, filler1.getZone());
        assertEquals(Zone.HAND, filler2.getZone());
        assertEquals(Zone.HAND, filler3.getZone());
        assertTrue(scn.DSCardActionAvailable(piett, "Place 3 cards on Force Pile to draw 2"));
        scn.DSUseCardAction(piett, "Place 3 cards on Force Pile to draw 2");
        int safety = 0;
        while (safety++ < 12 && scn.DSGetADParam("cardId") == null) {
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        choosePiettHandCard(scn, filler1);
        choosePiettHandCard(scn, filler2);
        choosePiettHandCard(scn, filler3);
        scn.PassAllResponses();
        assertTrue(filler1.getZone() == Zone.FORCE_PILE || filler1.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertTrue(filler2.getZone() == Zone.FORCE_PILE || filler2.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertTrue(filler3.getZone() == Zone.FORCE_PILE || filler3.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertEquals(Zone.HAND, draw1.getZone());
        assertEquals(Zone.HAND, draw2.getZone());
    }

    @Test
    public void CaptainPiettDrawIsNotOfferedDuringYourTurn() {
        var scn = GetScenario();
        var piett = scn.GetDSCard("piett");
        var filler1 = scn.GetDSCard("filler1");
        var filler2 = scn.GetDSCard("filler2");
        var filler3 = scn.GetDSCard("filler3");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, piett);
        scn.MoveCardsToDSHand(filler1, filler2, filler3);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(piett, "Place 3 cards on Force Pile to draw 2"));
    }

    private void choosePiettHandCard(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        int safety = 0;
        while (safety++ < 12 && scn.DSGetADParam("cardId") == null) {
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue("Piett hand choice missing for " + card.getTitle()
                        + " zone=" + card.getZone()
                        + " decision=" + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText())
                        + " cardId=" + java.util.Arrays.toString(scn.DSGetADParam("cardId")),
                scn.DSGetADParam("cardId") != null);
        scn.DSChooseCardIDFromSelection(card);
    }
}
