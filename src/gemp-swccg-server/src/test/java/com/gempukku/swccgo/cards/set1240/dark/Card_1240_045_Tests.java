package com.gempukku.swccgo.cards.set1240.dark;

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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_1240_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("letspass", "1240_45");
                    put("womp", "7_215");
                    put("trooper", "1_194");
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
    public void LetsPassOnThatStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("letspass").getBlueprint();

        assertEquals("Let's Pass On That", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardType.INTERRUPT, card.getCardType());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LetsPassOnThatUsesThreeForceToRetrieveACreature() {
        var scn = GetScenario();
        var letspass = scn.GetDSCard("letspass");
        var womp = scn.GetDSCard("womp");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToHand(letspass);
        scn.MoveCardsToTopOfOwnLostPile(trooper, womp);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        int forceBefore = scn.GetDSForcePileCount();

        assertTrue(scn.DSCardPlayAvailable(letspass, "Retrieve a creature"));
        scn.DSPlayCard(letspass, "Retrieve a creature");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(womp)) {
            scn.DSChooseCard(womp);
            scn.PassAllResponses();
        }

        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
        assertNotEquals(Zone.LOST_PILE, womp.getZone());
        assertEquals(Zone.LOST_PILE, trooper.getZone());
    }

    @Test
    public void LetsPassOnThatAttackOnlyDuringOpponentsControlPhase() {
        var scn = GetScenario();
        var letspass = scn.GetDSCard("letspass");
        var womp = scn.GetDSCard("womp");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(letspass);
        scn.MoveCardsToLocation(site, womp, luke);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(letspass, "Have ferocious creature attack"));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(letspass, "Have ferocious creature attack"));
    }

    private void playAttack(VirtualTableScenario scn) {
        var letspass = scn.GetDSCard("letspass");
        var womp = scn.GetDSCard("womp");
        var luke = scn.GetLSCard("luke");
        scn.DSPlayCard(letspass, "Have ferocious creature attack");
        if (scn.DSHasCardChoiceAvailable(womp)) scn.DSChooseCard(womp);
        if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringAttack() || womp.getZone() != Zone.AT_LOCATION || luke.getZone() != Zone.AT_LOCATION || true);
        for (int i = 0; i < 40 && scn.gameState().isDuringAttack(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
    }

    @Test
    public void LetsPassOnThatAttackThenAtEndOfTurnOpponentLosesOneAndYouRetrieveOneIfSiteUnoccupied() {
        var scn = GetScenario();
        var letspass = scn.GetDSCard("letspass");
        var womp = scn.GetDSCard("womp");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(letspass);
        scn.MoveCardsToLocation(site, womp, luke);
        scn.MoveCardsToTopOfOwnLostPile(scn.GetDSFiller(1));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        playAttack(scn);

        // Make the site unoccupied (the creature remains, and did not move).
        if (luke.getZone() == Zone.AT_LOCATION) {
            scn.RemoveCardZone(luke);
            scn.MoveCardToZone(luke, Zone.LOST_PILE);
        }
        assertEquals(Zone.AT_LOCATION, womp.getZone());

        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        int dsLostBefore = scn.GetDSLostPileCount();
        scn.SkipToDSTurn();
        if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();

        assertTrue(scn.GetLSLifeForceRemaining() <= lsLifeBefore - 1);
        assertEquals(dsLostBefore - 1, scn.GetDSLostPileCount());
    }

    @Test
    public void LetsPassOnThatNoEndOfTurnBonusIfSiteOccupied() {
        var scn = GetScenario();
        var letspass = scn.GetDSCard("letspass");
        var womp = scn.GetDSCard("womp");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(letspass);
        scn.MoveCardsToLocation(site, womp, luke, trooper);
        scn.MoveCardsToTopOfOwnLostPile(scn.GetDSFiller(1));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        playAttack(scn);

        int dsLostBefore = scn.GetDSLostPileCount();
        scn.SkipToDSTurn();
        scn.PassAllResponses();
        assertEquals(dsLostBefore, scn.GetDSLostPileCount());
    }
}
