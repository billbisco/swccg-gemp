package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1116_067_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("junkyard", "12_87");
                }},
                new HashMap<>() {{
                    put("watto", "1116_67");
                    put("oldwatto", "11_65");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void WattoHasWattoPersona() {
        var scn = GetScenario();
        var card = scn.GetDSCard("watto").getBlueprint();

        assertEquals("Watto", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.WATTO);
        }});
    }

    @Test
    public void WattoCountsAsWattoForWattosJunkyardForceDrainBonus() {
        var scn = GetScenario();
        var watto = scn.GetDSCard("watto");
        var junkyard = scn.GetLSCard("junkyard");

        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        scn.MoveCardsToLocation(junkyard, watto);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(junkyard);
        scn.PassForceDrainStartResponses();
        // 1 Dark Side icon + "If Watto present, Force drain +1 here" (Filters.Watto = persona WATTO).
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void WattoMayDeployWhileNoOtherVersionIsOnTable() {
        var scn = GetScenario();
        var watto = scn.GetDSCard("watto");

        scn.StartGame();
        scn.MoveCardsToDSHand(watto);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);
        assertTrue(scn.DSCardPlayAvailable(watto, "Deploy"));
    }

    @Test
    public void WattoMayNotDeployWhileAnotherWattoIsOnTable() {
        var scn = GetScenario();
        var watto = scn.GetDSCard("watto");
        var oldwatto = scn.GetDSCard("oldwatto");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, oldwatto);
        scn.MoveCardsToDSHand(watto);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);
        assertFalse(scn.DSCardPlayAvailable(watto, "Deploy"));
    }
}
