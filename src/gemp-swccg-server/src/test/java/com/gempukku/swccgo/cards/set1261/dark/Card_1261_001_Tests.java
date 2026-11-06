package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_001_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("hallway", "14_51"); put("obiwan", "1_21");
                    put("han", "1_11"); put("objective", "13_46"); put("enemyDroid", "3_14"); }},
                new HashMap<>() {{ put("droid", "1261_1"); put("oom", "14_83"); put("infantry", "14_80");
                    put("nonPresence", "2_99"); put("vader", "1_168"); put("ownHallway", "14_112");
                    put("exterior", "12_170"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ThreeBThreeTenPrintedMetadataMatchesLegacyRatherThanOriginalVirtualText() {
        var scn = scenario(); var b = scn.GetDSCard("droid").getBlueprint();
        assertEquals("3B3-10", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(2, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon); assertEquals(3, b.getArmor(), scn.epsilon);
        assertEquals(3, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.BATTLE); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.DROID); add(Icon.PRESENCE); add(Icon.THEED_PALACE);
            add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.INFANTRY_BATTLE_DROID); }});
    }

    private VirtualTableScenario protection() {
        var scn = scenario(); scn.StartGame(); var hallway = scn.GetLSCard("hallway"); scn.MoveLocationToTable(hallway);
        scn.MoveCardsToLocation(hallway, scn.GetDSCard("droid"), scn.GetDSCard("oom"),
                scn.GetDSCard("nonPresence"), scn.GetLSCard("enemyDroid"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("infantry"));
        return scn;
    }

    @Test
    public void ThreeBThreeTenImmunityRequiresOwnPresenceDroidHereAtInteriorNabooSite() {
        var scn = protection(); var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("droid"), Title.Well_Handle_This));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("oom"), Title.Well_Handle_This));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("nonPresence"), Title.Well_Handle_This));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetLSCard("enemyDroid"), Title.Well_Handle_This));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("infantry"), Title.Well_Handle_This));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("oom"), Title.Alter));
        var exterior = scn.GetDSCard("exterior"); scn.MoveLocationToTable(exterior);
        scn.MoveCardsToLocation(exterior, scn.GetDSCard("droid"), scn.GetDSCard("oom"));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("oom"), Title.Well_Handle_This));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("droid"), scn.GetDSCard("oom"));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("oom"), Title.Well_Handle_This));
    }

    @Test
    public void ThreeBThreeTenBlocksEveryPlacingPlayerDuringBattleEvenAtAnotherSite() {
        var scn = protection(); var q = scn.game().getModifiersQuerying(); var target = scn.GetDSCard("oom");
        assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), target, scn.DS));
        var battleSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(battleSite, scn.GetDSCard("vader"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(battleSite);
        for (String player : new String[]{scn.DS, scn.LS, null}) {
            assertTrue(q.mayNotBePlacedInUsedPile(scn.gameState(), target, player));
            assertTrue(q.mayNotBePlacedInUsedPile(scn.gameState(), scn.GetDSCard("droid"), player));
            assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), scn.GetDSCard("nonPresence"), player));
            assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), scn.GetDSCard("infantry"), player));
            assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), scn.GetLSCard("enemyDroid"), player));
        }
        scn.MoveCardsToDSHand(scn.GetDSCard("droid"));
        assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), target, scn.DS));
    }

    @Test
    public void ThreeBThreeTenOwnInteriorSiteGrantsImmunityButNotUsedPileProtection() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSCard("ownHallway"); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetDSCard("oom"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation()); var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("oom"), Title.Well_Handle_This));
        assertFalse(q.mayNotBePlacedInUsedPile(scn.gameState(), scn.GetDSCard("oom"), scn.LS));
    }

    @Test
    public void ThreeBThreeTenPreventsActualWellHandleThisTargetWhileAnUnprotectedDroidCanBeLost() {
        var scn = protection(); var objective = scn.GetLSCard("objective");
        scn.MoveCardsToLSSideOfTable(objective);
        scn.MoveCardsToLocation(scn.GetLSCard("hallway"), scn.GetLSCard("obiwan"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(objective, "Target character");
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("droid")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("oom")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("nonPresence")));
        scn.LSChooseCard(scn.GetDSCard("nonPresence")); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("nonPresence"));
    }

    private VirtualTableScenario battleWith(String companion) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetLSCard("han"));
        if (companion != null) scn.MoveCardsToLocation(site, scn.GetDSCard(companion));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    @Test
    public void ThreeBThreeTenDrawsOneBattleDestinyWithAnotherUniqueBattleDroid() {
        assertEquals(1, battleWith("oom").GetDSBattleDestinyCount());
    }

    @Test
    public void ThreeBThreeTenDoesNotCountItselfOrAnUnrestrictedBattleDroid() {
        assertEquals(0, battleWith(null).GetDSBattleDestinyCount());
        assertEquals(0, battleWith("infantry").GetDSBattleDestinyCount());
    }

    @Test
    public void ThreeBThreeTenDoesNotAddDestinyWhenAlreadyAbleToDraw() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetDSCard("oom"), scn.GetDSCard("vader"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(1, scn.GetDSBattleDestinyCount());
    }
}
