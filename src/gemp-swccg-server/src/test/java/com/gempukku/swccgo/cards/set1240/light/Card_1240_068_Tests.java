package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_068_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("leebo", "1240_68"); put("dash", "10_6"); put("luke", "1_19");
            put("threepio", "1_5"); put("warroom", "1_139");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("top", "1_194"); put("next", "1_164");
            put("used", "1_165");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void LeeboPrintedStatsIconsKeywordsAndModelAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("leebo").getBlueprint();
        assertEquals(Title.Leebo, bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon);
        assertEquals(4, bp.getArmor(), scn.epsilon);
        assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Smuggler. Heavily modified Cybot Galactica LE-series repair droid. Information broker. Provides information gained through underworld channels to the Alliance.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.DROID)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.DROID, Icon.PILOT, Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER)));
        assertEquals(List.of(ModelType.MAINTENANCE), new ArrayList<>(bp.getModelTypes()));
    }

    private VirtualTableScenario Ready() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("next"), scn.GetDSCard("top"));
        scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("used"));
        return scn;
    }

    private void Peek(VirtualTableScenario scn) {
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
        scn.LSUseCardAction(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Top card of"));
        assertTrue(scn.LSDecisionAvailable("Reserve Deck"));
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("on Used Pile?"));
    }

    @Test
    public void LeeboPeeksAndPlacesExactlyTopOpponentCardAboveExistingUsedPileWithoutForceCost() {
        var scn = Ready();
        int force = scn.GetLSForcePileCount();
        int reserve = scn.GetDSReserveDeckCount();
        int used = scn.GetDSUsedPileCount();
        Peek(scn);
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(force, scn.GetLSForcePileCount());
        assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
        assertEquals(used + 1, scn.GetDSUsedPileCount());
        assertEquals(scn.GetDSCard("next"), scn.GetDSReserveDeck().getFirst());
        assertEquals(List.of(scn.GetDSCard("top"), scn.GetDSCard("used")), scn.GetDSUsedPile().subList(0, 2));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("top"));
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboMayDeclinePlacementAndPreservesBothPiles() {
        var scn = Ready();
        var reserve = new ArrayList<>(scn.GetDSReserveDeck());
        var used = new ArrayList<>(scn.GetDSUsedPile());
        Peek(scn);
        scn.LSChooseNo();
        scn.PassAllResponses();
        assertEquals(reserve, scn.GetDSReserveDeck());
        assertEquals(used, scn.GetDSUsedPile());
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboMayPeekAgainNextTurn() {
        var scn = Ready();
        Peek(scn);
        scn.LSChooseNo();
        scn.PassAllResponses();
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
        scn.SkipToDSTurn(Phase.CONTROL);
        // The non-turn player receives a normal control action opportunity once the turn player passes.
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCannotPeekAtNonBattlegroundButRegainsActionOnReturning() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("warroom"));
        scn.MoveCardsToLocation(scn.GetLSCard("warroom"), scn.GetLSCard("leebo"));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        scn.SkipToDSTurn(Phase.CONTROL);
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCannotPeekEmptyOpponentReserveEvenWithCardsInOwnReserve() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSReserveDeck().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(0, scn.GetDSReserveDeckCount());
        assertTrue(scn.GetLSReserveDeckCount() > 0);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCanMoveTheOnlyOpponentReserveCardToUsedPile() {
        var scn = Ready();
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSReserveDeck().subList(1, scn.GetDSReserveDeckCount()).toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertEquals(1, scn.GetDSReserveDeckCount());
        Peek(scn);
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSReserveDeckCount());
        assertEquals(scn.GetDSCard("top"), scn.GetDSUsedPile().getFirst());
    }

    @Test
    public void LeeboAloneMayBeBattledUnlikeAnOrdinaryDroid() {
        var ordinary = GetScenario();
        ordinary.StartGame();
        ordinary.MoveCardsToLocation(ordinary.GetLSStartingLocation(), ordinary.GetLSCard("threepio"), ordinary.GetDSCard("vader"));
        ordinary.EnsureDSForcePile(5);
        ordinary.SkipToDSTurn(Phase.BATTLE);
        assertFalse(ordinary.DSCanInitiateBattle(ordinary.GetLSStartingLocation()));

        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"), scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(scn.GetLSStartingLocation()));
        scn.DSInitiateBattle(scn.GetLSStartingLocation());
        assertTrue(scn.gameState().isParticipatingInBattle(scn.GetLSCard("leebo")));
    }

    private VirtualTableScenario BattleReady(boolean withDash) {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(withDash ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation(), scn.GetLSCard("dash"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PrepareLSDestiny(3); // Attrition-only draw is below normal destiny2.
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(0);
        return scn;
    }

    /** Passes the remaining battle steps (drawing every offered destiny) until the damage segment's payments begin. */
    private static void ToDamageSegment(VirtualTableScenario scn) {
        for (int i = 0; i < 40 && !(scn.IsReachedDamageSegment()
                && (scn.AwaitingDSAttritionPayment() || scn.AwaitingDSBattleDamagePayment()
                || scn.AwaitingLSAttritionPayment() || scn.AwaitingLSBattleDamagePayment())); i++) {
            if (scn.LSDecisionAvailable("destiny?")) scn.LSChooseYes();
            else if (scn.DSDecisionAvailable("destiny?")) scn.DSChooseYes();
            else scn.PassResponses();
        }
    }

    @Test
    public void LeeboWithDashAddsActualAttritionDestinyWithoutAddingItToPower() {
        var scn = BattleReady(true);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        assertEquals(11, scn.GetLSTotalPower()); // Leebo3+Dash3+Luke3+normalDestiny2.
        ToDamageSegment(scn);
        assertEquals(5, scn.GetUnpaidDSAttrition()); // Normal destiny 2 + attrition-only destiny 3.
    }

    @Test
    public void LeeboDoesNotAddAttritionDestinyWhenDashIsAtAnotherSite() {
        var scn = BattleReady(false);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        ToDamageSegment(scn);
        assertEquals(2, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void LeeboCannotAddAttritionDestinyWithoutAnyBattleDestiny() {
        var scn = BattleReady(true);
        scn.SkipToEndOfPowerSegment(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(0, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void LeeboDeploysForExactlyThreeForce() {
        var scn = Ready();
        scn.MoveCardsToLSHand(scn.GetLSCard("leebo"));
        scn.SkipToPhase(Phase.DEPLOY);
        while (scn.GetLSForcePileCount() > 3) scn.MoveCardsToTopOfLSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        scn.LSDeployCard(scn.GetLSCard("leebo"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
    }

    @Test
    public void LeeboCannotDeployWithOnlyTwoForce() {
        var scn = Ready();
        scn.MoveCardsToLSHand(scn.GetLSCard("leebo"));
        while (scn.GetLSForcePileCount() > 2) scn.MoveCardsToTopOfLSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(2, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("leebo")));
    }
}
