package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardCategory;
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
                    put("bogwing", "4_108"); // parasite
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
        assertEquals(CardCategory.INTERRUPT, card.getCardCategory());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);
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
        assertFalse(scn.GetDSLostPile().contains(womp));
        assertTrue(scn.GetDSLostPile().contains(trooper));
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
        scn.MoveCardsToHand(womp);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, womp);
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
        Block4DarkTestSupport.advanceUntil(scn, () -> scn.gameState().isDuringAttack(), 10);
        assertTrue(Block4DarkTestSupport.describe(scn), scn.gameState().isDuringAttack());
        Block4DarkTestSupport.advanceUntil(scn, () -> !scn.gameState().isDuringAttack(), 60);
        assertFalse(Block4DarkTestSupport.describe(scn), scn.gameState().isDuringAttack());
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
        scn.MoveCardsToTopOfOwnLostPile(scn.GetDSFiller(1));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, womp, luke);
        scn.LSPass();
        playAttack(scn);

        // Make the site unoccupied (the creature remains, and did not move).
        if (luke.getZone() == Zone.AT_LOCATION) {
            scn.RemoveCardZone(luke);
            scn.MoveCardToZone(scn.LS, luke, Zone.LOST_PILE);
        }
        assertEquals(Zone.AT_LOCATION, womp.getZone());

        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        int dsLostBefore = scn.GetDSLostPileCount();
        assertTrue(Block4DarkTestSupport.describe(scn), Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingLSForceLossPayment, 80));
        scn.LSPayRemainingForceLossFromReserveDeck();
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
        scn.MoveCardsToTopOfOwnLostPile(scn.GetDSFiller(1));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, womp, luke, trooper);
        scn.LSPass();
        playAttack(scn);

        int dsLostBefore = scn.GetDSLostPileCount();
        scn.SkipToDSTurn();
        scn.PassAllResponses();
        assertEquals(dsLostBefore, scn.GetDSLostPileCount());
    }

    @Test
    public void LetsPassOnThatParasiteIsNotAFerociousCreatureToAttack() {
        var scn = GetScenario();
        var letspass = scn.GetDSCard("letspass");
        var bogwing = scn.GetDSCard("bogwing");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(letspass);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, bogwing, luke);
        scn.LSPass();
        assertFalse(scn.DSCardPlayAvailable(letspass, "Have ferocious creature attack"));
    }
}
