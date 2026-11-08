package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
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

/** Restore Freedom To The Galaxy as on the PC card list (PC-E2, by 2012-07-21), a version of 1271_46: same play. */
public class Card_2271_2046_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("restore", "2271_2046");
            put("current", "1271_46");
            put("yavin", "1_135");
            put("bespin", "5_076");
            put("xwing", "1_146");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CardListRestoreFreedomIsTheCurrentCardWithTheJune2010Text() {
        var s = scenario();
        assertVersion(s.GetLSCard("restore").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1271_46", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "Deploy on Yavin 4. Once per turn, may deploy a battleground system or an interior Yavin 4 site. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from either player's Reserve Deck there (limit one per system) to 'liberate' the system. At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile.");
    }

    @Test
    public void CardListRestoreFreedomIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetLSCard(currentOnTable ? "current" : "restore");
                var inHand = s.GetLSCard(currentOnTable ? "restore" : "current");
                var yavin = s.GetLSCard("yavin");
                s.StartGame();
                s.MoveLocationToTable(yavin);
                if (otherOut)
                    s.AttachCardsTo(yavin, onTable);
                s.MoveCardsToLSHand(inHand);
                s.SkipToLSTurn(Phase.DEPLOY);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.LSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void CardListRestoreFreedomStacksTheLiberatingCardFaceUp() {
        var s = scenario();
        var restore = s.GetLSCard("restore");
        var yavin = s.GetLSCard("yavin");
        var bespin = s.GetLSCard("bespin");
        s.StartGame();
        s.MoveLocationToTable(yavin);
        s.MoveCardsToHand(restore);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(restore);
        s.LSChooseCard(yavin);
        s.PassAllResponses();
        s.MoveLocationToTable(bespin);
        s.MoveCardsToLocation(bespin, s.GetLSCard("xwing"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.PrepareLSDestiny(7);
        s.LSForceDrainAt(bespin);
        s.DSPass();
        s.LSChooseAction("Draw destiny to liberate");
        s.PassAllResponses();
        s.LSChoose("Light side");
        s.PassAllResponses();
        var stacked = s.GetStackedCards(bespin);
        assertFalse(stacked.isEmpty());
        assertEquals(Zone.STACKED, stacked.get(0).getZone()); // face up
        s.PassForceDrainStartResponses();
        assertEquals(2, s.GetForceDrainTotal());
        assertTrue(restore.getAttachedTo() == yavin);
    }
}
