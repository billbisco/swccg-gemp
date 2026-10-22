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
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("slip", "1282_22");
                    put("badfeeling", "1_91");
                    put("sense", "1_85");
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("spice", "2_125");
                    put("watch", "12_158");
                    put("sewatch", "7_268");
                    put("tie", "1_304");
                    put("tallon", "1_270");
                    put("vader", "1_168");
                    put("dantooine", "1_282");
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
    public void CorellianSlipVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("slip").getBlueprint();

        assertEquals("Corellian Slip", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorellianSlipVTakesAnInterruptFromReserveDeck() {
        var scn = GetScenario();
        var slip = scn.GetLSCard("slip");
        var badfeeling = scn.GetLSCard("badfeeling");
        var sense = scn.GetLSCard("sense");

        scn.StartGame();
        scn.MoveCardsToLSHand(slip);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.MoveCardsToTopOfLSReserveDeck(badfeeling, sense);

        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(slip, "Take Interrupt"));
        scn.LSPlayCard(slip, "Take Interrupt");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(badfeeling));
        scn.LSChooseCard(badfeeling);
        scn.PassAllResponses();

        assertInHand(badfeeling);
        assertInZone(Zone.LOST_PILE, slip);
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void CorellianSlipVCancelsCoruscantWatchYourBack() {
        var scn = GetScenario();
        var slip = scn.GetLSCard("slip");
        var watch = scn.GetDSCard("watch");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(watch);
        scn.MoveCardsToLSHand(slip);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(slip, "Watch Your Back"));
        scn.LSPlayCard(slip, "Watch Your Back");
        assertTrue(scn.LSDecisionAvailable("Choose card to cancel"));
        assertTrue(scn.LSHasCardChoiceAvailable(watch));
        scn.LSChooseCard(watch);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, watch);
        assertInZone(Zone.LOST_PILE, slip);
    }

    @Test
    public void CorellianSlipVDoesNotCancelSpecialEditionWatchYourBack() {
        var scn = GetScenario();
        var slip = scn.GetLSCard("slip");
        var sewatch = scn.GetDSCard("sewatch");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(sewatch);
        scn.MoveCardsToLSHand(slip);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(slip, "Watch Your Back"));
        assertEquals(Zone.SIDE_OF_TABLE, sewatch.getZone());
    }

    @Test
    public void CorellianSlipVExchangesACardInHandForSpiceInLostPile() {
        var scn = GetScenario();
        var slip = scn.GetLSCard("slip");
        var luke = scn.GetLSCard("luke");
        var spice = scn.GetDSCard("spice");

        scn.StartGame();
        spice.setOwner(scn.LS);
        scn.MoveCardsToLSHand(slip, luke);
        scn.MoveCardsToTopOfLSLostPile(spice);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(slip, "spice"));
        scn.LSPlayCard(slip, "spice");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(spice));
        scn.LSChooseCard(spice);
        scn.PassAllResponses();

        assertInHand(spice);
        assertInZone(Zone.LOST_PILE, luke);
        assertInZone(Zone.LOST_PILE, slip);
    }

    @Test
    public void CorellianSlipVCancelsWeaponTargetingYourStarfighter() {
        var scn = GetScenario();
        var slip = scn.GetLSCard("slip");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var tallon = scn.GetDSCard("tallon");
        var vader = scn.GetDSCard("vader");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie, xwing);
        scn.BoardAsPilot(tie, vader);
        scn.MoveCardsToLSHand(slip);
        scn.MoveCardsToDSHand(tallon);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(tallon));
        scn.DSPlayCard(tallon);
        scn.DSChooseCard(tie);
        scn.DSChooseCard(xwing);
        assertTrue(scn.LSCardPlayAvailable(slip, "Cancel targeting"));
        scn.LSPlayCard(slip, "Cancel targeting");
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, xwing.getZone());
        assertInZone(Zone.LOST_PILE, slip);
        assertInZone(Zone.LOST_PILE, tallon);
    }
}
