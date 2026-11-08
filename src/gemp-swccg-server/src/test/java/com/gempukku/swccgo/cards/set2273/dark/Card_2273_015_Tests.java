package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Evacuate? (V) (2011-06-03 sheet), a version of 1273_15: same play, sheet wording. An Interrupt (not unique), so no uniqueness test. */
public class Card_2273_015_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2273_15");
            put("current", "1273_15");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalEvacuateIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_15"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_15"), "1273_15", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "Use 1 Force to target your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile.");
    }
}
