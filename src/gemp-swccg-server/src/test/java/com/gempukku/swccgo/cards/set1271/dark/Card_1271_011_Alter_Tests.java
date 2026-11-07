package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1271_011_Alter_Tests {
    @Test
    public void IllTakeThemMyselfCannotBeAlteredWhileAnUnprotectedEffectCanActuallyBeCanceled() {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("obiwan", "1_21");
            put("alter", "1_71");
        }}, new HashMap<>() {{
            put("myself", "1271_11");
            put("rack", "1_211");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("myself"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("obiwan"));
        scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("alter")));

        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("rack"));
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("alter")));
        scn.PrepareLSDestiny(1);
        scn.LSPlayCard(scn.GetLSCard("alter"));
        scn.PassAllResponses();
        assertEquals(Zone.LOST_PILE, scn.GetDSCard("rack").getZone());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("myself").getZone());
    }
}
