package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1263_022_Tests {
    private static final String REARRANGE = "Rearrange all Blockade Flagship sites";
    private static final String ADD = "Use Force to add to battle destiny";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("general", "1263_22"); put("general2", "1263_22"); put("prison", "1116_7");
            put("dock", "14_48"); put("luke", "1_19"); put("palpatine", "12_28"); put("jawa", "1_12");
        }}, new HashMap<>() {{
            put("bridge", "12_164"); put("hallway", "13_57"); put("trooper", "1_194"); put("trooper2", "1_194"); put("dsdock", "14_111");
        }}, 60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void SolomahalUsesFinalRepublicPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("general").getBlueprint();
        assertEquals("General Solomahal", b.getTitle());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon);
        assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(3, b.getArmor(), scn.epsilon);
        assertEquals(3, b.getForfeit(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardCategory.CHARACTER, b.getCardCategory());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REPUBLIC)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REPUBLIC, Icon.EPISODE_I, Icon.WARRIOR, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.GENERAL, Keyword.LEADER, Keyword.SCOUT)));
    }

    private PhysicalCardImpl start(VirtualTableScenario scn) {
        scn.StartGame(); var general = scn.GetLSCard("general");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), general);
        return general;
    }

    @Test public void SolomahalReordersAllFlagshipSitesAndTheirCardsWithoutMovingOtherSites() {
        var scn = scenario(); var general = start(scn);
        var dock = scn.GetLSCard("dock"); var prison = scn.GetLSCard("prison");
        var hallway = scn.GetDSCard("hallway"); var bridge = scn.GetDSCard("bridge");
        var trooper = scn.GetDSCard("trooper"); var luke = scn.GetLSCard("luke");
        for (var site : List.of(bridge, dock, prison, hallway)) scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(dock, trooper);
        scn.MoveCardsToLocation(prison, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        var original = new ArrayList<>(List.of(bridge, dock, prison, hallway));
        original.sort(Comparator.comparingInt(PhysicalCardImpl::getLocationZoneIndex));
        var chosen = new ArrayList<>(original.reversed());
        int unrelatedIndex = scn.GetLSStartingLocation().getLocationZoneIndex();
        scn.LSUseCardAction(general, REARRANGE);
        assertTrue(scn.LSHasCardChoicesAvailable(dock, prison, hallway, bridge));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
        scn.LSChooseCard(chosen.get(0));
        assertTrue(original.get(0).getLocationZoneIndex() < original.get(3).getLocationZoneIndex());
        scn.LSChooseCard(chosen.get(1)); scn.LSChooseCard(chosen.get(2));
        if (scn.LSHasCardChoiceAvailable(chosen.get(3))) scn.LSChooseCard(chosen.get(3));
        for (int i = 1; i < chosen.size(); i++) {
            assertTrue(chosen.get(i - 1).getLocationZoneIndex() < chosen.get(i).getLocationZoneIndex());
        }
        assertEquals(unrelatedIndex, scn.GetLSStartingLocation().getLocationZoneIndex());
        assertEquals(dock, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), trooper));
        assertEquals(prison, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), luke));
        scn.MoveCardsToLSHand(general);
        var other = scn.GetLSCard("general2");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), other);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(REARRANGE)));
    }

    @Test public void SolomahalCannotRearrangeUnrelatedLocations() {
        var scn = scenario(); start(scn); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(REARRANGE)));
    }

    /**
     * [Virtual Block 5] Palpatine (1250_53) is not on this branch. Stand-in: Senator Palpatine (12_28, Sidious
     * persona) given the [Virtual Block 5] icon by an ad hoc modifier. Without the modifier it is a non-VB5 Palpatine.
     */
    private void makeBlockFivePalpatine(VirtualTableScenario scn, PhysicalCardImpl palpatine) {
        scn.ApplyAdHocModifier(new IconModifier(scn.GetDSStartingLocation(), Filters.sameCardId(palpatine), Icon.LEGACY_BLOCK_5));
    }

    /** Battle at the LS starting site (Solomahal and Luke vs a Stormtrooper) up to the moment the LS battle destiny is drawn. */
    private void battleAndDraw(VirtualTableScenario scn, PhysicalCardImpl site, int destiny, boolean firstBattle) {
        if (firstBattle) {
            scn.SkipToLSTurn(Phase.CONTROL);
            scn.LSActivateForceCheat(6);
            scn.SkipToPhase(Phase.BATTLE);
        }
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(destiny);
        scn.SkipToPowerSegment();
        assertTrue(scn.LSDecisionAvailable("battle destiny"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.DSPass(); // DESTINY_DRAWN: Dark Side responds first, then the Light Side may use Solomahal
    }

    private boolean addAvailable(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(ADD));
    }

    private void putBattleOnTable(VirtualTableScenario scn, PhysicalCardImpl site) {
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
    }

    @Test public void SolomahalCannotBoostAThreeWithANonBlockFivePalpatine() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), scn.GetLSCard("palpatine"));
        putBattleOnTable(scn, site);
        battleAndDraw(scn, site, 3, true);
        assertFalse(addAvailable(scn));
        scn.PassDestinyDrawResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount()); assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test public void SolomahalUsesXForceToAddXToABattleDestinyOfThreeWithBlockFivePalpatineOnTable() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        var palpatine = scn.GetLSCard("palpatine");
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), palpatine);
        makeBlockFivePalpatine(scn, palpatine);
        putBattleOnTable(scn, site);
        battleAndDraw(scn, site, 3, true);
        assertTrue(addAvailable(scn));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("general"), ADD);
        assertEquals(1, scn.LSGetChoiceMin());
        assertEquals(3, scn.LSGetChoiceMax()); // limit 3 even with more Force available
        scn.LSDecided("2");
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertEquals(1, scn.GetLSBattleDestinyCount());
        assertEquals(5, scn.GetLSTotalDestiny());
    }

    @Test public void SolomahalWorksWithBlockFivePalpatineImprisoned() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        var palpatine = scn.GetLSCard("palpatine"); var prison = scn.GetLSCard("prison");
        scn.MoveLocationToTable(prison);
        scn.gameState().imprisonCharacter(scn.game(), palpatine, prison);
        makeBlockFivePalpatine(scn, palpatine);
        putBattleOnTable(scn, site);
        battleAndDraw(scn, site, 3, true);
        assertTrue(palpatine.isCaptive());
        assertTrue(addAvailable(scn));
        scn.LSUseCardAction(scn.GetLSCard("general"), ADD);
        scn.LSDecided("3");
        scn.PassAllResponses();
        assertEquals(6, scn.GetLSTotalDestiny());
    }

    @Test public void SolomahalDoesNotBoostADestinyOtherThanThree() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        var palpatine = scn.GetLSCard("palpatine");
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), palpatine);
        makeBlockFivePalpatine(scn, palpatine);
        putBattleOnTable(scn, site);
        battleAndDraw(scn, site, 2, true);
        assertFalse(addAvailable(scn));
        scn.PassDestinyDrawResponses();
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test public void SolomahalXIsLimitedToForceAvailable() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        var palpatine = scn.GetLSCard("palpatine");
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), palpatine);
        makeBlockFivePalpatine(scn, palpatine);
        putBattleOnTable(scn, site);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToBottomOfLSReserveDeck(scn.GetLSForcePile().stream().map(c -> (PhysicalCardImpl) c).toArray(PhysicalCardImpl[]::new));
        scn.LSActivateForceCheat(3); // 1 is used to initiate the battle
        scn.SkipToPhase(Phase.BATTLE);
        battleAndDraw(scn, site, 3, false);
        assertEquals(2, scn.GetLSForcePileCount());
        assertTrue(addAvailable(scn));
        scn.LSUseCardAction(scn.GetLSCard("general"), ADD);
        assertEquals(2, scn.LSGetChoiceMax());
    }

    @Test public void SolomahalBoostIsOncePerTurn() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        var palpatine = scn.GetLSCard("palpatine");
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), palpatine);
        makeBlockFivePalpatine(scn, palpatine);
        putBattleOnTable(scn, site);
        // Light Side draws two battle destinies this battle; both are 3 (the second is the other Solomahal, destiny 3).
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(site, 1, scn.LS));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(6);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("general2"));
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        assertTrue(scn.LSDecisionAvailable("battle destiny"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.DSPass();
        scn.LSUseCardAction(scn.GetLSCard("general"), ADD);
        scn.LSDecided("1");
        // Second draw in the same turn: another 3, but the boost is not offered again.
        StringBuilder seen = new StringBuilder();
        boolean offeredAgain = false;
        for (int i = 0; i < 30 && scn.GetLSBattleDestinyCount() < 2; i++) {
            var ls = scn.LSGetDecision(); var ds = scn.DSGetDecision();
            seen.append(ls == null ? "-" : ls.getText()).append(" / ").append(ds == null ? "-" : ds.getText()).append(" | ");
            if (ls != null && scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains(ADD))) offeredAgain = true;
            if (scn.LSDecisionAvailable("battle destiny")) scn.LSChooseYes();
            else if (ds != null) scn.DSPass();
            else if (ls != null) scn.LSPass();
            else break;
        }
        var ls = scn.LSGetDecision();
        if (ls != null && scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains(ADD))) offeredAgain = true;
        assertEquals(seen.toString(), 2, scn.GetLSBattleDestinyCount());
        assertFalse(seen.toString(), offeredAgain);
        scn.PassAllResponses();
        assertEquals(seen.toString(), 7, scn.GetLSTotalDestiny()); // 3 + 1 + 3
    }


    /** Solomahal rearranges the given Flagship sites into the given left-to-right order. */
    private void rearrange(VirtualTableScenario scn, PhysicalCardImpl general, List<PhysicalCardImpl> order) {
        scn.LSUseCardAction(general, REARRANGE);
        for (int i = 0; i < order.size() - 1; i++) scn.LSChooseCard(order.get(i));
        if (scn.LSHasCardChoiceAvailable(order.get(order.size() - 1))) scn.LSChooseCard(order.get(order.size() - 1));
        for (int i = 1; i < order.size(); i++) {
            assertTrue(order.get(i - 1).getLocationZoneIndex() < order.get(i).getLocationZoneIndex());
        }
    }

    /** The Docking Bay ends up between two interior sites: Prison, Docking Bay, Bridge. */
    private List<PhysicalCardImpl> dockBetweenInteriors(VirtualTableScenario scn, PhysicalCardImpl general, PhysicalCardImpl dock) {
        var prison = scn.GetLSCard("prison"); var bridge = scn.GetDSCard("bridge");
        for (var site : List.of(bridge, dock, prison)) scn.MoveLocationToTable(site);
        scn.SkipToLSTurn(Phase.CONTROL);
        var order = List.of(prison, dock, bridge);
        rearrange(scn, general, order);
        return order;
    }

    private void deploySiteNextTo(VirtualTableScenario scn, String player, PhysicalCardImpl site, PhysicalCardImpl nextTo, String side) {
        boolean ls = player.equals(scn.LS);
        assertTrue(ls ? scn.LSDeployAvailable(site) : scn.DSDeployAvailable(site));
        if (ls) scn.LSDeployCard(site); else scn.DSDeployCard(site);
        if (ls ? scn.LSHasCardChoiceAvailable(nextTo) : scn.DSHasCardChoiceAvailable(nextTo)) {
            if (ls) scn.LSChooseCard(nextTo); else scn.DSChooseCard(nextTo);
        }
        if (ls ? scn.LSDecisionAvailable("On which side") : scn.DSDecisionAvailable("On which side")) {
            if (ls) scn.LSChooseOption(side); else scn.DSChooseOption(side);
        }
        scn.PassAllResponses();
    }

    @Test public void AfterRearrangeLightDockingBayStillConvertsDarkDockingBayInPlace() {
        // (14_48 may not be converted itself, so the Light copy converts the Dark one.)
        var scn = scenario(); var general = start(scn);
        var dsDock = scn.GetDSCard("dsdock"); var dock = scn.GetLSCard("dock");
        scn.MoveCardsToLSHand(dock);
        var order = dockBetweenInteriors(scn, general, dsDock);
        int dockIndex = dsDock.getLocationZoneIndex();
        scn.SkipToPhase(Phase.DEPLOY); scn.EnsureLSForcePile(4);
        deploySiteNextTo(scn, scn.LS, dock, dsDock, "Convert");
        assertEquals(Zone.LOCATIONS, dock.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dsDock.getZone());
        assertEquals(dockIndex, dock.getLocationZoneIndex());
        assertTrue(order.get(0).getLocationZoneIndex() < dock.getLocationZoneIndex());
        assertTrue(dock.getLocationZoneIndex() < order.get(2).getLocationZoneIndex());
        var layout = scn.gameState().getLocationsLayout();
        assertSame(layout.findGroupContaining(dock), layout.findGroupContaining(order.get(0)));
    }

    @Test public void AfterRearrangeNewFlagshipSiteDeploysIntoTheRearrangedRow() {
        var scn = scenario(); var general = start(scn);
        var hallway = scn.GetDSCard("hallway"); var dock = scn.GetLSCard("dock");
        var order = dockBetweenInteriors(scn, general, dock);
        scn.MoveCardsToDSHand(hallway);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(4);
        deploySiteNextTo(scn, scn.DS, hallway, dock, "Right");
        assertEquals(Zone.LOCATIONS, hallway.getZone());
        // Prison, Docking Bay, Hallway, Bridge: in the same row, right of the Docking Bay.
        assertEquals(dock.getLocationZoneIndex() + 1, hallway.getLocationZoneIndex());
        assertEquals(hallway.getLocationZoneIndex() + 1, order.get(2).getLocationZoneIndex());
        var layout = scn.gameState().getLocationsLayout();
        assertSame(layout.findGroupContaining(dock), layout.findGroupContaining(hallway));
        assertSame(layout.findGroupContaining(dock), layout.findGroupContaining(order.get(0)));
    }
}
