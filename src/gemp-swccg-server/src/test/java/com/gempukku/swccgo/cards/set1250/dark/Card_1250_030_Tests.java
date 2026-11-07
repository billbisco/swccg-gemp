package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1250_030_Tests {
    private static final String DOWNLOAD = "Deploy Block 5 Sidious from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "obi", "1_21", "luke", "1_19", "revolution", "1_62",
                "entrenchment", "7_61", "rebel", "1_28")),
                new HashMap<>(Map.of("office", "1250_30", "sidious", "1250_7", "sidious2", "1250_7",
                        "original", "14_78", "ovs", "1116_46", "trooper", "1_194", "senate", "12_167")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("office"));
        return scn;
    }

    @Test public void ChancellorsOfficePrintedAttributesAndIconsMatch() {
        var scn = scenario();
        var b = scn.GetDSCard("office").getBlueprint();
        assertEquals("Coruscant: Chancellor's Office", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardCategory.LOCATION, b.getCardCategory());
        assertEquals(CardSubtype.SITE, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(0, b.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERIOR_SITE, Icon.PLANET,
                Icon.CORUSCANT, Icon.EPISODE_I, Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        assertEquals(1, b.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, b.getIconCount(Icon.DARK_FORCE));
        assertTrue(GameTextActionId.LEGACY__CHANCELLORS_OFFICE__DEPLOY_SIDIOUS.isPerGame());
    }

    private void exactForce(VirtualTableScenario scn, int amount) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new));
        scn.EnsureDSForcePile(amount);
    }

    private VirtualTableScenario search(int force) {
        var scn = scenario();
        scn.MoveLocationToTable(scn.GetDSCard("senate"));
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sidious"), scn.GetDSCard("original"),
                scn.GetDSCard("ovs"), scn.GetDSCard("sidious2"));
        scn.SkipToPhase(Phase.CONTROL);
        exactForce(scn, force);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("sidious"), scn.GetDSCard("original"), scn.GetDSCard("ovs"));
        assertEquals(force, scn.GetDSForcePileCount());
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var ids = scn.DSGetBPChoices();
        var selectable = scn.GetADParamAsList(scn.DS, "selectable");
        for (int i = 0; i < ids.size() && i < selectable.size(); i++) {
            if (ids.get(i).equals(card.getBlueprintId(true)) && "true".equals(selectable.get(i))) return true;
        }
        return false;
    }

    private void deploySidious(VirtualTableScenario scn) {
        var office = scn.GetDSCard("office");
        var sidious = scn.GetDSCard("sidious");
        assertTrue(scn.DSCardActionAvailable(office, DOWNLOAD));
        scn.DSUseCardAction(office, DOWNLOAD);
        assertTrue(selectable(scn, sidious));
        assertFalse(selectable(scn, scn.GetDSCard("original")));
        assertFalse(selectable(scn, scn.GetDSCard("ovs")));
        scn.DSChooseCard(sidious);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("senate")));
        if (scn.DSHasCardChoiceAvailable(office)) scn.DSChooseCard(office);
        scn.PassAllResponses();
        assertEquals(office, sidious.getAtLocation());
    }

    @Test public void ChancellorsOfficeDeploysOnlyBlockFiveSidiousHereForSixForceAndReshuffles() {
        var scn = search(6);
        var remaining = new ArrayList<>(scn.gameState().getReserveDeck(scn.DS));
        assertTrue(remaining.remove(scn.GetDSCard("sidious")));
        scn.gameState().setRandomSeed(551L);
        deploySidious(scn);
        assertEquals(0, scn.GetDSForcePileCount());
        var reserve = new ArrayList<>(scn.gameState().getReserveDeck(scn.DS));
        assertEquals(remaining.size(), reserve.size());
        assertTrue(reserve.containsAll(remaining));
        assertNotEquals(remaining, reserve);
    }

    @Test public void ChancellorsOfficeCannotDeploySidiousWithFiveForce() {
        var scn = search(5);
        var office = scn.GetDSCard("office");
        if (scn.DSCardActionAvailable(office, DOWNLOAD)) {
            scn.DSUseCardAction(office, DOWNLOAD);
            assertFalse(selectable(scn, scn.GetDSCard("sidious")));
        }
        assertEquals(5, scn.GetDSForcePileCount());
        assertEquals(Zone.RESERVE_DECK, scn.GetDSCard("sidious").getZone());
    }

    @Test public void ChancellorsOfficeDoesNotRepeatDownloadOnLaterTurn() {
        var scn = search(6);
        deploySidious(scn);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("sidious"));
        scn.SkipToDSTurn(Phase.CONTROL);
        exactForce(scn, 6);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("sidious2"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("office"), DOWNLOAD));
    }

    @Test public void ChancellorsOfficeCannotDownloadDuringControlPhase() {
        var scn = scenario();
        scn.SkipToPhase(Phase.CONTROL);
        exactForce(scn, 6);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("office"), DOWNLOAD));
    }

    private void lightDrain(boolean jediHere, int expected) {
        var scn = scenario();
        var site = scn.GetDSCard("office");
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(jediHere ? site : scn.GetLSStartingLocation(), scn.GetLSCard("obi"));
        scn.SkipToLSTurn(Phase.CONTROL);
        int life = scn.GetDSLifeForceRemaining();
        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(expected, scn.GetForceDrainTotal());
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(life - expected, scn.GetDSLifeForceRemaining());
    }

    @Test public void ChancellorsOfficeLightDrainReducedByOneWithOnlyForceSensitiveLukeHere() {
        lightDrain(false, 1);
    }
    @Test public void ChancellorsOfficeLightDrainRestoredWithJediPresent() {
        lightDrain(true, 2);
    }

    @Test public void ChancellorsOfficeIsNotALegalRevolutionTargetButMarketplaceIs() {
        var scn = scenario();
        var revolution = scn.GetLSCard("revolution");
        var otherSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLSHand(revolution);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(3);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(revolution));
        scn.LSPlayCard(revolution);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("office")));
        scn.LSChooseCard(otherSite);
        scn.PassAllResponses();
        assertEquals(otherSite, revolution.getAttachedTo());
    }

    @Test public void ChancellorsOfficeDarkDrainCanBeCanceledEvenWithDarkJediHere() {
        var scn = scenario();
        var site = scn.GetDSCard("office");
        scn.MoveCardsToLocation(site, scn.GetDSCard("original"));
        scn.MoveCardsToSideOfTable(scn.GetLSCard("entrenchment"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rebel"));
        scn.SkipToPhase(Phase.CONTROL);
        int life = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(site);
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("entrenchment"), "Cancel Force drain"));
        scn.LSUseCardAction(scn.GetLSCard("entrenchment"), "Cancel Force drain");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("rebel"))) scn.LSChooseCard(scn.GetLSCard("rebel"));
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(life, scn.GetLSLifeForceRemaining());
    }
}
