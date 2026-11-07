package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_018_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("blaster", "1_152"); }},
                new HashMap<>() {{ put("guards", "1261_18"); put("jabba", "6_109");
                    put("trooper", "1_194"); put("palace", "6_162"); }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ThokAndThugPrintedStatsAndNoMaintenanceAreCorrect() {
        var scn = scenario(); var card = scn.GetDSCard("guards").getBlueprint();
        assertEquals("Thok & Thug", card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(1, card.getDestiny(), scn.epsilon); assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon); assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon); assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.GUARD); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.WARRIOR); add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void ThokAndThugHeroicRequiresYourAlienLeaderWithThem() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var jabba = scn.GetDSCard("jabba");
        scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, guards, scn.GetDSCard("trooper"));
        assertFalse(scn.HasKeyword(guards, Keyword.HEROIC));
        scn.MoveCardsToLocation(site, jabba); assertTrue(scn.HasKeyword(guards, Keyword.HEROIC));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), jabba); assertFalse(scn.HasKeyword(guards, Keyword.HEROIC));
    }

    @Test
    public void ThokAndThugDeployDiscountOnlyAppliesAtPalaceSites() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var palace = scn.GetDSCard("palace");
        scn.StartGame(); scn.MoveLocationToTable(palace);
        var q = scn.game().getModifiersQuerying();
        assertEquals(3, q.getDeployCost(scn.gameState(), guards, guards, palace, false, null, false, 0, null, false), scn.epsilon);
        assertEquals(4, q.getDeployCost(scn.gameState(), guards, guards, scn.GetDSStartingLocation(), false, null, false, 0, null, false), scn.epsilon);
    }

    @Test
    public void ThokAndThugDefendingBonusCostsOneAndCannotRepeat() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, guards, scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.LSPass();
        int power = scn.GetDSTotalPower(); int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
        scn.DSUseCardAction(guards, "Add 2 to total power"); scn.PassAllResponses();
        assertEquals(power + 2, scn.GetDSTotalPower()); assertEquals(force - 1, scn.GetDSForcePileCount());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
    }

    @Test
    public void ThokAndThugCannotBoostAnAttackingBattle() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, guards, scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertFalse(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
    }

    @Test
    public void ThokAndThugCannotBoostWithoutForce() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, guards, scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.DrainDSForcePileTo(0);
        scn.LSPass();
        assertEquals(0, scn.GetDSForcePileCount());
        assertFalse(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
    }

    @Test
    public void ThokAndThugCannotBoostWhenNotParticipating() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), guards);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
    }

    @Test
    public void ThokAndThugActuallyDeployForThreeAtPalaceSite() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var palace = scn.GetDSCard("palace");
        scn.StartGame(); scn.MoveLocationToTable(palace); scn.MoveCardsToDSHand(guards);
        scn.SkipToDSTurn(Phase.DEPLOY); int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(guards); scn.DSChooseCard(palace); scn.PassAllResponses();
        assertEquals(palace, guards.getAtLocation());
        assertEquals(force - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void ThokAndThugBonusExpiresAfterBattleAndResetsNextTurn() {
        var scn = scenario(); var guards = scn.GetDSCard("guards"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, guards, scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.LSPass();
        scn.DSUseCardAction(guards, "Add 2 to total power"); scn.PassAllResponses();
        scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertFalse(scn.gameState().isDuringBattle());
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.LSPass();
        assertEquals(6, scn.GetDSTotalPower());
        assertTrue(scn.DSCardActionAvailable(guards, "Add 2 to total power"));
    }

    @Test
    public void ThokAndThugDoNotBecomeHeroicWithOpponentsAlienLeader() {
        var scn = scenario(); scn.StartGame(); var guards = scn.GetDSCard("guards");
        var enemyLeader = com.gempukku.swccgo.cards.legacy.MissionTestSupport.putInHand(scn, 9184, scn.LS,
                new com.gempukku.swccgo.cards.AbstractAlien(Side.LIGHT, 1, 2, 2, 2, 3, "Alien Leader", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {
                    { addKeywords(Keyword.LEADER); }
                });
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), guards, enemyLeader);
        assertFalse(scn.HasKeyword(guards, Keyword.HEROIC));
    }

    @Test
    public void ThokAndThugGrantedHeroicProtectsJabbaFromWeaponTargeting() {
        var scn = scenario(); scn.StartGame(); var guards = scn.GetDSCard("guards"); var jabba = scn.GetDSCard("jabba");
        var luke = scn.GetLSCard("luke"); var blaster = scn.GetLSCard("blaster"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, guards, jabba, luke); scn.AttachCardsTo(luke, blaster);
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(guards));
        assertTrue(scn.LSHasCardChoiceNotAvailable(jabba));
    }

}
