package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Saber Squadron Pilot (2010-06-18 sheet), a version of 1271_6: same play, sheet wording. Not unique (three per deck), so no uniqueness test; LegacyVersionsTests checks the uniqueness value. */
public class Card_2271_006_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2271_6");
            put("current", "1271_6");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalSaberSquadronPilotIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_6"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_6"), "1271_6", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "[Pilot] 2. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that Force drain).");
    }
}
