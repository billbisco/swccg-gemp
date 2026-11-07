package com.gempukku.swccgo.rules.creatures;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyCreatureFilters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.LandspeedModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class LegacyCreatureClassificationTests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
            put("sleen", "4_111"); put("wampa", "3_93"); put("mynock", "4_110");
            put("vine", "4_113"); put("bogwing", "4_108"); put("trooper", "1_194");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        for (String name : new String[]{"sleen", "wampa", "mynock", "vine", "bogwing", "trooper"})
            scn.MoveCardsToDSHand(scn.GetDSCard(name));
        return scn;
    }

    @Test
    public void DefenseValueTwoDoesNotDisqualifyANonParasite() {
        var scn = scenario();
        assertTrue(LegacyCreatureFilters.ferocious.accepts(scn.game(), scn.GetDSCard("sleen")));
        assertFalse(LegacyCreatureFilters.packhunter.accepts(scn.game(), scn.GetDSCard("sleen")));
        assertTrue(LegacyCreatureFilters.ferocious.accepts(scn.game(), scn.GetDSCard("wampa")));
    }

    @Test
    public void ParasitesAreExcludedRegardlessOfDefenseValueAndLandspeed() {
        var scn = scenario();
        for (String name : new String[]{"mynock", "vine", "bogwing"}) {
            var card = scn.GetDSCard(name);
            assertFalse(name, LegacyCreatureFilters.ferocious.accepts(scn.game(), card));
            assertFalse(name, LegacyCreatureFilters.packhunter.accepts(scn.game(), card));
            scn.ApplyAdHocModifier(new LandspeedModifier(scn.GetDSStartingLocation(), Filters.sameCardId(card), 1));
            assertFalse(name, LegacyCreatureFilters.ferocious.accepts(scn.game(), card));
            assertFalse(name, LegacyCreatureFilters.packhunter.accepts(scn.game(), card));
        }
    }

    @Test
    public void SelectiveCreatureBecomesPackhunterOnlyAboveOneLandspeed() {
        var scn = scenario();
        var sleen = scn.GetDSCard("sleen");
        scn.ApplyAdHocModifier(new LandspeedModifier(scn.GetDSStartingLocation(), Filters.sameCardId(sleen), 1));
        assertTrue(LegacyCreatureFilters.packhunter.accepts(scn.game(), sleen));
        assertFalse(LegacyCreatureFilters.ferocious.accepts(scn.game(), sleen));
    }

    @Test
    public void NonSelectiveCreatureWithIncreasedLandspeedRemainsFerocious() {
        var scn = scenario();
        var wampa = scn.GetDSCard("wampa");
        scn.ApplyAdHocModifier(new LandspeedModifier(scn.GetDSStartingLocation(), Filters.sameCardId(wampa), 1));
        assertFalse(LegacyCreatureFilters.packhunter.accepts(scn.game(), wampa));
        assertTrue(LegacyCreatureFilters.ferocious.accepts(scn.game(), wampa));
        assertFalse(LegacyCreatureFilters.packhunter.accepts(scn.game(), scn.GetDSCard("trooper")));
        assertFalse(LegacyCreatureFilters.ferocious.accepts(scn.game(), scn.GetDSCard("trooper")));
    }
}
