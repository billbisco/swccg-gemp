package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_007_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("terrik", "1261_7", "felth", "2_106", "trooper", "1_194",
                        "trooper2", "1_194", "droid", "1_192", "vader", "1_168")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void verifiesFinalArmoredSandtrooperPrinting() {
        var scn = scenario();
        var bp = scn.GetDSCard("terrik").getBlueprint();
        assertEquals("Captain Mod Terrik", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getArmor(), scn.epsilon);
        assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR,
                Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.SANDTROOPER, Keyword.LEADER, Keyword.CAPTAIN)));
    }

    @Test
    public void onlyOtherSandtroopersReceiveTheDeploymentDiscount() {
        var scn = scenario();
        var terrik = scn.GetDSCard("terrik");
        var felth = scn.GetDSCard("felth");
        var stormtrooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToDSHand(terrik, felth, stormtrooper);
        var site = scn.GetDSStartingLocation();
        float initial = cost(scn, felth, site);
        assertEquals(1, initial, scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), terrik);
        assertEquals(initial - 1, cost(scn, felth, site), scn.epsilon);
        assertEquals(stormtrooper.getBlueprint().getDeployCost(), cost(scn, stormtrooper, site), scn.epsilon);
    }

    @Test
    public void realForfeitPenaltyIsBlockedOnlyForOwnAbilityOneTroopers() {
        var scn = scenario();
        var low = scn.GetDSCard("trooper");
        var high = scn.GetDSCard("felth");
        var droid = scn.GetDSCard("droid");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var source = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(site, scn.GetDSCard("terrik"), low, high, droid, source);
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                new ForfeitModifier(source, Filters.or(low, high, droid), -1));
        assertEquals(low.getBlueprint().getForfeit(), scn.GetForfeit(low), scn.epsilon);
        assertEquals(high.getBlueprint().getForfeit() - 1, scn.GetForfeit(high), scn.epsilon);
        assertEquals(droid.getBlueprint().getForfeit() - 1, scn.GetForfeit(droid), scn.epsilon);
    }

    @Test(timeout = 10000)
    public void actualBattleNeedsTwoTroopersAtItsSiteAndCannotStackWithOrdinaryDestiny() {
        for (int i = 0; i < 3; i++) {
            var scn = scenario();
            scn.StartGame();
            var site = scn.GetDSStartingLocation();
            var remote = scn.GetLSStartingLocation();
            scn.MoveCardsToLocation(remote, scn.GetDSCard("terrik"));
            scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
            scn.MoveCardsToLocation(i == 0 ? remote : site, scn.GetDSCard("trooper2"));
            if (i == 2)
                scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
            scn.DSStartBattleAndSkipToWeaponsSegment(site);
            assertEquals(i == 0 ? 0 : 1, scn.GetDSBattleDestinyCount());
        }
    }

    private float cost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, site,
                false, null, false, 0, null, false);
    }
}
