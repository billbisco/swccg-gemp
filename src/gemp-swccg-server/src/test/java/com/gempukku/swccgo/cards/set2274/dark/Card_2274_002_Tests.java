package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original OOM Command Battle Droid (2011-12-16 sheet), a version of 1274_2: same play, sheet wording. Two per deck (not unique), so no uniqueness test. */
public class Card_2274_002_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2274_2");
            put("current", "1274_2");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalOOMCommandBattleDroidIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_2"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_2"), "1274_2", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "[Pilot] 2: any vehicle. While piloting a [Theed Palace] vehicle at a battleground site, adds one [Dark Side] and may add 1 to your Force drains here. Once per game, may [upload] a card with 'fire' in title.");
    }
}
