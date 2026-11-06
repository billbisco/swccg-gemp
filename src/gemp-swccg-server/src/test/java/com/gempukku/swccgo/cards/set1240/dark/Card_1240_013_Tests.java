package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_013_Tests {
    private static final String SEARCH = "Take Unexpected Interruption into hand";

    private boolean IsSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(scn.DS, "cardId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        assertNotNull(ids);
        assertNotNull(selectable);
        assertEquals(ids.length, selectable.length);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(String.valueOf(card.getCardId()))) return Boolean.parseBoolean(selectable[i]);
        }
        return false;
    }

    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("han", "1_11"); put("obiwan", "1_21");
        }}, new HashMap<>() {{
            put("xizor", "1240_13"); put("guri", "10_41");
            put("smuggler", "1_172"); put("thief", "2_102");
            put("gambler", "5_99"); put("gangster", "6_109");
            put("restricted", "2_82"); put("nonunique", "7_195");
            put("motti", "1_164"); put("trooper", "1_194");
            put("unexpected", "4_152"); put("ship", "1_301");
            put("bike", "8_169"); put("system", "1_282");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void PrinceXizorPrintedStatsIconsKeywordsAndSpeciesAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("xizor").getBlueprint();
        assertEquals(Title.Xizor, bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(5, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getAbility(), scn.epsilon);
        assertEquals(6, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Species.FALLEEN, bp.getSpecies());
        assertEquals("Falleen gangster and leader. Black Sun agent. Dark Prince of the Black Sun crime syndicate. Fortifies his personal defenses with information gathered by his agents.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.GANGSTER, Keyword.LEADER, Keyword.BLACK_SUN_AGENT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.PILOT, Icon.WARRIOR,
                Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
    }

    private VirtualTableScenario AgentsReady() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"),
                scn.GetDSCard("guri"), scn.GetDSCard("smuggler"), scn.GetDSCard("thief"),
                scn.GetDSCard("gangster"), scn.GetDSCard("restricted"),
                scn.GetDSCard("nonunique"), scn.GetDSCard("motti"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("gambler"));
        return scn;
    }

    @Test
    public void PrinceXizorMakesEachUniqueProfessionABlackSunAgentAnywhereOnTable() {
        var scn = AgentsReady();
        for (String key : List.of("smuggler", "thief", "gambler", "gangster")) {
            assertTrue(scn.HasKeyword(scn.GetDSCard(key), Keyword.BLACK_SUN_AGENT));
        }
        assertEquals(4, scn.GetForfeit(scn.GetDSCard("smuggler")));
        assertEquals(4, scn.GetForfeit(scn.GetDSCard("thief")));
        assertEquals(4, scn.GetForfeit(scn.GetDSCard("gambler")));
        assertEquals(8, scn.GetForfeit(scn.GetDSCard("gangster")));
    }

    @Test
    public void PrinceXizorDoesNotConvertRestrictedNonuniqueUnrelatedOrOpposingCharacters() {
        var scn = AgentsReady();
        for (String key : List.of("restricted", "nonunique", "motti")) {
            assertFalse(scn.HasKeyword(scn.GetDSCard(key), Keyword.BLACK_SUN_AGENT));
        }
        assertFalse(scn.HasKeyword(scn.GetLSCard("han"), Keyword.BLACK_SUN_AGENT));
        assertEquals(2, scn.GetForfeit(scn.GetDSCard("restricted")));
        assertEquals(3, scn.GetForfeit(scn.GetDSCard("nonunique")));
    }

    @Test
    public void PrinceXizorIncreasesOtherNativeAgentsForfeitButNotHisOwn() {
        var scn = AgentsReady();
        assertEquals(8, scn.GetForfeit(scn.GetDSCard("guri")));
        assertEquals(6, scn.GetForfeit(scn.GetDSCard("xizor")));
    }

    @Test
    public void PrinceXizorLosesConversionAndForfeitBonusWhenHeLeavesPlay() {
        var scn = AgentsReady();
        scn.MoveCardsToDSHand(scn.GetDSCard("xizor"));
        assertFalse(scn.HasKeyword(scn.GetDSCard("smuggler"), Keyword.BLACK_SUN_AGENT));
        assertEquals(3, scn.GetForfeit(scn.GetDSCard("smuggler")));
        assertTrue(scn.HasKeyword(scn.GetDSCard("guri"), Keyword.BLACK_SUN_AGENT));
        assertEquals(7, scn.GetForfeit(scn.GetDSCard("guri")));
    }

    @Test
    public void PrinceXizorAddsTwoPowerToAStarshipHePilots() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("ship"));
        scn.BoardAsPilot(scn.GetDSCard("ship"), scn.GetDSCard("xizor"));
        assertEquals(11, scn.GetPower(scn.GetDSCard("ship")));
        scn.MoveCardsToDSHand(scn.GetDSCard("xizor"));
        assertEquals(9, scn.GetPower(scn.GetDSCard("ship")));
    }

    @Test
    public void PrinceXizorAddsTwoPowerAsAVehiclePilotButNotAsPassenger() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("bike"));
        scn.BoardAsPilot(scn.GetDSCard("bike"), scn.GetDSCard("xizor"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
        scn.BoardAsPassenger(scn.GetDSCard("bike"), scn.GetDSCard("xizor"));
        scn.BoardAsPilot(scn.GetDSCard("bike"), scn.GetDSCard("motti"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
        scn.MoveCardsToDSHand(scn.GetDSCard("xizor"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
    }

    private VirtualTableScenario SearchReady() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveOutOfPlay(scn.GetDSCard("unexpected"));
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("unexpected"));
        return scn;
    }

    @Test
    public void PrinceXizorTakesUnexpectedInterruptionIntoHandAtNoForceCost() {
        var scn = SearchReady();
        int force = scn.GetDSForcePileCount();
        int reserve = scn.GetDSReserveDeckCount();
        var order = new ArrayList<>(scn.gameState().getReserveDeck(scn.DS));
        order.remove(scn.GetDSCard("unexpected"));
        scn.gameState().setRandomSeed(124013);
        scn.DSUseCardAction(scn.GetDSCard("xizor"), SEARCH);
        scn.PassAllResponses();
        assertTrue(IsSelectable(scn, scn.GetDSCard("unexpected")));
        assertFalse(IsSelectable(scn, scn.GetDSCard("trooper")));
        scn.DSChooseCard(scn.GetDSCard("unexpected"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("unexpected"));
        assertEquals(force, scn.GetDSForcePileCount());
        assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
        assertNotEquals(order, new ArrayList<>(scn.gameState().getReserveDeck(scn.DS)));
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("xizor"), SEARCH));
    }

    @Test
    public void PrinceXizorSearchLimitPersistsAcrossPhasesAndResetsNextTurn() {
        var scn = SearchReady();
        scn.DSUseCardAction(scn.GetDSCard("xizor"), SEARCH);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("unexpected"));
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("xizor"), SEARCH));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("unexpected"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("xizor"), SEARCH));
        scn.DSUseCardAction(scn.GetDSCard("xizor"), SEARCH);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("unexpected"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("unexpected"));
    }

    private VirtualTableScenario ImmunityReady(int destiny) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("xizor"), scn.GetDSCard("trooper"), scn.GetLSCard("obiwan"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(destiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(destiny, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("trooper"));
        return scn;
    }

    @Test
    public void PrinceXizorSurvivesThreeAttritionAfterAnotherCharacterIsForfeited() {
        var scn = ImmunityReady(3);
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"));
    }

    @Test
    public void PrinceXizorMustStillSatisfyAttritionAtExactlyFour() {
        var scn = ImmunityReady(4);
        assertTrue(scn.GetUnpaidDSAttrition() > 0);
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("xizor"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("xizor"));
    }

    @Test
    public void PrinceXizorDeploysToASiteForFourForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("xizor"));
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("xizor"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"));
        assertEquals(force - 4, scn.GetDSForcePileCount());
    }

    @Test
    public void PrinceXizorCannotDeployWithOnlyThreeForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("xizor"));
        scn.EnsureDSForcePile(3);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(3, scn.GetDSForcePileCount());
        assertFalse(scn.DSDeployAvailable(scn.GetDSCard("xizor")));
    }
}
