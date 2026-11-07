package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_052_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("arena", "1240_52");
                    put("waters", "2_119");
                    put("bones", "1240_29");
                    put("womp", "7_215");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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
    public void ExecutionArenaPitStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("arena").getBlueprint();

        assertEquals("Execution Arena (Pit)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardCategory.LOCATION, card.getCardCategory());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertTrue(card.hasIcon(Icon.EXTERIOR_SITE));
        assertTrue(card.hasIcon(Icon.PLANET));
        assertTrue(card.hasIcon(Icon.EPISODE_I));
        assertTrue(card.hasIcon(Icon.TATOOINE));
        assertTrue(card.hasIcon(Icon.LEGACY_BLOCK_4));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ExecutionArenaPitMayDeployDarkWatersHereFromReserveDeck() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var waters = scn.GetDSCard("waters");

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfOwnReserveDeck(waters);

        assertTrue(scn.DSCardActionAvailable(arena, "Deploy Dark Waters here from Reserve Deck"));
        scn.DSUseCardAction(arena, "Deploy Dark Waters here from Reserve Deck");
        Block4DarkTestSupport.advanceUntil(scn, () -> scn.DSHasCardChoiceAvailable(waters), 10);
        if (scn.DSHasCardChoiceAvailable(waters)) scn.DSChooseCard(waters);
        if (scn.DSHasCardChoiceAvailable(arena)) scn.DSChooseCard(arena);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(arena, waters));
    }

    @Test
    public void ExecutionArenaPitCancelsBlock4KraytDragonBones() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var bones = scn.GetDSCard("bones");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(bones);
        scn.MoveLocationToTable(arena);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertFalse(scn.gameState().isCardInPlayActive(bones));
    }

    @Test
    public void ExecutionArenaPitYourForceDrainsHereAreCanceled() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveCardsToLocation(arena, trooper);
        scn.SkipToPhase(Phase.CONTROL);

        int lifeBefore = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSForceDrainAvailable(arena));
        scn.DSForceDrainAt(arena);
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lifeBefore, scn.GetLSLifeForceRemaining());
    }

    private boolean trooperCanMoveFromArena(boolean creatureHere) {
        return trooperCanMoveFromArena(creatureHere ? "womp" : null);
    }

    private boolean trooperCanMoveFromArena(String creatureKey) {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var womp = scn.GetDSCard("womp");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveCardsToLocation(arena, trooper);
        if (creatureKey != null)
            scn.MoveCardsToLocation(arena, scn.GetDSCard(creatureKey));
        scn.SkipToPhase(Phase.MOVE);
        scn.EnsureDSForcePile(5);
        return scn.DSCardActionAvailable(trooper, "Move");
    }

    @Test
    public void ExecutionArenaPitCharactersCannotMoveFromHereWhileCreatureHere() {
        assertTrue(trooperCanMoveFromArena(false));
        assertFalse(trooperCanMoveFromArena(true));
        assertTrue(trooperCanMoveFromArena("bogwing")); // a parasite is not a ferocious creature
    }

    @Test
    public void ExecutionArenaPitLightSideLosesOneForceDuringOpponentsControlPhaseIfOpponentControlsAdjacentSite() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var womp = scn.GetDSCard("womp");
        var trooper = scn.GetDSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        assertTrue(scn.IsAdjacentTo(arena, dsSite));
        scn.MoveCardsToLocation(arena, womp);
        scn.MoveCardsToLocation(dsSite, trooper);

        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.SkipToPhase(Phase.CONTROL);
        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingLSForceLossPayment, 10);
        assertTrue(Block4DarkTestSupport.describe(scn), scn.AwaitingLSForceLossPayment());
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lifeBefore - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void ExecutionArenaPitNoForceLossWithoutCreatureHere() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var trooper = scn.GetDSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveCardsToLocation(dsSite, trooper);

        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.SkipToPhase(Phase.CONTROL);
        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingLSForceLossPayment, 10);
        assertFalse(scn.AwaitingLSForceLossPayment());
        assertEquals(lifeBefore, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void ExecutionArenaPitNoForceLossWithOnlyAParasiteHere() {
        var scn = GetScenario();
        var arena = scn.GetDSCard("arena");
        var bogwing = scn.GetDSCard("bogwing");
        var trooper = scn.GetDSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveCardsToLocation(arena, bogwing);
        scn.MoveCardsToLocation(dsSite, trooper);

        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.SkipToPhase(Phase.CONTROL);
        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingLSForceLossPayment, 10);
        assertFalse(scn.AwaitingLSForceLossPayment());
        assertEquals(lifeBefore, scn.GetLSLifeForceRemaining());
    }
}
