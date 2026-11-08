package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.assertSameCard;
import static com.gempukku.swccgo.cards.Vb89VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** PC-E1 Lando Calrissian, Unlikely Hero (PC website card list, Dec 2012), a version of 1282_14. */
public class Card_2282_1014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lando", "2282_1014");
                    put("current", "1282_14");
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
    public void LandoPcE1IdentityAndText() {
        assertVersion("2282_1014", "1282_14", "Lando Calrissian, Unlikely Hero",
                "Adds 3 to anything he pilots. May reveal from hand to take Luxury Yacht into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may target a card here. Draw destiny. If destiny < twice the number of scoundrels here, target is forfeit = 0.",
                "PC-E1", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27));
        assertSameCard("2282_1014", "1282_14");
    }

    @Test
    public void LandoPcE1IsTheSameUniqueCardAsTheCurrentLando() {
        var scn = GetScenario();
        var current = scn.GetLSCard("current");
        var lando = scn.GetLSCard("lando");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), current);
        scn.MoveCardsToLSHand(lando);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        assertFalse(scn.LSDeployAvailable(lando));
    }

    @Test
    public void LandoPcE1TargetsACardHereBeforeDrawingDestiny() {
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
        assertFalse(scn.LSCardActionAvailable(lando, "Draw destiny"));
        assertTrue(scn.LSCardActionAvailable(lando, "Target a card here"));
        scn.LSUseCardAction(lando, "Target a card here");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
    }
}
