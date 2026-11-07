package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
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
        scn.MoveCardsToTopOfLSReserveDeck(cost);
        scn.SkipToLSTurn(Phase.CONTROL);
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(dressel, RETRIEVE); scn.PassAllResponses();
        scn.LSChooseCard(cost); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertInHand(trooper); assertInZone(Zone.LOST_PILE, cost); assertInZone(Zone.LOST_PILE, droid);
        assertEquals(lost, scn.GetLSLostPileCount());
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
}
