package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** A Useless Gesture (V) (Legacy Block 6.1, 1261_20 / 601_31); text per the Virtual Cards Master List (2014) and card image. */
public class Card_601_031_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("revolution", "1_62");
        var dark = new HashMap<String, String>();
        dark.put("gesture", "601_31");
        return new VirtualTableScenario(light, dark, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AUselessGestureVTextIsTheMasterListText() {
        var b = scenario().GetDSCard("gesture").getBlueprint();
        assertEquals("A Useless Gesture", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals("Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here (if possible) and use +1 Force for each card here, even if Interrupt is normally free.", b.getGameText());
    }

    @Test
    public void AUselessGestureVDoesNotCancelRevolution() {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("gesture"));
        var revolution = scn.GetLSCard("revolution");
        scn.MoveCardsToLSHand(revolution);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSDeployAvailable(revolution));
        scn.LSDeployCard(revolution);
        if (scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation()))
            scn.LSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertInZone(Zone.ATTACHED, revolution);
        scn.SkipToPhase(Phase.BATTLE);
        assertInZone(Zone.ATTACHED, revolution);
    }
}
