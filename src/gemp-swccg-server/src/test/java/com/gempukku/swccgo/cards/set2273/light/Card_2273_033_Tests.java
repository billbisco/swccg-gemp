package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Communing (first printing 2011-06-03, reconstructed in batch B39 as PC-E1 without the I Can't Believe He's Gone sentence), a version of 1273_33: no I Can't Believe He's Gone cancel; it has the look at top cards. An Epic Event played instead of a starting Interrupt (never deployed from hand), so no deploy-from-hand uniqueness test. */
public class Card_2273_033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("icbhg", "1112_12");
            put("jedi1", "1_21");
            put("jedi2", "1_21");
            put("version", "2273_33");
            put("current", "1273_33");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalCommuningIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_33"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_33"), "1273_33", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2011, 6, 22),
                "If your starting location has exactly two [Light Side], play instead of a Starting Interrupt. [download] two always [Immune to Alter] Effects. Deploy on table; [upload] and stack a Jedi here. Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are 'communing' and considered out of play. You generate +1 Force for each card stacked here. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.");
    }

    private VirtualTableScenario withTwoStacked(String key) {
        var s = scenario();
        var communing = s.GetLSCard(key);
        s.StartGame();
        s.MoveCardsToLSSideOfTable(communing, s.GetLSCard("icbhg"));
        s.StackCardsOn(communing, s.GetLSCard("jedi1"));
        s.StackCardsOn(communing, s.GetLSCard("jedi2"));
        s.EnsureLSForcePile(10);
        s.SkipToLSTurn(Phase.CONTROL);
        s.PassAllResponses();
        return s;
    }

    @Test
    public void OriginalCommuningLooksAtTopCardsButDoesNotCancelICantBelieveHesGone() {
        var s = withTwoStacked("version");
        assertTrue(s.LSCardActionAvailable(s.GetLSCard("version"), "Look at top cards"));
        assertEquals(Zone.SIDE_OF_TABLE, s.GetLSCard("icbhg").getZone());
    }

    @Test
    public void CurrentCommuningLooksAtTopCardsAndCancelsICantBelieveHesGone() {
        var s = withTwoStacked("current");
        assertTrue(s.LSCardActionAvailable(s.GetLSCard("current"), "Look at top cards"));
        assertFalse(s.GetLSCard("icbhg").getZone() == Zone.SIDE_OF_TABLE);
    }
}
