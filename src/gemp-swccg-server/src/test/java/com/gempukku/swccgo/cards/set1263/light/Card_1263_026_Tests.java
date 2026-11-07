package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_026_Tests {
    private VirtualTableScenario fixture() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("phylo", "1263_26", "local", "1_28", "remote", "1_30",
                        "ship", "1_146", "system", "3_55")),
                new HashMap<>(Map.of("vader", "1_168", "saber", "1_324", "beaten", "5_163", "otherDark", "1_173")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void printedIdentityAndStats() {
        var scn = fixture(); var bp = scn.GetLSCard("phylo").getBlueprint();
        assertEquals("Phylo Gandish", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(1, bp.getPower(), scn.epsilon); assertEquals(4, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(Rarity.V, bp.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.PILOT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE)));
    }

    @Test public void addsTwoPowerOnlyToTheShipShePilots() {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ship"); var phylo = scn.GetLSCard("phylo");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        int without = scn.GetPower(ship);
        scn.BoardAsPilot(ship, phylo);
        assertEquals(without + 2, scn.GetPower(ship));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), phylo);
        assertEquals(without, scn.GetPower(ship));
    }

    @Test public void attritionThresholdAppliesToPhyloAndOwnCharactersHereAndFollowsHer() {
        var scn = fixture(); scn.StartGame();
        var here = scn.GetLSStartingLocation(); var away = scn.GetDSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local"); var remote = scn.GetLSCard("remote");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(here, phylo, local, vader); scn.MoveCardsToLocation(away, remote);
        var mods = scn.game().getModifiersQuerying(); var state = scn.gameState();
        var opposingBaseline = mods.getImmunityToAttritionLessThan(state, vader);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, phylo), scn.epsilon);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, local), scn.epsilon);
        assertEquals(0, mods.getImmunityToAttritionLessThan(state, remote), scn.epsilon);
        scn.MoveCardsToLocation(away, phylo);
        assertEquals(0, mods.getImmunityToAttritionLessThan(state, local), scn.epsilon);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, remote), scn.epsilon);
        assertEquals(opposingBaseline, mods.getImmunityToAttritionLessThan(state, vader), scn.epsilon);
    }
    @Test public void actualYouAreBeatenCannotTargetOwnCharactersHereButCanTargetOpposingCharacters() {
        var scn = fixture(); scn.StartGame();
        var here = scn.GetLSStartingLocation(); var away = scn.GetDSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local");
        var vader = scn.GetDSCard("vader"); var saber = scn.GetDSCard("saber"); var beaten = scn.GetDSCard("beaten");
        scn.MoveCardsToLocation(here, phylo, local, vader, scn.GetDSCard("otherDark")); scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToDSHand(beaten); scn.DSActivateForceCheat(8); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(beaten, "Prevent character from moving or battling");
        assertFalse(scn.DSHasCardChoiceAvailable(phylo));
        assertFalse(scn.DSHasCardChoiceAvailable(local));
        assertTrue(scn.DSHasCardChoiceAvailable(vader));
    }
}
