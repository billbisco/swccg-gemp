package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Lando Calrissian, Unlikely Hero (Virtual Block 8.2 slip, Sep 2012), a version of 1282_14. */
public class Card_2282_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lando", "2282_14");
                    put("current", "1282_14");
                    put("ladyluck", "601_49");
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
    public void LandoVersionIdentityAndText() {
        var scn = GetScenario();
        var card = scn.GetLSCard("lando").getBlueprint();

        assertEquals("Lando Calrissian, Unlikely Hero", card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals("Adds 3 to power of anything he pilots. May reveal from hand to take Luxury Yacht into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may draw destiny. If destiny < twice the number of scoundrels here, reset a participating card's forfeit to 0.",
                card.getGameText());
        var version = card.getLegacyVersion();
        org.junit.Assert.assertNotNull(version);
        assertEquals("1282_14", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2012, 9, 14), version.printedOn());
        assertEquals(java.time.LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals("Lando Calrissian, Unlikely Hero", com.gempukku.swccgo.logic.GameUtils.getFullName(card));
    }

    @Test
    public void LandoVersionIsTheSameUniqueCardAsTheCurrentLando() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetLSCard(currentOnTable ? "current" : "lando");
                var inHand = scn.GetLSCard(currentOnTable ? "lando" : "current");
                scn.StartGame();
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetLSStartingLocation(), onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(6);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void LandoMayRevealToDeployWithLuxuryYachtWhichIsLadyLuck() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var ladyluck = scn.GetLSCard("ladyluck");

        scn.StartGame();
        scn.MoveCardsToLSHand(lando);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        assertInZone(Zone.RESERVE_DECK, ladyluck);
        assertTrue(scn.LSCardActionAvailable(lando, "Reveal to deploy with Luxury Yacht (Lady Luck)"));
    }

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
