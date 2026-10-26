package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_227_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
            }},
            new HashMap<>() {{
            }},
            10,
            10,
            StartingSetup.DefaultLSGroundLocation,
            new StartingSetup.StartingLocation("227_009"),
            StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,
            StartingSetup.NoDSShields,
            VirtualTableScenario.Open
        );
    }

    @Test
    public void GeonosisSeparatistControlledMayBeStartingLocationFrontSideUp() {
        //test1: Geonosis (Separatist Controlled) deploys as the starting location
        //test2: it starts front side up (not flipped)
        var scn = GetScenario();

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        assertEquals(Zone.LOCATIONS, system.getZone()); //test1
        assertEquals(Title.Geonosis, system.getTitle()); //test1
        assertTrue(system.getBlueprint().isFrontOfDoubleSidedCard()); //test2
        assertFalse(system.isFlipped()); //test2
    }
}
