package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.MayMoveAwayAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_030_Tests {
    private static final String RETRIEVE = "Lose 1 Force to retrieve a trooper into hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("trooper", "1_28"); put("cost", "1_66"); put("echo", "3_6"); put("xwing", "1_146");
            put("droid", "1_1"); put("remote", "1_126");
        }}, new HashMap<>() {{
            put("tie", "1_304");
        }}, 60, 60, StartingSetup.LSStartingLocation("1263_30"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void DresselUsesPrintedForceIconsAndParsec() {
        var scn = scenario(); var b = scn.GetLSStartingLocation().getBlueprint();
        assertEquals("Dressel", b.getTitle()); assertEquals("Dressel", b.getSystemName());
        assertEquals(2, b.getParsec()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.SYSTEM, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.PLANET, Icon.LIGHT_FORCE, Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.LEGACY_BLOCK_6)));
    }

    private VirtualTableScenario setup(boolean occupy, boolean contested) {
        var scn = scenario(); scn.StartGame(); var dressel = scn.GetLSStartingLocation(); var remote = scn.GetLSCard("remote");
        scn.MoveLocationToTable(remote);
        scn.MoveCardsToLocation(occupy ? dressel : remote, scn.GetLSCard("xwing"));
        if (contested) scn.MoveCardsToLocation(dressel, scn.GetDSCard("tie"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("trooper"), scn.GetLSCard("echo"), scn.GetLSCard("droid"));
        return scn;
    }

    @Test public void DresselLosesExactlyOneThenRetrievesATrooperFromBelowNonTrooperIntoHand() {
        var scn = setup(true, true); var dressel = scn.GetLSStartingLocation(); var trooper = scn.GetLSCard("trooper");
        var droid = scn.GetLSCard("droid"); var cost = scn.GetLSCard("cost");
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(cost); // after the activate phase, so it is still the top card
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(dressel, RETRIEVE); scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(cost));
        scn.LSChooseCard(cost); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertInHand(trooper); assertInZone(Zone.LOST_PILE, cost); assertInZone(Zone.LOST_PILE, droid);
        assertEquals(lost, scn.GetLSLostPileCount());
        if (scn.DSDecisionAvailable("Control action")) scn.DSPass(); // players alternate control phase actions
        // "During your control phase ... retrieve a trooper": once per control phase (the One Rule), even while another
        // trooper (Echo Base Trooper) is in the Lost Pile.
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
        // Available again in your next control phase.
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
    }

    @Test public void DresselRequiresOccupationAtThisSystem() {
        var scn = setup(false, false); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
    }

    @Test public void DresselRetrievalIsNotAnActionInEitherWrongPhaseOrOpponentControl() {
        var scn = setup(true, false); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(text -> text.contains(RETRIEVE)));
    }

    @Test public void DresselActuallyReducesDarkDrainFromTwoToOne() {
        var scn = scenario(); scn.StartGame(); var dressel = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(dressel, scn.GetDSCard("tie")); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(dressel); scn.PassAllResponses(); assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test public void DresselDoesNotReduceLightDrain() {
        var scn = setup(true, false); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(scn.GetLSStartingLocation()); scn.PassAllResponses(); assertEquals(1, scn.GetForceDrainTotal());
    }

    // Despair clause. Despair (V) prohibits movement "during your battle phase, while you have a frozen captive";
    // the tests stand in for that printed condition with an ad hoc "may not move" modifier whose source is Despair,
    // so they check exactly what Dressel changes: whose prohibition is ignored, and for which movement.
    private static final String REACT = "as a 'react'";

    private VirtualTableScenario despairScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wookiee", "1116_18"); put("luke", "1_19"); put("db94", "1_129");
        }}, new HashMap<>() {{
            put("despair", "1106_32"); put("trooper", "1_194");
        }}, 60, 60, StartingSetup.LSStartingLocation("1263_30"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    /** Wookiee (may move as a 'react') at Docking Bay 94, next to the Marketplace where Luke and a Stormtrooper are. */
    private VirtualTableScenario despairTable(boolean wookieeInBattle) {
        var scn = despairScenario(); scn.StartGame();
        var marketplace = scn.GetDSStartingLocation(); var db94 = scn.GetLSCard("db94");
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("despair"));
        scn.MoveCardsToLocation(marketplace, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(wookieeInBattle ? marketplace : db94, scn.GetLSCard("wookiee"));
        return scn;
    }

    private void prohibitWookieeMovementFrom(VirtualTableScenario scn, PhysicalCardImpl source) {
        scn.ApplyAdHocModifier(new MayNotMoveModifier(source, Filters.sameCardId(scn.GetLSCard("wookiee"))));
    }

    private void dsBattleAtMarketplace(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4); scn.EnsureLSForcePile(4);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
    }

    private boolean reactAvailable(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(REACT));
    }

    @Test public void DresselLetsCardsMoveAsReactToBattleDespiteDespair() {
        var scn = despairTable(false); var wookiee = scn.GetLSCard("wookiee"); var marketplace = scn.GetDSStartingLocation();
        prohibitWookieeMovementFrom(scn, scn.GetDSCard("despair"));
        dsBattleAtMarketplace(scn);
        assertTrue(reactAvailable(scn));
        scn.LSUseCardAction(wookiee, "Move using landspeed as a 'react'");
        if (scn.LSHasCardChoiceAvailable(marketplace)) scn.LSChooseCard(marketplace);
        scn.PassAllResponses();
        assertEquals(marketplace, wookiee.getAtLocation());
    }

    @Test public void DresselDoesNotStopOtherCardsPreventingReacts() {
        var scn = despairTable(false);
        prohibitWookieeMovementFrom(scn, scn.GetDSCard("trooper"));
        dsBattleAtMarketplace(scn);
        assertFalse(reactAvailable(scn));
    }

    @Test public void DespairStillPreventsRegularMovementWithDressel() {
        var scn = despairTable(false);
        prohibitWookieeMovementFrom(scn, scn.GetDSCard("despair"));
        scn.SkipToLSTurn(Phase.MOVE); scn.EnsureLSForcePile(4);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("wookiee")));
    }

    @Test public void DespairStillPreventsReactsAwayFromBattleWithDressel() {
        var scn = despairTable(true); var wookiee = scn.GetLSCard("wookiee");
        scn.ApplyAdHocModifier(new MayMoveAwayAsReactModifier(wookiee));
        prohibitWookieeMovementFrom(scn, scn.GetDSCard("despair"));
        dsBattleAtMarketplace(scn);
        assertFalse(reactAvailable(scn));
    }

    @Test public void ReactAwayIsAvailableWithoutTheDespairProhibition() {
        var scn = despairTable(true); var wookiee = scn.GetLSCard("wookiee");
        scn.ApplyAdHocModifier(new MayMoveAwayAsReactModifier(wookiee));
        dsBattleAtMarketplace(scn);
        assertTrue(reactAvailable(scn));
    }

}
