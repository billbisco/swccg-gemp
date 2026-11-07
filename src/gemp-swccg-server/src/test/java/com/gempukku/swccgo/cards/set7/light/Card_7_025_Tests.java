package com.gempukku.swccgo.cards.set7.light;

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

public class Card_7_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ketwol", "7_25");
                    put("ketwolv", "216_34");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void KetwolHasKetwolPersona() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ketwol").getBlueprint();

        assertEquals("Ketwol", card.getTitle());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.KETWOL);
        }});
    }

    @Test
    public void KetwolMayDeployWhileNoOtherVersionIsOnTable() {
        var scn = GetScenario();
        var ketwol = scn.GetLSCard("ketwol");

        scn.StartGame();
        scn.MoveCardsToLSHand(ketwol);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        assertTrue(scn.LSCardPlayAvailable(ketwol, "Deploy"));
    }

    @Test
    public void KetwolMayNotDeployWhileKetwolVIsOnTable() {
        var scn = GetScenario();
        var ketwol = scn.GetLSCard("ketwol");
        var ketwolv = scn.GetLSCard("ketwolv");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ketwolv);
        scn.MoveCardsToLSHand(ketwol);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        assertFalse(scn.LSCardPlayAvailable(ketwol, "Deploy"));
    }
}
