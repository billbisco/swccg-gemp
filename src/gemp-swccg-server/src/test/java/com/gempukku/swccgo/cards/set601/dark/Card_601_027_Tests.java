package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Sonic Bombardment (V) (Legacy Block 8.1, 1281_17 / 601_27); text per the Virtual Cards Master List (2014) and card image. */
public class Card_601_027_Tests {
    @Test
    public void SonicBombardmentVTextNamesAStarfighter() {
        var dark = new HashMap<String, String>();
        dark.put("sonic", "601_27");
        var scn = new VirtualTableScenario(new HashMap<>(), dark, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var b = scn.GetDSCard("sonic").getBlueprint();
        assertEquals("Sonic Bombardment", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals("Once per game, may take into hand a prison from Reserve Deck; reshuffle. OR Use 1 Force to take into hand a non-droid assassin (or slaver) of ability < 5 from Reserve Deck; reshuffle. OR During battle involving an assassin, each of opponent's battle destiny draws is -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien.", b.getGameText());
    }
}
