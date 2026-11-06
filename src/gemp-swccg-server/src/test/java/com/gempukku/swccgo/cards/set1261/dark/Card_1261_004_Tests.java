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
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("chewie", "2_3"); }},
                new HashMap<>() {{ put("boba", "1261_4"); put("vader", "1_168"); put("trooper", "1_194");
                    put("alien", "1_172"); put("hunter", "4_100"); put("hoth", "3_143"); put("ship", "1_301"); put("camp", "1_292"); put("farm", "1_294"); put("canyon", "7_293"); put("pass", "7_294"); }},
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
        var canyon = scn.GetDSCard("canyon"); var far = scn.GetDSCard("pass");
        var previous = start;
        for (var next : new com.gempukku.swccgo.game.PhysicalCardImpl[] {camp, farm, canyon, far}) {
            scn.MoveLocationToTable(next, new com.gempukku.swccgo.game.layout.LocationPlacement(
                    "Tatooine", null, null, previous, LocationPlacementDirection.RIGHT_OF));
            previous = next;
        }
        assertEquals(Integer.valueOf(3), scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(), start, canyon));
        assertEquals(Integer.valueOf(4), scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(), start, far));
        scn.MoveCardsToLocation(start, b); scn.SkipToDSTurn(Phase.MOVE);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(b, "Move");
        assertTrue(isSelectable(scn, canyon)); assertFalse(isSelectable(scn, far));
        scn.DSChooseCard(canyon); scn.PassAllResponses();
        assertEquals(canyon, b.getAtLocation()); assertEquals(force - 1, scn.GetDSForcePileCount());
    }


    private void assertBobaAttritionBoundary(int destiny, boolean enemyHunter, boolean immune) {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("boba"); var site = scn.GetDSStartingLocation();
        if (enemyHunter) {
            var hunter = com.gempukku.swccgo.cards.legacy.MissionTestSupport.putInHand(scn, 9185, scn.LS,
                    new com.gempukku.swccgo.cards.AbstractAlien(Side.LIGHT, 1, 3, 3, 4, 5, "Opposing Bounty Hunter", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {
                        { addKeywords(Keyword.BOUNTY_HUNTER); }
                    });
            scn.MoveCardsToLocation(site, b, hunter);
        } else {
            scn.MoveCardsToLocation(site, b, scn.GetLSCard("obiwan"));
        }
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.PrepareLSDestiny(destiny);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        if (immune) {
            scn.DSPass(); scn.PassAllResponses(); assertEquals(site, b.getAtLocation());
        } else {
            assertTrue(scn.AwaitingDSAttritionPayment()); assertTrue(scn.DSHasCardChoiceAvailable(b));
            scn.DSPayAttritionFromCardInPlay(b); scn.PassAllResponses();
            com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, b);
        }
    }

    @Test
    public void BobaFettBountyHunterSurvivesTwoAttritionButCanBeForfeitedAtThree() {
        assertBobaAttritionBoundary(2, false, true);
        assertBobaAttritionBoundary(3, false, false);
    }

    @Test
    public void BobaFettBountyHunterOpponentsBountyHunterGrantsImmunityBelowFiveButNotFive() {
        assertBobaAttritionBoundary(4, true, true);
        assertBobaAttritionBoundary(5, true, false);
    }


    private boolean isSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId");
        var ids = scn.GetADParam(scn.DS, "cardId");
        var allowed = scn.GetADParam(scn.DS, "selectable");
        int length = blueprints != null ? blueprints.length : ids.length;
        for (int i = 0; i < length; i++) {
            if (blueprints != null ? blueprints[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId())))
                return allowed == null || Boolean.parseBoolean(allowed[i]);
        }
        return false;
    }

}
