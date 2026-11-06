package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1263_020_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("walker", "1263_20"); put("driver", "3_83"); put("other", "1_180"); for (int i=1; i<=9; i++) put("passenger"+i, "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CycloneWalkerStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("walker").getBlueprint();
        assertEquals("Cyclone Walker", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(5, bp.getDeployCost(), scn.epsilon);
        assertEquals(5, bp.getPower(), scn.epsilon);
        assertEquals(7, bp.getArmor(), scn.epsilon);
        assertEquals(1, bp.getLandspeed(), scn.epsilon);
        assertEquals(6, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(1, bp.getPilotCapacity());
        assertEquals(8, bp.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.VEHICLE); }});
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>() {{ add(ModelType.AT_AT); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.ENCLOSED); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.VEHICLE); add(Icon.PILOT); add(Icon.SCOMP_LINK); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void CycloneWalkerPermanentPilotPermitsBattleWithoutAnAddedPilot() {
        var scn = scenario(); var walker = scn.GetDSCard("walker"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, walker, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(1, scn.GetBattleDestinyAbility(walker));
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site); scn.SkipToEndOfPowerSegment(false);
        assertEquals(5, scn.GetDSTotalPower(), scn.epsilon);
    }

    @Test
    public void CycloneWalkerDriverPilotingGainsForfeitAndAddsVehiclePower() {
        var scn = scenario(); var walker = scn.GetDSCard("walker"); var driver = scn.GetDSCard("driver");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        scn.BoardAsPilot(walker, driver); scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(9, scn.GetPower(walker));
        assertEquals(4, scn.GetForfeit(driver));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
        scn.MoveCardsToDSHand(driver);
        assertEquals(5, scn.GetPower(walker));
        assertEquals(2, scn.GetForfeit(driver));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
        scn.BoardAsPilot(walker, driver);
        assertEquals(9, scn.GetPower(walker));
        assertEquals(4, scn.GetForfeit(driver));
    }

    @Test
    public void CycloneWalkerDriverPassengerDoesNotGrantPilotingBenefits() {
        var scn = scenario(); var walker = scn.GetDSCard("walker"); var driver = scn.GetDSCard("driver");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        scn.BoardAsPassenger(walker, driver); scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(5, scn.GetPower(walker)); assertEquals(2, scn.GetForfeit(driver));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
    }

    @Test
    public void CycloneWalkerOtherPilotDoesNotGrantDriverBenefits() {
        var scn = scenario(); var walker = scn.GetDSCard("walker"); var pilot = scn.GetDSCard("other");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        scn.BoardAsPilot(walker, pilot); scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(7, scn.GetPower(walker));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
    }

    private void attrition(int destiny, int remaining) {
        var scn = scenario(); var walker = scn.GetDSCard("walker"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, walker, scn.GetLSCard("luke"));
        scn.BoardAsPilot(walker, scn.GetDSCard("driver"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.PrepareLSDestiny(destiny);
        scn.DSInitiateBattle(site); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(destiny, scn.GetUnpaidDSAttrition());
        if (remaining == 0) {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
            scn.DSPass();
            assertEquals(Zone.AT_LOCATION, walker.getZone());
            assertEquals(walker, scn.GetDSCard("driver").getAttachedTo());
        }
        else {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit"));
            assertFalse(scn.DSDecisionAvailable("(if desired)"));
            scn.DSChooseCard(scn.GetDSCard("driver"));
            scn.PassAllResponses();
            com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("driver"));
        }
    }
    @Test public void CycloneWalkerDriverProtectsFromAttritionBelowFive() { attrition(4, 0); }
    @Test public void CycloneWalkerDriverDoesNotProtectFromAttritionEqualToFive() { attrition(5, 5); }
    @Test public void CycloneWalkerDriverDoesNotProtectFromAttritionAboveFive() { attrition(6, 6); }

    private void passengers(VirtualTableScenario scn, int aboard) {
        scn.StartGame(); var walker = scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        for (int i=1; i<=aboard; i++) scn.BoardAsPassenger(walker, scn.GetDSCard("passenger"+i));
        scn.MoveCardsToDSHand(scn.GetDSCard("passenger9"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("passenger9"));
    }
    @Test
    public void CycloneWalkerAllowsDeploymentOfEighthPassenger() {
        var scn = scenario(); passengers(scn, 7);
        assertTrue(scn.DSHasCardChoicesAvailable(scn.GetDSCard("walker")));
        scn.DSChooseCard(scn.GetDSCard("walker")); scn.PassAllResponses();
        assertEquals(scn.GetDSCard("walker"), scn.GetDSCard("passenger9").getAttachedTo());
    }
    @Test
    public void CycloneWalkerDisallowsDeploymentOfNinthPassenger() {
        var scn = scenario(); passengers(scn, 8);
        assertTrue(scn.DSHasCardChoicesNotAvailable(scn.GetDSCard("walker")));
        assertTrue(scn.DSHasCardChoicesAvailable(scn.GetDSStartingLocation()));
    }

    private void fullPassengerHold(VirtualTableScenario scn) {
        scn.StartGame(); var walker = scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        for (int i=1; i<=8; i++) scn.BoardAsPassenger(walker, scn.GetDSCard("passenger"+i));
    }
    @Test
    public void CycloneWalkerAllowsOneAddedPilotWithEightPassengers() {
        var scn = scenario(); fullPassengerHold(scn);
        var driver = scn.GetDSCard("driver"); var walker = scn.GetDSCard("walker");
        scn.MoveCardsToDSHand(driver); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(driver); assertTrue(scn.DSHasCardChoicesAvailable(walker));
        scn.DSChooseCard(walker); scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(walker, driver));
    }
    @Test
    public void CycloneWalkerDisallowsSecondAddedPilotWhenPassengerHoldIsFull() {
        var scn = scenario(); fullPassengerHold(scn); var walker = scn.GetDSCard("walker");
        scn.BoardAsPilot(walker, scn.GetDSCard("driver"));
        scn.MoveCardsToDSHand(scn.GetDSCard("other")); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("other"));
        assertTrue(scn.DSHasCardChoicesNotAvailable(walker));
        assertTrue(scn.DSHasCardChoicesAvailable(scn.GetDSStartingLocation()));
    }
}
