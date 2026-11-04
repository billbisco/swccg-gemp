package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Rebel
                    put("rex", "200_3"); // Republic, not a Rebel
                    put("tatooine", "1_127"); // Tatooine system (battleground)
                }},
                new HashMap<>() {{
                    put("khurgee", "1291_4");
                    put("tie", "1_304"); // TIE Fighter
                    put("thunderflare", "9_174");
                    put("motti", "1_164"); // Admiral Motti (leader)
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
    public void CaptainKhurgeeVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("khurgee").getBlueprint();
        assertEquals("Captain Khurgee", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.KHURGEE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainKhurgeeVAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, khurgee);
        assertEquals(3, scn.GetPower(tie));
    }

    private void khurgeePilotingTieAtTatooine(VirtualTableScenario scn) {
        var khurgee = scn.GetDSCard("khurgee");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, khurgee);
    }

    @Test
    public void CaptainKhurgeeVPeeksAndMayLoseForceToMakeRebelLost() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var luke = scn.GetLSCard("luke");
        khurgeePilotingTieAtTatooine(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        int lifeForce = scn.GetDSLifeForceRemaining();
        int forcePile = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(khurgee, "Peek"));
        scn.DSUseCardAction(khurgee, "Peek");
        scn.DSDismissRevealedCards();
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");
        assertTrue(scn.DSDecisionAvailable("Lose 1 Force"));
        scn.DSChooseYes();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSPayForceLossFromForcePile();
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, luke);
        assertEquals(lifeForce - 1, scn.GetDSLifeForceRemaining());
        assertEquals(forcePile - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void CaptainKhurgeeVPeekDoesNotOfferLossForNonRebel() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var rex = scn.GetLSCard("rex");
        khurgeePilotingTieAtTatooine(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rex);
        scn.DSUseCardAction(khurgee, "Peek");
        scn.DSDismissRevealedCards();
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");
        assertFalse(scn.DSDecisionAvailable("Lose 1 Force"));
        assertInZone(Zone.RESERVE_DECK, rex);
    }

    @Test
    public void CaptainKhurgeeVPeekIsOnceDuringYourTurn() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var rex = scn.GetLSCard("rex");
        khurgeePilotingTieAtTatooine(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rex);
        scn.DSUseCardAction(khurgee, "Peek");
        scn.DSDismissRevealedCards();
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(khurgee, "Peek"));
    }

    @Test
    public void CaptainKhurgeeVMayNotPeekUnlessAtBattlegroundSystem() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), khurgee);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(khurgee, "Peek"));
    }

    @Test
    public void CaptainKhurgeeVForfeitPlus2AboardThunderflare() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var thunderflare = scn.GetDSCard("thunderflare");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, thunderflare);
        scn.BoardAsPassenger(thunderflare, khurgee);
        assertEquals(6, scn.GetForfeit(khurgee));
    }

    @Test
    public void CaptainKhurgeeVForfeitPlus2WithALeaderAndNotAlone() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, khurgee);
        assertEquals(4, scn.GetForfeit(khurgee));
        scn.MoveCardsToLocation(site, motti);
        assertEquals(6, scn.GetForfeit(khurgee));
    }

    @Test
    public void CaptainKhurgeeVForfeitBonusIsLimitedToAMaximumOf7() {
        var scn = GetScenario();
        var khurgee = scn.GetDSCard("khurgee");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, khurgee, motti);
        scn.ApplyAdHocModifier(new ForfeitModifier(motti, khurgee, 2));
        assertEquals(7, scn.GetForfeit(khurgee));
    }
}
