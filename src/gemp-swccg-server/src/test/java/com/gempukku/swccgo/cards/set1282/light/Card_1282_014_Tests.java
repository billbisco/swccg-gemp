package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.assertCurrentCard;
import static org.junit.Assert.assertTrue;

/** Lando Calrissian, Unlikely Hero (1282_14, class Card601_050): the current (Legacy) card. */
public class Card_1282_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lando", "1282_14");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void LandoIsTheMasterListText() {
        assertCurrentCard("1282_14", "Lando Calrissian, Unlikely Hero",
                "Adds 3 to power of anything he pilots. May reveal from hand to ▲ Lady Luck and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may draw destiny. If destiny < twice the number of scoundrels here, reset a participating card's forfeit to 0.");
    }

    /** The battle action was built but never returned (bug), so it was never offered. */
    @Test
    public void LandoMayDrawDestinyDuringBattle() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(5);
        scn.LSInitiateBattle(site);
        scn.PassResponses("Battle just initiated");
        assertTrue(scn.LSCardActionAvailable(lando, "Draw destiny"));
    }
}
