package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Dengar With Blaster Carbine (V) (Legacy Block 7.1, 1271_4 / 601_6); text per the Virtual Cards Master List (2014) and card image. */
public class Card_601_006_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        light.put("leia", "1_17");
        var dark = new HashMap<String, String>();
        dark.put("dengar", "601_6");
        return new VirtualTableScenario(light, dark, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DengarWithBlasterCarbineVTextIsTheMasterListText() {
        var b = scenario().GetDSCard("dengar").getBlueprint();
        assertEquals("Dengar With Blaster Carbine", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals("While opponent's [Reflections II] objective on table, adds one battle destiny. Permanent weapon is •Blaster Carbine (twice per battle, may target a character or vehicle for free; draw destiny; target 'hit,' and its forfeit = 0, if destiny +1 > defense value).", b.getGameText());
    }

    @Test
    public void BlasterCarbineFiresTwicePerBattleButNotAThirdTime() {
        var scn = scenario();
        var dengar = scn.GetDSCard("dengar");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, dengar, luke, leia);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(6);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        for (int shot = 0; shot < 2; shot++) {
            assertTrue("shot " + (shot + 1), scn.DSCardActionAvailable(dengar, "Fire"));
            scn.PrepareDSDestiny(0);
            scn.DSUseCardAction(dengar, "Fire");
            if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
            scn.PassAllResponses();
            scn.PassWeaponFireWithDestinyDraw();
            scn.PassAllResponses();
            if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(!scn.DSCardActionAvailable(dengar, "Fire"));
    }
}
