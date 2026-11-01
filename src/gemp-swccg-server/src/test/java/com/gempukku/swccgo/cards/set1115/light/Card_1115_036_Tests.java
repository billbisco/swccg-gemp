package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1115_036_Tests {
    @Test
    public void Yt1300StatsAreCorrect() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{ put("yt", "1115_36"); }},
                new HashMap<>() {{ put("trooper", "1_194"); }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var card = scn.GetLSCard("yt").getBlueprint();
        assertEquals("YT-1300 Transport", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertNull(card.getArmor());
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
    }
}
