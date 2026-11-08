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

/** Original Shadows Of The Empire (2011-12-16 sheet), a version of 1274_8: same play, sheet wording. Deploys only on the Agents Of Black Sun Objective, so no deploy-from-hand uniqueness test. */
public class Card_2274_008_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2274_8");
            put("current", "1274_8");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalShadowsOfTheEmpireIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_8"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_8"), "1274_8", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "Deploy on Agents Of Black Sun. Mistryl are lost. Once per game, may [upload] Secret Plans. May use 3 Force to [download] Imperial Square, and once per turn, if Emperor Palpatine there, may draw a card from Force Pile (and if you occupy three battlegrounds, opponent loses 1 Force). [Immune to Alter.]");
    }
}
