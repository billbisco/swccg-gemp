package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.*;

/** Leave Them To Me (Death Star II) (V) PC-E1 version of 1261_24. */
public class Card_2261_024_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_24", "current", "1261_24", "shield21", "1261_21")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_24", "PC-E1",
                LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21),
                "Plays on table. At opponent's Dantooine and Subjugated planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent.");
    }

    @Test
    public void RevisedSheetTextHasNoSwampJungleOrForestClauses() {
        var scn = scenario();
        scn.StartGame();
        var v = scn.GetDSCard("v");
        var current = scn.GetDSCard("current");
        // The current printing plays all five clauses; the revised-sheet version only the first three.
        assertEquals(3, v.getBlueprint().getWhileInPlayModifiers(scn.game(), v).size());
        assertEquals(5, current.getBlueprint().getWhileInPlayModifiers(scn.game(), current).size());
        assertSame(scn.GetDSCard("shield21").getBlueprint().getClass(), current.getBlueprint().getClass());
    }
}
