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
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(4, bp.getArmor(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
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
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboMayPeekAgainNextTurn() {
        var scn = Ready();
        Peek(scn);
        scn.LSChooseNo();
        scn.PassAllResponses();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        // The non-turn player receives a normal control action opportunity.
        if (scn.DSAnyActionsAvailable()) scn.DSPass();
        assertTrue(scn.LSAnyActionsAvailable());
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCannotPeekAtNonBattlegroundButRegainsActionOnReturning() {
        var scn = Ready();
        scn.MoveLocationToTable(scn.GetLSCard("warroom"));
        scn.MoveCardsToLocation(scn.GetLSCard("warroom"), scn.GetLSCard("leebo"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCannotPeekEmptyOpponentReserveEvenWithCardsInOwnReserve() {
        var scn = Ready();
        scn.MoveCardsToDSLostPile(scn.GetDSReserveDeck().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertTrue(scn.GetLSReserveDeckCount() > 0);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leebo"), "Peek at top of opponent's Reserve Deck"));
    }

    @Test
    public void LeeboCanMoveTheOnlyOpponentReserveCardToUsedPile() {
        var scn = Ready();
        scn.MoveCardsToDSLostPile(scn.GetDSReserveDeck().subList(1, scn.GetDSReserveDeckCount()).toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertEquals(1, scn.GetDSReserveDeckCount());
        Peek(scn);
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSReserveDeckCount());
        assertEquals(scn.GetDSCard("top"), scn.GetDSUsedPile().getFirst());
    }

    @Test
    public void LeeboAloneMayBeBattledUnlikeAnOrdinaryDroid() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("threepio"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(5);
        assertFalse(scn.DSCanInitiateBattle(scn.GetLSStartingLocation()));
        scn.MoveCardsToLSHand(scn.GetLSCard("threepio"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
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

    @Test
    public void LeeboWithDashAddsActualAttritionDestinyWithoutAddingItToPower() {
        var scn = BattleReady(true);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        assertEquals(10, scn.GetLSTotalPower()); // Leebo2+Dash3+Luke3+normalDestiny2.
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(5, scn.GetUnpaidDSAttrition()); // Normal2+attritionOnly3.
        assertEquals(3, scn.GetUnpaidDSBattleDamage()); // Vader's normal destiny0 becomes1: DS power7.
    }

    @Test
    public void LeeboDoesNotAddAttritionDestinyWhenDashIsAtAnotherSite() {
        var scn = BattleReady(false);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
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
    public void LeeboDeploysForExactlyTwoForce() {
        var scn = Ready();
        scn.MoveCardsToLSHand(scn.GetLSCard("leebo"));
        scn.SkipToPhase(Phase.DEPLOY);
        while (scn.GetLSForcePileCount() > 2) scn.MoveCardsToTopOfLSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        scn.LSDeployCard(scn.GetLSCard("leebo"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
    }

    @Test
    public void LeeboCannotDeployWithOnlyOneForce() {
        var scn = Ready();
        scn.MoveCardsToLSHand(scn.GetLSCard("leebo"));
        while (scn.GetLSForcePileCount() > 1) scn.MoveCardsToTopOfLSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) scn.GetLSForcePile().getFirst());
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(1, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("leebo")));
    }
}
