package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
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
            put("dock", "14_48"); put("luke", "1_19"); put("palpatine", "1116_10");
        }}, new HashMap<>() {{
            put("bridge", "12_164"); put("hallway", "13_57"); put("trooper", "1_194");
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
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.CHARACTER, CardType.REPUBLIC)));
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

    @Test public void SolomahalCannotBoostAnActualThreeWithOriginalVirtualPalpatine() {
        var scn = scenario(); start(scn); var site = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSCard("dock"), scn.GetLSCard("palpatine"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(5); scn.SkipToPhase(Phase.BATTLE); scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(3); scn.SkipToPowerSegment();
        assertTrue(scn.LSDecisionAvailable("battle destiny"));
        scn.LSChooseYes(); scn.PassResponses("COST_TO_DRAW_DESTINY_CARD"); scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(ADD)));
        scn.PassDestinyDrawResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount()); assertEquals(3, scn.GetLSTotalDestiny());
    }
}
