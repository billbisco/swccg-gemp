package com.gempukku.swccgo.cards.set106;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Official Tournament Sealed Deck text states (docs/legacy-versions.md, "Text states"): 106_1o Arleil Schous,
 * 106_7o Red Squadron X-wing, 106_10o Black Squadron TIE and 106_11o Chall Bekan as printed (May 1998). The bare
 * ids keep the Decipher Glossary errata text (wiki.swccg.com [[Arleil Schous (Errata)]] etc.); texts of the printed
 * states from the plain wiki articles.
 */
public class Card_106_TextStates_Tests {
    private static final String ALIEN_PRINTED = "When deployed, you may immediately search your Reserve Deck, take one non-unique alien into hand and reshuffle. Adds 2 to power of any starship he pilots. Your aliens deploy -1 to same or adjacent Tatooine site.";
    private static final String ALIEN_ERRATA = "When deployed, you may take one non-unique alien into hand from Reserve Deck; reshuffle. Adds 2 to power of anything he pilots. Your aliens deploy -1 to same or adjacent Tatooine site.";
    private static final String XWING_PRINTED = "Deploy -2 at Yavin 4 or to same location as Red Leader. Permanent pilot aboard provides ability of 2 and adds 2 to power. Proton Torpedoes deploy and fire free here.";
    private static final String XWING_ERRATA = "Deploy -2 at Yavin 4 or to same location as Red Leader. Permanent pilot provides ability of 2 and adds 2 to power. Proton Torpedoes deploy and fire free aboard.";
    private static final String TIE_PRINTED = "Deploy -2 to same location as Darth Vader or at Death Star. Permanent pilot aboard provides ability of 2 and adds 2 to power. Adds 1 to its weapon destiny draws.";
    private static final String TIE_ERRATA = "Deploy -2 to Death Star or same location as Vader. Permanent pilot aboard provides ability of 2 and adds 2 to power. Adds 1 to its weapon destiny draws.";

    private static VirtualTableScenario scenario(String arleilId, String xwingId, String tieId, String challId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("arleil", arleilId);
                    put("xwing", xwingId);
                    put("x34", "8_83");      // Speeder Bike (combat vehicle, piloted)
                }},
                new HashMap<>() {{
                    put("tie", tieId);
                    put("chall", challId);
                    put("z001", "8_169");    // Speeder Bike (combat vehicle, piloted)
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    private static void assertPrintedState(SwccgCardBlueprint card, String currentId, String title, String text, LocalDate superseded) {
        assertEquals(title, card.getTitle());
        assertEquals(text, card.getGameText());
        TextState state = card.getTextState();
        assertEquals(currentId, state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(superseded, state.supersededOn());
        assertEquals(title + " [Printed]", GameUtils.getFullName(card));
    }

    private static void assertBare(SwccgCardBlueprint card, String title, String text) {
        assertEquals(title, card.getTitle());
        assertEquals(text, card.getGameText());
        assertNull(card.getTextState());
        assertEquals(title, GameUtils.getFullName(card));
    }

    @Test
    public void PrintedStatesHaveThePrintedTexts() {
        var scn = scenario("106_1o", "106_7o", "106_10o", "106_11o");
        assertPrintedState(scn.GetLSCard("arleil").getBlueprint(), "106_1", "Arleil Schous", ALIEN_PRINTED, LocalDate.of(1998, 11, 1));
        assertPrintedState(scn.GetLSCard("xwing").getBlueprint(), "106_7", "Red Squadron X-wing", XWING_PRINTED, LocalDate.of(1998, 11, 1));
        assertPrintedState(scn.GetDSCard("tie").getBlueprint(), "106_10", "Black Squadron TIE", TIE_PRINTED, LocalDate.of(2002, 1, 29));
        assertPrintedState(scn.GetDSCard("chall").getBlueprint(), "106_11", "Chall Bekan", ALIEN_PRINTED, LocalDate.of(1998, 11, 1));
        // Stats are the bare card's
        assertEquals(1, scn.GetLSCard("arleil").getBlueprint().getPower(), scn.epsilon);
        assertEquals(3, scn.GetLSCard("xwing").getBlueprint().getPower(), scn.epsilon);
        assertEquals(1, scn.GetDSCard("tie").getBlueprint().getPower(), scn.epsilon);
        assertEquals(2, scn.GetDSCard("chall").getBlueprint().getPower(), scn.epsilon);
    }

    @Test
    public void BareCardsAreUnchanged() {
        var scn = scenario("106_1", "106_7", "106_10", "106_11");
        assertBare(scn.GetLSCard("arleil").getBlueprint(), "Arleil Schous", ALIEN_ERRATA);
        assertBare(scn.GetLSCard("xwing").getBlueprint(), "Red Squadron X-wing", XWING_ERRATA);
        assertBare(scn.GetDSCard("tie").getBlueprint(), "Black Squadron TIE", TIE_ERRATA);
        assertBare(scn.GetDSCard("chall").getBlueprint(), "Chall Bekan", ALIEN_ERRATA);
    }

    /** Arleil and Chall pilot a vehicle: the printed text adds power only to starships, the errata to anything. */
    private static void assertPilotBonus(String arleilId, String challId, int vehicleBonus) {
        var scn = scenario(arleilId, "106_7", "106_10", challId);
        var site = scn.GetDSStartingLocation();
        var arleil = scn.GetLSCard("arleil");
        var chall = scn.GetDSCard("chall");
        var x34 = scn.GetLSCard("x34");
        var z001 = scn.GetDSCard("z001");
        scn.StartGame();
        scn.MoveCardsToLocation(site, x34, z001);
        scn.BoardAsPilot(x34, arleil);
        scn.BoardAsPilot(z001, chall);
        // Speeder Bike: power 1
        assertEquals(1 + vehicleBonus, scn.GetPower(x34));
        assertEquals(1 + vehicleBonus, scn.GetPower(z001));
    }

    @Test
    public void PrintedAliensAddPowerOnlyToStarshipsTheyPilot() {
        assertPilotBonus("106_1o", "106_11o", 0);
    }

    @Test
    public void BareAliensAddPowerToAnythingTheyPilot() {
        assertPilotBonus("106_1", "106_11", 2);
    }
}
