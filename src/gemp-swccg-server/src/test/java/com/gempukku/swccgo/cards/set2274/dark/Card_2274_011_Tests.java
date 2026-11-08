package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Grievous' Lightsabers (Dec 2011 slip, printed as Cyborg Commander's Lightsabers), a version of 1274_11. */
public class Card_2274_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("sabers", "2274_11");
            put("current", "1274_11");
            put("cyborgCommander", "1271_1");
            put("generalGrievous", "203_27");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalGrievousLightsabersIsTheCurrentCardWithTheDecember2011Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("sabers").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1274_11", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2013, 4, 27),
                "Deploy on Cyborg Commander. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not satisfy attrition (or be removed from Lost Pile this turn), if total destiny > defense value.");
    }

    private boolean mayDeployOn(String sabersKey, String grievousKey) {
        var s = scenario();
        var sabers = s.GetDSCard(sabersKey);
        var grievous = s.GetDSCard(grievousKey);
        s.StartGame();
        s.MoveCardsToLocation(s.GetDSStartingLocation(), grievous);
        s.MoveCardsToDSHand(sabers);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.EnsureDSForcePile(10);
        boolean available = s.DSCardPlayAvailable(sabers);
        if (available) {
            s.DSPlayCard(sabers);
            if (s.DSHasCardChoiceAvailable(grievous)) s.DSChooseCard(grievous);
            s.PassAllResponses();
            assertEquals(grievous, sabers.getAttachedTo());
        }
        return available;
    }

    @Test
    public void OriginalGrievousLightsabersDeployOnCyborgCommander() {
        assertTrue(mayDeployOn("sabers", "cyborgCommander"));
    }

    @Test
    public void OriginalGrievousLightsabersDoNotDeployOnAnotherGrievous() {
        assertFalse(mayDeployOn("sabers", "generalGrievous"));
        assertTrue(mayDeployOn("current", "generalGrievous"));
    }

    @Test
    public void OriginalGrievousLightsabersAreTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            var s = scenario();
            var onTable = s.GetDSCard(currentOnTable ? "current" : "sabers");
            var inHand = s.GetDSCard(currentOnTable ? "sabers" : "current");
            var grievous = s.GetDSCard("cyborgCommander");
            s.StartGame();
            s.MoveCardsToLocation(s.GetDSStartingLocation(), grievous);
            s.AttachCardsTo(grievous, onTable);
            s.MoveCardsToDSHand(inHand);
            s.SkipToDSTurn(Phase.DEPLOY);
            s.EnsureDSForcePile(10);
            assertFalse("current on table=" + currentOnTable, s.DSCardPlayAvailable(inHand));
        }
    }
}
