package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_059_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("speeder", "3_69"); }},
                new HashMap<>() {{ put("marquand", "1261_59"); put("otherWalker", "3_155"); put("cannon", "3_158"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void MarquandInBlizzardSixPrintedStatsAndIdentityMatchOriginalSheet() {
        var scn = scenario(); var b = scn.GetDSCard("marquand").getBlueprint();
        assertEquals("Marquand In Blizzard 6", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(6, b.getDeployCost(), scn.epsilon);
        assertEquals(6, b.getPower(), scn.epsilon); assertEquals(7, b.getArmor(), scn.epsilon);
        assertEquals(1, b.getLandspeed(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity()); assertEquals(0, b.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.getLore().contains("Death Squadron"));
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.VEHICLE); }});
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.AT_AT); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.VEHICLE); add(Icon.PILOT); add(Icon.SCOMP_LINK);
            add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.ENCLOSED); add(Keyword.DEATH_SQUADRON); }});
    }

    @Test
    public void MarquandInBlizzardSixPermanentPilotProvidesTwoAndAttritionThresholdIsFour() {
        var scn = scenario(); scn.StartGame(); var walker = scn.GetDSCard("marquand");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        assertEquals(2, scn.GetBattleDestinyAbility(walker));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
    }

    private VirtualTableScenario battle(boolean otherWalkerFires, boolean vehicleTarget) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        var firing = scn.GetDSCard(otherWalkerFires ? "otherWalker" : "marquand");
        scn.MoveCardsToLocation(site, scn.GetDSCard("marquand"), firing,
                scn.GetLSCard(vehicleTarget ? "speeder" : "obiwan"));
        scn.AttachCardsTo(firing, scn.GetDSCard("cannon"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.EnsureDSForcePile(5);
        return scn;
    }

    private void fire(VirtualTableScenario scn, PhysicalCardImpl target, int destiny) {
        scn.PrepareDSDestiny(destiny); scn.DSUseCardAction(scn.GetDSCard("cannon"), "Fire");
        if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
        scn.PassAllResponses();
    }

    @Test
    public void MarquandInBlizzardSixActualCannonHitReducesCharacterPowerForfeitAndDestinyAbility() {
        var scn = battle(false, false); var target = scn.GetLSCard("obiwan");
        assertEquals(5, scn.GetPower(target)); assertEquals(9, scn.GetForfeit(target));
        assertEquals(6, scn.GetBattleDestinyAbility(target));
        fire(scn, target, 7);
        assertTrue(target.isHit()); assertEquals(3, scn.GetPower(target)); assertEquals(7, scn.GetForfeit(target));
        assertEquals(0, scn.GetBattleDestinyAbility(target));
    }

    @Test
    public void MarquandInBlizzardSixMissDoesNotApplyAnyHitPenalty() {
        var scn = battle(false, false); var target = scn.GetLSCard("obiwan");
        assertEquals(6, scn.GetDefense(target)); fire(scn, target, 5);
        assertFalse(target.isHit()); assertEquals(5, scn.GetPower(target)); assertEquals(9, scn.GetForfeit(target));
        assertEquals(6, scn.GetBattleDestinyAbility(target));
    }

    @Test
    public void MarquandInBlizzardSixDoesNotPenalizeAnotherWalkersHit() {
        var scn = battle(true, false); var target = scn.GetLSCard("obiwan"); fire(scn, target, 7);
        assertTrue(target.isHit()); assertEquals(5, scn.GetPower(target)); assertEquals(9, scn.GetForfeit(target));
        assertEquals(6, scn.GetBattleDestinyAbility(target));
    }

    @Test
    public void MarquandInBlizzardSixVehicleHitAlsoPreventsPermanentPilotDestinyAbility() {
        var scn = battle(false, true); var target = scn.GetLSCard("speeder");
        assertEquals(3, scn.GetPower(target)); assertEquals(4, scn.GetForfeit(target));
        assertEquals(1, scn.GetBattleDestinyAbility(target)); fire(scn, target, 3);
        assertTrue(target.isHit()); assertEquals(1, scn.GetPower(target)); assertEquals(2, scn.GetForfeit(target));
        assertEquals(0, scn.GetBattleDestinyAbility(target));
    }
}
