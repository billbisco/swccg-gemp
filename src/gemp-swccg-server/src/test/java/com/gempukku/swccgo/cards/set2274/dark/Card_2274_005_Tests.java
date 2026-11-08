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

/** Original Desperate Counter (V) (2011-12-16 sheet), a version of 1274_5: same play, sheet wording. Deploys only as a response, so no deploy-from-hand uniqueness test. */
public class Card_2274_005_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2274_5");
            put("current", "1274_5");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalDesperateCounterIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_5"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_5"), "1274_5", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may [upload] any one card. [Immune to Control.] OR Use 2 Force to deploy on table and stack a just-played Interrupt here.");
    }
}
