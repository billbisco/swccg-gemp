package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1240_076_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wes", "1240_76"); put("jek", "1_13"); put("pilot", "2_23");
            put("red", "1_145"); put("corvette", "1_140"); put("other", "1_140");
            put("rogue", "3_66"); put("quad", "1_159"); put("laser", "3_74");
            put("site", "1_133"); put("elsewhere", "5_79");
        }}, new HashMap<>() {{
            put("dread", "106_13"); put("pilot", "1_164"); put("walker", "7_313");
        }}, 50, 50, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WesJansonPrintedStatsIconsAndGunnerCharacteristicAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("wes").getBlueprint();
        assertEquals("Wes Janson", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertNull(bp.getArmor());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Served for many years as a gunner with friend Jek Porkins in the Tierfon Yellow Aces. Expert marksmanship helped the Alliance earn some of its earliest victories.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REBEL, Icon.PILOT, Icon.WARRIOR, Icon.HOTH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.GUNNER)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<ModelType>());
    }

    private VirtualTableScenario PilotReady() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("red"), scn.GetLSCard("corvette"));
        return scn;
    }

    @Test
    public void WesJansonAddsTwoPowerAndOneNavComputerIconWhilePilotingRedThree() {
        var scn = PilotReady();
        scn.BoardAsPilot(scn.GetLSCard("red"), scn.GetLSCard("wes"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("red")));
        assertEquals(2, scn.GetIconCount(scn.GetLSCard("red"), Icon.NAV_COMPUTER));
        // Red 3 is not printed as Wes's matching starship.
        assertFalse(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), scn.GetLSCard("wes"), scn.GetLSCard("red")));
        assertFalse(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), scn.GetLSCard("wes"), scn.GetLSCard("corvette")));
        scn.MoveCardsToLSHand(scn.GetLSCard("wes"));
        scn.BoardAsPilot(scn.GetLSCard("red"), scn.GetLSCard("pilot"));
        assertEquals(1, scn.GetIconCount(scn.GetLSCard("red"), Icon.NAV_COMPUTER));
    }

    @Test
    public void WesJansonAddsPowerButNoNavComputerToAnotherStarshipHePilots() {
        var scn = PilotReady();
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("wes"));
        assertEquals(7, scn.GetPower(scn.GetLSCard("corvette")));
        assertEquals(1, scn.GetIconCount(scn.GetLSCard("corvette"), Icon.NAV_COMPUTER));
    }

    @Test
    public void WesJansonAddsTwoPowerToAVehicleAsPilotButNotPassenger() {
        var scn = PilotReady();
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("rogue"));
        scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("wes"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("rogue")));
        scn.BoardAsPassenger(scn.GetLSCard("rogue"), scn.GetLSCard("wes"));
        scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("jek"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("rogue")));
        scn.MoveCardsToLSHand(scn.GetLSCard("wes"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("rogue")));
    }

    private VirtualTableScenario DestinyReady(boolean wesPiloting, boolean normallyAble) {
        var scn = PilotReady();
        var system = scn.GetLSStartingLocation();
        scn.BoardAsPilot(scn.GetLSCard("red"), scn.GetLSCard(wesPiloting ? "wes" : "pilot"));
        if (normallyAble) scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("jek"));
        else scn.MoveCardsToLSHand(scn.GetLSCard("corvette"));
        scn.MoveCardsToLocation(system, scn.GetDSCard("dread"));
        scn.BoardAsPilot(scn.GetDSCard("dread"), scn.GetDSCard("pilot"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(3), scn.GetLSDestiny(2));
        scn.PrepareDSDestiny(0);
        return scn;
    }

    @Test
    public void WesJansonDrawsOneActualBattleDestinyWhenRedThreeCannotOtherwiseDraw() {
        var scn = DestinyReady(true, false);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        assertEquals(3, scn.GetTopOfLSReserveDeck().getBlueprint().getDestiny(), scn.epsilon);
    }

    @Test
    public void WesJansonDoesNotAddAnotherBattleDestinyWhenAbilityAlreadyAllowsOne() {
        var scn = DestinyReady(true, true);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        assertEquals(3, scn.GetTopOfLSReserveDeck().getBlueprint().getDestiny(), scn.epsilon);
    }

    @Test
    public void WesJansonDoesNotGrantRedThreeBattleDestinyWhenAnotherPilotIsAboard() {
        var scn = DestinyReady(false, false);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(0, scn.GetLSTotalDestiny());
        assertEquals(2, scn.GetTopOfLSReserveDeck().getBlueprint().getDestiny(), scn.epsilon);
    }

    @Test
    public void WesJansonDoesNotGrantBattleDestinyWhileRedThreeIsAtAnotherSystem() {
        var scn = PilotReady();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("red"));
        scn.BoardAsPilot(scn.GetLSCard("red"), scn.GetLSCard("wes"));
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("jek"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("dread"));
        scn.BoardAsPilot(scn.GetDSCard("dread"), scn.GetDSCard("pilot"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(0, scn.GetLSTotalDestiny());
        assertEquals(2, scn.GetTopOfLSReserveDeck().getBlueprint().getDestiny(), scn.epsilon);
    }

    private VirtualTableScenario WeaponReady(boolean vehicle, boolean jekHere, boolean otherCarrier) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        var location = scn.GetLSStartingLocation();
        if (vehicle) {
            scn.MoveLocationToTable(scn.GetLSCard("site"));
            location = scn.GetLSCard("site");
            scn.MoveCardsToLocation(location, scn.GetLSCard("rogue"), scn.GetDSCard("walker"));
            scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("wes"));
            scn.AttachCardsTo(scn.GetLSCard("rogue"), scn.GetLSCard("laser"));
            if (jekHere) scn.BoardAsPassenger(scn.GetLSCard("rogue"), scn.GetLSCard("jek"));
        }
        else {
            scn.MoveCardsToLocation(location, scn.GetLSCard("corvette"), scn.GetDSCard("dread"));
            scn.BoardAsPassenger(scn.GetLSCard("corvette"), scn.GetLSCard("wes"));
            if (jekHere) scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("jek"));
            var firing = scn.GetLSCard("corvette");
            if (otherCarrier) {
                firing = scn.GetLSCard("other");
                scn.MoveCardsToLocation(location, firing);
            }
            scn.AttachCardsTo(firing, scn.GetLSCard("quad"));
        }
        if (!jekHere) {
            scn.MoveLocationToTable(scn.GetLSCard("elsewhere"));
            scn.MoveCardsToLocation(scn.GetLSCard("elsewhere"), scn.GetLSCard("jek"));
        }
        scn.EnsureLSForcePile(10);
        scn.EnsureDSForcePile(5);
        scn.LSStartBattleAndSkipToWeaponsSegment(location);
        return scn;
    }

    private void Fire(VirtualTableScenario scn, boolean vehicle, int destiny, boolean expectLoss) {
        var weapon = scn.GetLSCard(vehicle ? "laser" : "quad");
        var target = scn.GetDSCard(vehicle ? "walker" : "dread");
        int reserve = scn.GetDSReserveDeckCount();
        int lost = scn.GetDSLostPileCount();
        int darkForce = scn.GetDSForcePileCount();
        int lightForce = scn.GetLSForcePileCount();
        scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(weapon, "Fire");
        scn.LSChooseCard(target);
        scn.PassAllResponses();
        if (scn.AwaitingDSForceLossPayment()) {
            assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetTopOfDSForcePile()));
            scn.DSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertEquals(lightForce - 1, scn.GetLSForcePileCount());
        assertEquals(darkForce, scn.GetDSForcePileCount());
        assertEquals(reserve - (expectLoss ? 1 : 0), scn.GetDSReserveDeckCount());
        assertEquals(lost + (expectLoss ? 1 : 0), scn.GetDSLostPileCount());
    }

    @Test
    public void WesJansonPassengerAddsOneToStarshipWeaponDestinyAndHitWithJekLosesReserveForce() {
        var scn = WeaponReady(false, true, false);
        assertEquals(4, scn.GetArmor(scn.GetDSCard("dread")));
        Fire(scn, false, 4, true);
        assertTrue(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void WesJansonVehiclePilotAddsOneToWeaponDestinyAndHitWithJekLosesReserveForce() {
        var scn = WeaponReady(true, true, false);
        assertEquals(7, scn.GetArmor(scn.GetDSCard("walker")));
        Fire(scn, true, 5, true);
        assertTrue(scn.GetDSCard("walker").isHit());
    }

    @Test
    public void WesJansonStillImprovesWeaponDestinyButDoesNotLoseForceWhenJekIsElsewhere() {
        var scn = WeaponReady(false, false, false);
        Fire(scn, false, 4, false);
        assertTrue(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void WesJansonDoesNotLoseForceWhenTheWeaponMissesEvenWithJekAboard() {
        var scn = WeaponReady(false, true, false);
        Fire(scn, false, 3, false);
        assertFalse(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void WesJansonDoesNotImproveWeaponDestinyForAnotherCarrierAtTheSameSystem() {
        var scn = WeaponReady(false, true, true);
        Fire(scn, false, 4, false);
        assertFalse(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void WesJansonDoesNotLoseForceForAnotherCarriersHitEvenWithJekAtTheSystem() {
        var scn = WeaponReady(false, true, true);
        Fire(scn, false, 5, false);
        assertTrue(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void WesJansonDeploysForTwoForceAndCannotDeployWithOnlyOne() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveCardsToLSHand(scn.GetLSCard("wes"));
        scn.EnsureLSForcePile(2);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("wes"));
        scn.LSChooseCard(scn.GetLSCard("site"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("site"), scn.GetLSCard("wes"));
        assertEquals(force - 2, scn.GetLSForcePileCount());
        var poor = GetScenario();
        poor.StartGame();
        poor.SkipToLSTurn(Phase.CONTROL);
        poor.MoveLocationToTable(poor.GetLSCard("site"));
        poor.MoveCardsToLSHand(poor.GetLSCard("wes"));
        poor.EnsureLSForcePile(1);
        while (poor.GetLSForcePileCount() > 1) poor.MoveCardsToTopOfLSUsedPile(poor.GetTopOfLSForcePile());
        poor.SkipToPhase(Phase.DEPLOY);
        assertTrue(poor.AwaitingLSDeployPhaseActions());
        assertFalse(poor.LSDeployAvailable(poor.GetLSCard("wes")));
    }
}
