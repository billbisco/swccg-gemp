package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertLocationVersion;
import static org.junit.Assert.assertEquals;

/** Death Star: Central Core (V) as errata'd 2010-07-26 (PC-E1), a version of 1271_22: same play, "Immune" spelled out. */
public class Card_2271_022_Tests {
    @Test
    public void ErrataDeathStarCentralCoreIsTheCurrentCardWithThe2010Text() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_22");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_22");
        assertLocationVersion(version, current, "1271_22", "PC-E1", LocalDate.of(2010, 7, 26), LocalDate.of(2013, 4, 27),
                "Opponent's Force drain bonuses at systems (except your systems) are limited to 1. Immune to Expand The Empire.",
                "May deploy Superlaser from Reserve Deck; reshuffle. At 'blown away' systems, adds one [Light Side] and one [Dark Side]. Whenever you Force drain at a 'blown away' system Death Star orbits, may retrieve 1 Force.", false);
        assertEquals(current.getLocationDarkSideGameText(), version.getLocationDarkSideGameText());
    }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new java.util.HashMap<>() {{
        }}, new java.util.HashMap<>() {{
            put("version", "2271_22");
            put("current", "1271_22");
        }}, 30, 30, com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ErrataDeathStarCentralCoreIsTheSameUniqueCardAsTheCurrentCard() {
        com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveLocationToTable(c);
        });
    }
}
