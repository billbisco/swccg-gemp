package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_004_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("chewie", "2_3"); put("dune", "1_130"); }},
                new HashMap<>() {{ put("boba", "1261_4"); put("vader", "1_168"); put("trooper", "1_194");
                    put("alien", "1_172"); put("hunter", "4_100"); put("hoth", "3_143"); put("ship", "1_301"); put("camp", "1_292"); put("farm", "1_294"); put("mos", "1_295"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BobaFettBountyHunterPrintedStatsAndNoMaintenanceAreCorrect() {
        var scn = scenario(); var b = scn.GetDSCard("boba").getBlueprint();
        assertEquals("Boba Fett, Bounty Hunter", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(5, b.getArmor(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.BOUNTY_HUNTER); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void BobaFettBountyHunterAddsThreeOnlyWhilePiloting() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba"); var ship = scn.GetDSCard("ship");
        scn.MoveLocationToTable(scn.GetDSCard("hoth")); scn.MoveCardsToLocation(scn.GetDSCard("hoth"), ship);
        scn.BoardAsPilot(ship, b); assertEquals(12, scn.GetPower(ship));
        scn.BoardAsPassenger(ship, b); assertEquals(9, scn.GetPower(ship));
    }

    @Test
    public void BobaFettBountyHunterFlyingLandspeedIsThree() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), b); assertEquals(3, scn.GetLandspeed(b));
    }

    @Test
    public void BobaFettBountyHunterAloneDoesNotSatisfyHisOwnCompanionConditions() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, b, scn.GetLSCard("obiwan"));
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), b), scn.epsilon);
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(0, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void BobaFettBountyHunterAddsExactlyOneDestinyWithOwnImperial() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetDSCard("trooper"), scn.GetLSCard("obiwan"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void BobaFettBountyHunterAddsDestinyWithOwnAlienButNotOpponentsAlien() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetLSCard("chewie"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(0, scn.GetDSBattleDestinyCount());
        var own = scenario(); own.StartGame(); var ownSite = own.GetDSStartingLocation();
        own.MoveCardsToLocation(ownSite, own.GetDSCard("boba"), own.GetDSCard("alien"), own.GetLSCard("chewie"));
        own.DSStartBattleAndSkipToWeaponsSegment(ownSite); assertEquals(2, own.GetDSBattleDestinyCount());
        var remote = scenario(); remote.StartGame(); var remoteSite = remote.GetDSStartingLocation();
        remote.MoveCardsToLocation(remoteSite, remote.GetDSCard("boba"), remote.GetLSCard("chewie"));
        remote.MoveCardsToLocation(remote.GetLSStartingLocation(), remote.GetDSCard("alien"));
        remote.DSStartBattleAndSkipToWeaponsSegment(remoteSite); assertEquals(0, remote.GetDSBattleDestinyCount());
    }

    @Test
    public void BobaFettBountyHunterImmunityRisesWithVaderAndReturnsWhenVaderLeaves() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba"); var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), b, vader); var q = scn.game().getModifiersQuerying();
        assertEquals(5, q.getImmunityToAttritionLessThan(scn.gameState(), b), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader);
        assertEquals(3, q.getImmunityToAttritionLessThan(scn.gameState(), b), scn.epsilon);
    }

    @Test
    public void BobaFettBountyHunterBountyHunterCompanionDoesNotGrantOldCardsTwoExtraDestinies() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation(); var b = scn.GetDSCard("boba");
        scn.MoveCardsToLocation(site, b, scn.GetDSCard("hunter"), scn.GetLSCard("obiwan"));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), b), scn.epsilon);
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void BobaFettBountyHunterActuallyFliesThreeSitesButCannotFlyFour() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba");
        var start = scn.GetDSStartingLocation(); var camp = scn.GetDSCard("camp"); var farm = scn.GetDSCard("farm");
        var mos = scn.GetDSCard("mos"); var dune = scn.GetLSCard("dune");
        var previous = start;
        for (var next : new com.gempukku.swccgo.game.PhysicalCardImpl[] {camp, farm, mos, dune}) {
            scn.MoveLocationToTable(next, new com.gempukku.swccgo.game.layout.LocationPlacement(
                    "Tatooine", null, null, previous, LocationPlacementDirection.RIGHT_OF));
            previous = next;
        }
        scn.MoveCardsToLocation(start, b); scn.SkipToDSTurn(Phase.MOVE);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(b, "Move");
        assertTrue(scn.DSHasCardChoiceAvailable(mos)); assertFalse(scn.DSHasCardChoiceAvailable(dune));
        scn.DSChooseCard(mos); scn.PassAllResponses();
        assertEquals(mos, b.getAtLocation()); assertEquals(force - 1, scn.GetDSForcePileCount());
    }

}
