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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_055_Tests {
    private static final String USED = "Place a 'spice' card in owner's Used Pile";
    private static final String OUT = "Place a 'spice' card out of play";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "droid", "2_14", "memorial", "1_45")),
                new HashMap<>(Map.of("facility", "1240_55", "office", "1273_18", "operations", "1273_19",
                        "kessel", "1_288", "spice", "1_194", "vader", "1_168")),
                35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("kessel"));
        scn.MoveLocationToTable(scn.GetDSCard("facility"));
        scn.MoveLocationToTable(scn.GetDSCard("office"));
        return scn;
    }

    @Test
    public void KesselExtractionFacilityMatchesBothPrintedSides() {
        var scn = Started();
        var card = scn.GetDSCard("facility").getBlueprint();
        assertEquals("Kessel: Spice Mines - Extraction Facility", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals("During your control phase, may use 1 Force to place a 'spice' card in owner's Used Pile.", card.getLocationDarkSideGameText());
        assertEquals("If you occupy, may use 3 Force to place a 'spice' card out of play.", card.getLocationLightSideGameText());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE,
                Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_4)));
    }

    private VirtualTableScenario Operations() {
        var scn = Started();
        var operations = scn.GetDSCard("operations");
        scn.MoveCardsToDSHand(operations);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(operations);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, operations);
        return scn;
    }

    private PhysicalCardImpl StackOwnSpice(VirtualTableScenario scn) {
        var spice = scn.GetDSCard("spice");
        var operations = scn.GetDSCard("operations");
        scn.MoveCardsToTopOfDSLostPile(spice);
        scn.EnsureDSForcePile(2);
        scn.DSUseCardAction(operations, "Stack a card from Lost Pile here");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Lost Pile")) scn.DSChooseCard(spice);
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(operations, spice));
        return spice;
    }

    private PhysicalCardImpl MineLightSpice(VirtualTableScenario scn) {
        var operations = scn.GetDSCard("operations");
        var spice = scn.GetLSDestiny(4);
        scn.PrepareLSDestiny(4);
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(operations, "'Mine'");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose number of destiny to draw"));
        scn.DSDecided("1");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose card to stack")) scn.DSChooseOption("Stack opponent's destiny");
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(operations, spice));
        return spice;
    }

    private void UseAndChoose(VirtualTableScenario scn, String player, String text, PhysicalCardImpl spice) {
        var facility = scn.GetDSCard("facility");
        if (player.equals(scn.DS)) scn.DSUseCardAction(facility, text);
        else scn.LSUseCardAction(facility, text);
        scn.PassAllResponses();
        if (scn.DecisionAvailable(player, "Choose card") && scn.GetADParam(player, "actionText") == null) {
            if (player.equals(scn.DS)) scn.DSChooseCard(spice);
            else scn.LSChooseCard(spice);
        }
        scn.PassAllResponses();
    }

    private void NextDarkControl(VirtualTableScenario scn, int force) {
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(force);
        scn.DrainDSForcePileTo(force);
    }

    @Test
    public void DarkCanPayOneWithoutOccupyingAndMovesOwnSpiceToUsed() {
        var scn = Operations();
        var spice = StackOwnSpice(scn);
        NextDarkControl(scn, 1);
        int used = scn.GetDSUsedPileCount();
        UseAndChoose(scn, scn.DS, USED, spice);
        assertInZone(Zone.USED_PILE, spice);
        assertEquals(0, scn.GetDSForcePileCount());
        assertEquals(used + 2, scn.GetDSUsedPileCount());
        assertFalse(scn.IsStackedOn(scn.GetDSCard("operations"), spice));
    }

    @Test
    public void DarkPlacesMinedLightCardInItsOwnersUsedPile() {
        var scn = Operations();
        var own = StackOwnSpice(scn);
        var spice = MineLightSpice(scn);
        NextDarkControl(scn, 1);
        int lsUsed = scn.GetLSUsedPileCount();
        int dsUsed = scn.GetDSUsedPileCount();
        UseAndChoose(scn, scn.DS, USED, spice);
        assertInZone(Zone.USED_PILE, spice);
        assertEquals(lsUsed + 1, scn.GetLSUsedPileCount());
        assertEquals(dsUsed + 1, scn.GetDSUsedPileCount());
        assertTrue(scn.IsStackedOn(scn.GetDSCard("operations"), own));
    }

    @Test
    public void DarkCannotUseOutsideOwnControlOrWithoutOneForce() {
        var scn = Operations();
        var spice = StackOwnSpice(scn);
        var facility = scn.GetDSCard("facility");
        scn.EnsureDSForcePile(1);
        assertFalse(scn.DSCardActionAvailable(facility, USED));
        scn.SkipToLSTurn(Phase.CONTROL);
        if (scn.AwaitingLSControlPhaseActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(facility, USED));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DrainDSForcePileTo(0);
        assertFalse(scn.DSCardActionAvailable(facility, USED));
        assertTrue(scn.IsStackedOn(scn.GetDSCard("operations"), spice));
    }

    private void ReachLightMove(VirtualTableScenario scn, int force, boolean occupied) {
        var facility = scn.GetDSCard("facility");
        scn.MoveCardsToLocation(facility, scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(occupied ? facility : scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.EnsureLSForcePile(force);
        scn.SkipToLSTurn(Phase.MOVE);
        scn.DrainLSForcePileTo(force);
    }

    @Test
    public void LightMayUseThreeInMovePhaseWhileContestingAndPlacesOnlyChosenSpiceOutOfPlay() {
        var scn = Operations();
        var own = StackOwnSpice(scn);
        var mined = MineLightSpice(scn);
        ReachLightMove(scn, 3, true);
        int used = scn.GetLSUsedPileCount();
        UseAndChoose(scn, scn.LS, OUT, own);
        assertInZone(Zone.OUT_OF_PLAY, own);
        assertTrue(scn.IsStackedOn(scn.GetDSCard("operations"), mined));
        assertEquals(used + 3, scn.GetLSUsedPileCount());
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void LightCanAlsoRemoveItsOwnCardMinedByDark() {
        var scn = Operations();
        var mined = MineLightSpice(scn);
        ReachLightMove(scn, 3, true);
        UseAndChoose(scn, scn.LS, OUT, mined);
        assertInZone(Zone.OUT_OF_PLAY, mined);
        assertFalse(scn.IsStackedOn(scn.GetDSCard("operations"), mined));
    }

    @Test
    public void LightWithoutOccupancyCannotActEvenWithThreeForceAndSpice() {
        var scn = Operations();
        var spice = StackOwnSpice(scn);
        ReachLightMove(scn, 3, false);
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("facility"), OUT));
        assertTrue(scn.IsStackedOn(scn.GetDSCard("operations"), spice));
    }

    @Test
    public void LightOccupyingCannotUseOnlyTwoForce() {
        var scn = Operations();
        StackOwnSpice(scn);
        ReachLightMove(scn, 2, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("facility"), OUT));
    }

    @Test
    public void ASpiceTitleAndUnrelatedStackDoNotCountAsSpiceCards() {
        var scn = Operations();
        var facility = scn.GetDSCard("facility");
        var memorial = scn.GetLSCard("memorial");
        var droid = scn.GetLSCard("droid");
        scn.MoveCardsToLSSideOfTable(memorial);
        scn.StackCardsOn(memorial, droid);
        NextDarkControl(scn, 1);
        assertFalse(scn.DSCardActionAvailable(facility, USED));
        ReachLightMove(scn, 3, true);
        assertFalse(scn.LSCardActionAvailable(facility, OUT));
        assertTrue(scn.IsStackedOn(memorial, droid));
        assertInZone(Zone.LOCATIONS, scn.GetDSCard("office"));
    }
}
