package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNonuniqueVehicleSite;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); put("chewie", "2_3"); }},
                new HashMap<>() {{ put("driver", "1261_3"); put("walker", "3_157"); put("otherWalker", "3_157");
                    put("scout", "3_156"); put("otherPilot", "3_83"); put("vader", "1_168");
                    put("tie", "1_306"); put("chokeVader", "101_5"); put("hoth", "3_143"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AtAtDriverPrintedStatsAndIconsMatchLegacySheet() {
        var scn = scenario(); var b = scn.GetDSCard("driver").getBlueprint();
        assertEquals("AT-AT Driver", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(1, b.getDeployCost(), scn.epsilon);
        assertEquals(0, b.getPower(), scn.epsilon); assertEquals(2, b.getAbility(), scn.epsilon);
        assertEquals(2, b.getForfeit(), scn.epsilon); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.PILOT); add(Icon.HOTH); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void AtAtDriverAddsExactlyTwoToCombatVehiclesButNotAsPassengerOrToStarships() {
        var scn = scenario(); scn.StartGame(); var driver = scn.GetDSCard("driver");
        var walker = scn.GetDSCard("walker"); var scout = scn.GetDSCard("scout");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker, scout);
        scn.BoardAsPilot(walker, driver); assertEquals(8, scn.GetPower(walker));
        scn.MoveCardsToDSHand(driver); scn.BoardAsPilot(scout, driver); assertEquals(5, scn.GetPower(scout));
        scn.MoveCardsToDSHand(driver); scn.BoardAsPassenger(scout, driver); assertEquals(3, scn.GetPower(scout));
        scn.MoveLocationToTable(scn.GetDSCard("hoth")); var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(scn.GetDSCard("hoth"), tie); scn.MoveCardsToDSHand(driver); scn.BoardAsPilot(tie, driver);
        assertEquals(2, scn.GetPower(tie));
    }

    private VirtualTableScenario battle(boolean passenger, boolean nonAtAt, boolean naturalAbility) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        var host = scn.GetDSCard(nonAtAt ? "scout" : "walker");
        scn.MoveCardsToLocation(site, host, scn.GetLSCard("obiwan"));
        if (passenger) { scn.BoardAsPilot(host, scn.GetDSCard("otherPilot")); scn.BoardAsPassenger(host, scn.GetDSCard("driver")); }
        else scn.BoardAsPilot(host, scn.GetDSCard("driver"));
        if (naturalAbility) scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    @Test
    public void AtAtDriverDrawsOneDestinyWhilePilotingAtAtBelowFourAbility() {
        assertEquals(1, battle(false, false, false).GetDSBattleDestinyCount());
    }

    @Test
    public void AtAtDriverDoesNotGrantDestinyAsPassengerOrNonAtAtPilot() {
        assertEquals(0, battle(true, false, false).GetDSBattleDestinyCount());
        assertEquals(0, battle(false, true, false).GetDSBattleDestinyCount());
    }

    @Test
    public void AtAtDriverDoesNotAddDestinyWhenAlreadyAbleToDraw() {
        assertEquals(1, battle(false, false, true).GetDSBattleDestinyCount());
    }

    @Test
    public void AtAtDriverDeploysVehicleSiteOnlyOnHisWalkerForOneForceAndCannotRepeat() {
        var scn = scenario(); scn.StartGame(); var driver = scn.GetDSCard("driver"); var walker = scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker, scn.GetDSCard("otherWalker")); scn.BoardAsPilot(walker, driver);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(4);
        // No concrete vehicle-site blueprint is loaded at this base; isolate the existing deployment path.
        var site = MissionTestSupport.putInReserve(scn, 126103, scn.DS,
                new AbstractNonuniqueVehicleSite(Side.DARK, "AT-AT: Test Compartment", Filters.AT_AT,
                        Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C1) {
                    { addIcons(Icon.INTERIOR_SITE, Icon.MOBILE); addIcon(Icon.DARK_FORCE, 1); }
                });
        int force = scn.GetDSForcePileCount();
        // A shuffle ends revealed-state for a card that remains in this pile, independent of random order.
        var revealed = scn.GetDSReserveDeck().stream().filter(card -> card != site).findFirst().orElseThrow();
        scn.gameState().markCardsRevealedFromPile(java.util.Collections.singletonList(revealed));
        assertTrue(scn.gameState().isCardRevealedFromPile(revealed));
        scn.DSUseCardAction(driver, "Deploy vehicle site from Reserve Deck");
        scn.PassAllResponses(); scn.DSChooseCard(site); scn.PassAllResponses();
        assertEquals(walker, site.getRelatedStarshipOrVehicle());
        assertTrue(scn.GetDSReserveDeck().contains(revealed));
        assertFalse("Completed location search must end revealed-state by reshuffling", scn.gameState().isCardRevealedFromPile(revealed));
        assertEquals(Zone.LOCATIONS, site.getZone()); assertEquals(force - 1, scn.GetDSForcePileCount());
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(driver, "Deploy vehicle site from Reserve Deck"));
    }

    @Test
    public void AtAtDriverCannotDownloadAsPassengerOrWithoutForce() {
        var scn = scenario(); scn.StartGame(); var driver = scn.GetDSCard("driver"); var walker = scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker); scn.BoardAsPilot(walker, scn.GetDSCard("otherPilot"));
        scn.BoardAsPassenger(walker, driver); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(driver, "Deploy vehicle site from Reserve Deck"));
        scn.MoveCardsToDSHand(driver); scn.BoardAsPilot(walker, driver); scn.DrainDSForcePileTo(0);
        scn.SkipToPhase(Phase.MOVE); assertEquals(0, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertFalse(scn.DSCardActionAvailable(driver, "Deploy vehicle site from Reserve Deck"));
    }

    private VirtualTableScenario forfeitDriver(boolean passenger, boolean nonAtAt, int force) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation(); var host = scn.GetDSCard(nonAtAt ? "scout" : "walker");
        scn.MoveCardsToLocation(site, host, scn.GetLSCard("obiwan"), scn.GetLSCard("han"), scn.GetLSCard("chewie"));
        if (passenger) { scn.BoardAsPilot(host, scn.GetDSCard("otherPilot")); scn.BoardAsPassenger(host, scn.GetDSCard("driver")); }
        else scn.BoardAsPilot(host, scn.GetDSCard("driver"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(force); scn.DrainDSForcePileTo(force); scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions(); scn.SkipBattleDestinyDraws(false); scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingDSBattleDamagePayment()); scn.DSChooseCard(scn.GetDSCard("driver")); scn.PassCardLeavingTable();
        return scn;
    }

    @Test
    public void AtAtDriverJustForfeitedFromPilotSlotPaysOneForceToReachUsedPile() {
        var scn = forfeitDriver(false, false, 3); int force = scn.GetDSForcePileCount();
        scn.DSChooseAction("Place in Used Pile"); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, scn.GetDSCard("driver"));
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void AtAtDriverLostAsPassengerOrNonAtAtPilotCannotReturnToUsedPile() {
        for (boolean passenger : new boolean[]{true, false}) {
            var scn = forfeitDriver(passenger, !passenger, 3);
            assertTrue(scn.AwaitingDSBattleDamagePayment());
            assertFalse(scn.GetDSAvailableActions().stream().anyMatch(t -> t.contains("Place in Used Pile")));
            com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("driver"));
        }
    }

    @Test
    public void AtAtDriverCannotReturnFromLostPileWithoutOneForce() {
        var scn = forfeitDriver(false, false, 1); assertEquals(0, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingDSBattleDamagePayment());
            assertFalse(scn.GetDSAvailableActions().stream().anyMatch(t -> t.contains("Place in Used Pile")));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("driver"));
    }
    @Test
    public void AtAtDriverOrdinaryLossFromPilotingAtAtCanAlsoReturnToUsedPile() {
        var scn = scenario(); scn.StartGame(); var walker = scn.GetDSCard("walker"); var driver = scn.GetDSCard("driver");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, walker, scn.GetLSCard("obiwan"), scn.GetLSCard("han"), scn.GetLSCard("chewie"));
        scn.BoardAsPilot(walker, driver); scn.BoardAsPassenger(walker, scn.GetDSCard("chokeVader"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.EnsureDSForcePile(3); scn.PrepareDSDestiny(5);
        scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions(); scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(driver)); scn.DSChooseCard(driver); scn.PassCardLeavingTable(); int force = scn.GetDSForcePileCount();
        scn.DSChooseAction("Place in Used Pile"); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, driver);
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

}
