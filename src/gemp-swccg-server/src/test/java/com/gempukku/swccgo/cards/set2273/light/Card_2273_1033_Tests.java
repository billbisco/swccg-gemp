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

/** Communing PC-E1 (2011-06-23 CBT errata), a version of 1273_33: same play as the current card. Never deployed from hand, so no deploy-from-hand uniqueness test. */
public class Card_2273_1033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("jedi1", "1_21");
            put("jedi2", "1_21");
            put("version", "2273_1033");
            put("current", "1273_33");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ErrataCommuningIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_1033"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_33"), "1273_33", "PC-E1",
                LocalDate.of(2011, 6, 23), LocalDate.of(2011, 7, 25),
                "If your starting location has exactly two [Light Side], play instead of a Starting Interrupt. [download] two always [Immune to Alter] Effects. Deploy on table; [upload] and stack a Jedi here. Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are 'communing' and considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.");
    }

    @Test
    public void ErrataCommuningLooksAtTopCardsLikeTheCurrentCard() {
        var s = scenario();
        var communing = s.GetLSCard("version");
        s.StartGame();
        s.MoveCardsToLSSideOfTable(communing);
        s.StackCardsOn(communing, s.GetLSCard("jedi1"));
        s.StackCardsOn(communing, s.GetLSCard("jedi2"));
        s.EnsureLSForcePile(10);
        s.SkipToLSTurn(Phase.CONTROL);
        assertTrue(s.LSCardActionAvailable(communing, "Look at top cards"));
    }
}
