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

/** Original Tibanna Floating Refinery (Dec 2011 slip, printed as Floating Refinery (V)), a version of 1274_4. */
public class Card_2274_004_Tests {
    private static final String DRAW = "Draw destiny to retrieve Force";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("tibanna", "2274_4");
            put("current", "1274_4");
            put("bespin", "5_164");
            put("clouds", "5_174");
            put("tie", "1_304");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalTibannaFloatingRefineryIsTheCurrentCardWithTheDecember2011Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("tibanna").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1274_4", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 11, 2),
                "Deploy on a cloud sector (limit one per sector). Your Force generation is +1 at cloud sectors you occupy. Once during your control phase, if you occupy this sector, may draw destiny. If destiny > 2, retrieve 1 Force. If lost from table, place this Device in Used Pile.");
    }

    @Test
    public void OriginalTibannaFloatingRefineryIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetDSCard(currentOnTable ? "current" : "tibanna");
                var inHand = s.GetDSCard(currentOnTable ? "tibanna" : "current");
                var clouds = s.GetDSCard("clouds");
                s.StartGame();
                s.MoveLocationToTable(s.GetDSCard("bespin"));
                s.MoveLocationToTable(clouds);
                if (otherOut)
                    s.AttachCardsTo(clouds, onTable);
                s.MoveCardsToDSHand(inHand);
                s.SkipToDSTurn(Phase.DEPLOY);
                s.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.DSCardPlayAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalTibannaFloatingRefineryDrawsWithoutABattlegroundSiteButTheCurrentCardDoesNot() {
        for (String key : List.of("tibanna", "current")) {
            var s = scenario();
            var refinery = s.GetDSCard(key);
            var clouds = s.GetDSCard("clouds");
            s.StartGame();
            s.MoveLocationToTable(s.GetDSCard("bespin"));
            s.MoveLocationToTable(clouds);
            s.MoveCardsToLocation(clouds, s.GetDSCard("tie"));
            s.AttachCardsTo(clouds, refinery);
            s.SkipToDSTurn(Phase.CONTROL);
            assertEquals(key, "tibanna".equals(key), s.DSCardActionAvailable(refinery, DRAW));
        }
    }

    @Test
    public void OriginalTibannaFloatingRefineryNeedsYouToOccupyItsSector() {
        var s = scenario();
        var refinery = s.GetDSCard("tibanna");
        var clouds = s.GetDSCard("clouds");
        s.StartGame();
        s.MoveLocationToTable(s.GetDSCard("bespin"));
        s.MoveLocationToTable(clouds);
        s.AttachCardsTo(clouds, refinery);
        s.SkipToDSTurn(Phase.CONTROL);
        assertFalse(s.DSCardActionAvailable(refinery, DRAW));
        assertTrue(refinery.getAttachedTo() == clouds);
    }
}
