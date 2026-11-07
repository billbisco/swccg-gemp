package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_006_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11");
                    put("quigon", "11_10"); put("worrt", "6_48"); }},
                new HashMap<>() {{ put("maul", "1261_6"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DarthMaulWithLightsaberPrintedStatsAndPermanentWeaponMatchPublishedSheet() {
        var scn = scenario(); var maul = scn.GetDSCard("maul"); var b = maul.getBlueprint();
        assertEquals("Darth Maul With Lightsaber", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(7, b.getDeployCost(), scn.epsilon);
        assertEquals(7, b.getPower(), scn.epsilon); assertEquals(6, b.getAbility(), scn.epsilon);
        assertEquals(7, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.SITH); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.SITH); add(Icon.PILOT); add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON); add(Icon.THEED_PALACE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_6); }});
        var weapon = b.getPermanentWeapon(maul);
        assertEquals("Maul's Lightsaber", weapon.getTitle(scn.game())); assertTrue(weapon.hasKeyword(Keyword.LIGHTSABER));
    }

    private VirtualTableScenario battle() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("maul"), scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    private int fire(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl target, int first, int second) {
        scn.PrepareDSDestiny(first); scn.PrepareDSDestiny(second);
        int reserveAfterPreparation = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("maul"), "Fire");
        if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
        scn.PassAllResponses();
        return reserveAfterPreparation;
    }

    @Test
    public void DarthMaulWithLightsaberHitsCharacterForFreeDrawsTwoAndZeroesForfeit() {
        var scn = battle(); var target = scn.GetLSCard("obiwan"); var maul = scn.GetDSCard("maul");
        int force = scn.GetDSForcePileCount();
        int reserve = fire(scn, target, 3, 4);
        assertTrue(target.isHit()); assertEquals(0, scn.GetForfeit(target)); assertEquals(force, scn.GetDSForcePileCount());
        assertEquals(reserve - 2, scn.GetDSReserveDeckCount());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(maul, "Fire"));
    }

    @Test
    public void DarthMaulWithLightsaberTotalEqualToDefenseDoesNotHitOrReduceForfeit() {
        var scn = battle(); var target = scn.GetLSCard("obiwan"); int forfeit = scn.GetForfeit(target);
        assertEquals(6, scn.GetDefense(target)); fire(scn, target, 2, 4);
        assertFalse(target.isHit()); assertEquals(forfeit, scn.GetForfeit(target));
    }

    @Test
    public void DarthMaulWithLightsaberCanActuallyFireAtCreatureDuringAnAttack() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        var maul = scn.GetDSCard("maul"); var worrt = scn.GetLSCard("worrt");
        scn.MoveCardsToLocation(site, maul, worrt); scn.DSActivateMaxForceAndPass(); scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate attack on creature"); scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringAttack()); fire(scn, worrt, 1, 4); assertTrue(worrt.isHit());
    }

    @Test
    public void DarthMaulWithLightsaberAddsOneDestinyWithOpponentsQuiGon() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("maul"), scn.GetLSCard("quigon"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DarthMaulWithLightsaberDoesNotAddDestinyWhenQuiGonIsElsewhere() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("maul"), scn.GetLSCard("obiwan"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("quigon"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DarthMaulWithLightsaberIsImmuneOnlyToPrintedEjectTitleAndHasNoAttritionImmunity() {
        var scn = scenario(); scn.StartGame(); var maul = scn.GetDSCard("maul");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), maul); var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), maul, Title.Eject_Eject));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), maul, Title.Alter));
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), maul), scn.epsilon);
    }
}
