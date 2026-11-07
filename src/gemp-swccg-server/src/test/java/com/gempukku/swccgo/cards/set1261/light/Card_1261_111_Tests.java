package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_111_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("echo", "1261_111"); put("local", "7_137"); put("point", "7_123"); put("point2", "7_123");
            put("transport", "3_65"); put("transport2", "3_65"); put("corvette", "1_140");
            put("first", "3_39"); put("hoth", "3_55"); put("perimeter", "3_56");
            put("ridge", "3_62"); put("generators", "3_61"); put("kessel", "1_126");
            put("luke", "1_19"); put("rebel", "1_28");
        }}, new HashMap<>() {{
            put("catch", "3_131"); put("trooper", "1_194"); put("trooper2", "1_194"); put("trooper3", "1_194");
            put("devastator", "1_301");
        }}, 60, 60, StartingSetup.LSStartingLocation("2_65"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame();
        for (String key : new String[]{"echo", "hoth", "perimeter", "ridge", "generators", "kessel"})
            scn.MoveLocationToTable(scn.GetLSCard(key));
        System.out.println("ECHO START " + scn.gameState().getCurrentPhase() + " " + scn.GetCurrentDecision().getText());
        return scn;
    }
    @Test public void EchoCommandCenterStatsMatchPrintedLegacyMaster() {
        var scn = scenario(); var b = scn.GetLSCard("echo").getBlueprint();
        assertEquals(Title.Echo_Command_Center, b.getTitle()); assertEquals(Title.Hoth, b.getSystemName());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.SITE, b.getCardSubtype());
        assertEquals(2, b.getIconCount(Icon.LIGHT_FORCE)); assertEquals(1, b.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.LIGHT_FORCE, Icon.DARK_FORCE,
                Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.WAR_ROOM)));
    }
    private VirtualTableScenario search(int force, boolean local, Phase phase, boolean lsTurn) {
        var scn = started();
        if (local) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("local"));
        if (lsTurn) scn.SkipToLSTurn(Phase.CONTROL); else scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("point"), scn.GetLSCard("point2"));
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile());
        scn.LSActivateForceCheat(force);
        // Refresh the decision after moving fixture cards out of piles.
        if (lsTurn) {
            scn.LSPass(); scn.DSPass();
            if (phase == Phase.MOVE) scn.SkipToPhase(Phase.MOVE);
        } else scn.DSPass();
        return scn;
    }
    private void takePoint(VirtualTableScenario scn, String key) {
        System.out.println("ECHO SEARCH " + scn.gameState().getCurrentPhase() + " " + scn.GetCurrentDecision().getText() + " " + scn.GetLSAvailableActions());
        scn.LSUseCardAction(scn.GetLSCard("echo"), "Take Rendezvous Point"); scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard(key)));
        scn.LSChooseCard(scn.GetLSCard(key)); scn.PassAllResponses();
        assertInHand(scn.GetLSCard(key));
    }
    private boolean offersSearch(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream().anyMatch(s -> s.contains("Take Rendezvous Point"));
    }
    @Test public void EchoCommandCenterActuallyTakesRendezvousForOneForce() {
        var scn = search(1, true, Phase.CONTROL, true); takePoint(scn, "point");
        assertEquals(0, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("point2"));
    }
    @Test public void EchoCommandCenterSearchHasNoInventedOncePerTurnLimit() {
        var scn = search(2, true, Phase.CONTROL, true); takePoint(scn, "point");
        scn.DSPass(); takePoint(scn, "point2"); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void EchoCommandCenterSearchWorksDuringOwnMovePhase() {
        var scn = search(1, true, Phase.MOVE, true); takePoint(scn, "point"); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void EchoCommandCenterSearchWorksDuringOpponentTurn() {
        var scn = search(1, true, Phase.CONTROL, false); takePoint(scn, "point"); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void EchoCommandCenterDoesNotOfferSearchWithoutLocalUprising() {
        var scn = search(2, false, Phase.CONTROL, true); assertFalse(offersSearch(scn));
    }
    @Test public void EchoCommandCenterDoesNotOfferUnaffordableSearch() {
        var scn = search(0, true, Phase.CONTROL, true); assertFalse(offersSearch(scn));
    }
    @Test public void EchoCommandCenterSearchCannotTakeUnrelatedCard() {
        var scn = search(1, true, Phase.CONTROL, true);
        scn.LSUseCardAction(scn.GetLSCard("echo"), "Take Rendezvous Point"); scn.PassAllResponses();
        String[] ids = scn.LSGetADParam("blueprintId"), enabled = scn.LSGetADParam("selectable");
        assertNotNull(ids); assertNotNull(enabled);
        for (int i = 0; i < ids.length; i++)
            if (Boolean.parseBoolean(enabled[i])) assertEquals("7_123", ids[i]);
        scn.LSChooseCard(scn.GetLSCard("point")); scn.PassAllResponses(); assertInHand(scn.GetLSCard("point"));
    }
    @Test public void EchoCommandCenterBoostsOwnTransportsGloballyWithoutLocalUprising() {
        var scn = started(); var a = scn.GetLSCard("transport"); var b = scn.GetLSCard("transport2");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), a); scn.MoveCardsToLocation(scn.GetLSCard("kessel"), b);
        assertEquals(6, scn.GetHyperspeed(a)); assertEquals(6, scn.GetHyperspeed(b));
    }
    @Test public void EchoCommandCenterDoesNotBoostNonTransportStarships() {
        var scn = started(); var ship = scn.GetLSCard("corvette"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        assertEquals(3, scn.GetHyperspeed(ship));
    }
    @Test public void EchoCommandCenterDoesNotBoostOpposingStarships() {
        var scn = started(); var ship = scn.GetDSCard("devastator"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        assertEquals(3, scn.GetHyperspeed(ship));
    }
    @Test public void EchoCommandCenterTransportBonusEndsWhenSourceLeavesTable() {
        var scn = started(); var ship = scn.GetLSCard("transport"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        assertEquals(6, scn.GetHyperspeed(ship)); scn.MoveCardsToLSHand(scn.GetLSCard("echo")); assertEquals(4, scn.GetHyperspeed(ship));
    }
    @Test public void EchoCommandCenterEnablesActualFiveParsecTransportMove() {
        var scn = started(); var ship = scn.GetLSCard("transport"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        scn.SkipToLSTurn(Phase.MOVE); scn.LSActivateForceCheat(5);
        scn.LSMoveCard(ship, scn.GetLSCard("kessel")); scn.PassAllResponses(); assertAtLocation(scn.GetLSCard("kessel"), ship);
    }
    @Test public void EchoCommandCenterAbsentCannotMakeSameFiveParsecMove() {
        var scn = started(); var ship = scn.GetLSCard("transport"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        scn.MoveCardsToLSHand(scn.GetLSCard("echo")); scn.SkipToLSTurn(Phase.MOVE); scn.LSActivateForceCheat(5);
        scn.LSUseCardAction(ship, "Move"); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("kessel")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("hoth")));
    }
    @Test public void EchoCommandCenterCannotBeConvertedWithoutProtectingOtherLocations() {
        var scn = started(); var q = scn.game().getModifiersQuerying();
        assertTrue(q.cannotBeConverted(scn.gameState(), scn.GetLSCard("echo")));
        assertFalse(q.cannotBeConverted(scn.gameState(), scn.GetLSCard("ridge")));
    }
    private VirtualTableScenario cancellation(int related, boolean echo, boolean contested) {
        var scn = started(); scn.AttachCardsTo(scn.GetLSCard("kessel"), scn.GetLSCard("first"));
        scn.MoveCardsToDSHand(scn.GetDSCard("catch"));
        String[] sites = {"perimeter", "ridge", "generators"}; String[] men = {"trooper", "trooper2", "trooper3"};
        for (int i = 0; i < related; i++) scn.MoveCardsToLocation(scn.GetLSCard(sites[i]), scn.GetDSCard(men[i]));
        if (echo) scn.MoveCardsToLocation(scn.GetLSCard("echo"), scn.GetDSCard("trooper"));
        if (contested) scn.MoveCardsToLocation(scn.GetLSCard(echo ? "echo" : "perimeter"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.CONTROL); return scn;
    }
    private void assertProtected(VirtualTableScenario scn) {
        assertFalse(GameConditions.canTargetToCancel(scn.game(), scn.GetDSCard("catch"), scn.GetLSCard("first")));
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("catch")));
        assertEquals(scn.GetLSCard("kessel"), scn.GetLSCard("first").getAttachedTo());
    }
    private void actuallyCancel(VirtualTableScenario scn) {
        assertTrue(GameConditions.canTargetToCancel(scn.game(), scn.GetDSCard("catch"), scn.GetLSCard("first")));
        scn.DSPlayCard(scn.GetDSCard("catch")); scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("first"))) scn.DSChooseCard(scn.GetLSCard("first"));
        scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetLSCard("first"));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("catch"));
    }
    @Test public void EchoCommandCenterProtectsFirstTransportWithNoDarkOccupation() { assertProtected(cancellation(0, false, false)); }
    @Test public void EchoCommandCenterProtectsFirstTransportWithOnlyTwoRelatedLocations() { assertProtected(cancellation(2, false, false)); }
    @Test public void EchoCommandCenterThreeRelatedLocationsAllowActualCancellation() { actuallyCancel(cancellation(3, false, false)); }
    @Test public void EchoCommandCenterDarkOccupationOfEchoAloneAllowsActualCancellation() { actuallyCancel(cancellation(0, true, false)); }
    @Test public void EchoCommandCenterContestedEchoStillCountsAsOccupation() { actuallyCancel(cancellation(0, true, true)); }
    @Test public void EchoCommandCenterContestedRelatedSiteStillCountsAsOccupation() { actuallyCancel(cancellation(3, false, true)); }
    @Test public void EchoCommandCenterUnrelatedOccupationDoesNotRemoveImmunity() {
        var scn = cancellation(2, false, false); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("devastator"));
        assertProtected(scn);
    }
    @Test public void EchoCommandCenterOccupationRemovalRestoresImmunity() {
        var scn = cancellation(3, false, false);
        assertTrue(GameConditions.canTargetToCancel(scn.game(), scn.GetDSCard("catch"), scn.GetLSCard("first")));
        scn.MoveCardsToDSHand(scn.GetDSCard("trooper3"));
        assertFalse(GameConditions.canTargetToCancel(scn.game(), scn.GetDSCard("catch"), scn.GetLSCard("first")));
    }
    @Test public void EchoCommandCenterSourceAbsentAllowsActualCancellation() {
        var scn = cancellation(0, false, false); scn.MoveCardsToLSHand(scn.GetLSCard("echo"));
        scn.SkipToDSTurn(Phase.CONTROL); actuallyCancel(scn);
    }
}
