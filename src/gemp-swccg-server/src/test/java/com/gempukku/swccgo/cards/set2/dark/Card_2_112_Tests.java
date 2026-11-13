package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_112_Tests {
    private VirtualTableScenario GetScenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("captive", "1_19");
                    put("hand", "1_19");
                }},
                new HashMap<>() {{
                    put("ito", "2_93");
                    put("hypo", "2_112");
                    put("guard", "1_194");
                    put("escort", "1_194");
                    put("farm", "1_294");
                    put("prison", "1_284");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("farm"));
        scn.MoveLocationToTable(scn.GetDSCard("prison"));
        scn.MoveCardsToLocation(scn.GetDSCard("farm"), scn.GetDSCard("ito"),
                scn.GetDSCard("guard"), scn.GetDSCard("escort"));
        scn.AttachCardsTo(scn.GetDSCard("ito"), scn.GetDSCard("hypo"));
        scn.MoveCardsToLSHand(scn.GetLSCard("hand"));
        scn.EnsureLSForcePile(8);
        return scn;
    }

    @Test
    public void HypoCannotIncreaseDrainWithoutCaptive() {
        //test1: a legal drain does not offer Hypo without its printed captive condition.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("hypo"), "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoCannotIncreaseDrainForCaptiveAtAnotherSite() {
        //test1: a captive elsewhere does not satisfy present-with or with-imprisoned.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var escort = scn.GetDSCard("escort");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), escort);
        scn.CaptureCardWith(escort, scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("hypo"), "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoIncreasesDrainByOneWhenPresentWithCaptive() {
        //test1: the permitted optional choice changes the actual drain from two to three.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal()); //test1
    }

    @Test
    public void HypoIncreasesDrainWithImprisonedCaptiveAtSameSite() {
        //test1: the explicit imprisoned-captive alternative permits the drain choice.
        var scn = GetScenario();
        var prison = scn.GetDSCard("prison");
        var hypo = scn.GetDSCard("hypo");
        scn.MoveCardsToLocation(prison, scn.GetDSCard("ito"), scn.GetDSCard("guard"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), prison);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(prison)); //test1
        scn.DSForceDrainAt(prison);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal()); //test1
    }
}
