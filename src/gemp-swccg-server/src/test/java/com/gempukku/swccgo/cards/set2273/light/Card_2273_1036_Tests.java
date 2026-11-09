package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
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
import static org.junit.Assert.assertTrue;

/** The Shield Is Down! (V) PC-E1 (AR 2.0 Appendix A, by Aug 2012: once per turn, every non-Jedi scout at Bunker counts), a version of 1273_36. */
public class Card_2273_1036_Tests {
    private static final String TAKE = "Take Bunker into hand from Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("shield", "2273_1036");
            put("current", "1273_36");
            put("endor", "8_068");
            put("bunker", "8_070");
            put("dtsg", "8_043");
            put("beezer", "8_004");
            put("beezer2", "8_001");
        }}, new HashMap<>() {{
            put("grond", "8_102");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ShieldIsDownPcE1IsTheCurrentCardWithItsOlderText() {
        var s = scenario();
        assertVersion(s.GetLSCard("shield").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1273_36", "PC-E1",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "If [Virtual Block 7] Rebel Strike Team on table, deploy on table. Your [Endor] Epic Event total is +1 for each non-Jedi scout at Bunker. For each of your non-Jedi scouts in battle, attrition against opponent is +1. While opponent's [Endor] objective on table, Bunker may not be converted. Once per turn may take Bunker into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
    }

    @Test
    public void ShieldIsDownPcE1IsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetLSCard(currentOnTable ? "current" : "shield");
                var inHand = s.GetLSCard(currentOnTable ? "shield" : "current");
                s.StartGame();
                if (otherOut)
                    s.MoveCardsToLSSideOfTable(onTable);
                s.MoveCardsToLSHand(inHand);
                s.LSActivateForceCheat(6);
                s.SkipToLSTurn(Phase.DEPLOY);
                com.gempukku.swccgo.cards.set1273.light.Vb73LightTestSupport.putOnTable(s, s.LS, "1273_43");
                s.SkipToNextTurn(s.LS);
                s.SkipToLSTurn(Phase.DEPLOY);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.LSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void ShieldIsDownPcE1CountsEveryNonJediScoutAtBunkerForTheEpicEventTotal() {
        // "+1 for each non-Jedi scout at Bunker": the Master List made it "each of your non-Jedi scouts"
        var s = scenario();
        var bunker = s.GetLSCard("bunker");
        var dtsg = s.GetLSCard("dtsg");
        s.StartGame();
        s.MoveLocationToTable(s.GetLSCard("endor"));
        s.MoveLocationToTable(bunker);
        s.AttachCardsTo(bunker, dtsg);
        s.MoveCardsToLocation(bunker, s.GetLSCard("beezer"), s.GetLSCard("beezer2"));
        s.MoveCardsToLSSideOfTable(s.GetLSCard("shield"));
        var q = s.game().getModifiersQuerying();
        assertEquals(12, q.getEpicEventCalculationTotal(s.gameState(), dtsg, 10), s.epsilon);
        var grond = s.GetDSCard("grond");
        assertTrue(com.gempukku.swccgo.filters.Filters.scout.accepts(s.game(), grond));
        s.MoveCardsToLocation(bunker, grond);
        assertEquals(13, q.getEpicEventCalculationTotal(s.gameState(), dtsg, 10), s.epsilon);
    }

    @Test
    public void ShieldIsDownPcE1TakesBunkerOnlyOncePerTurn() {
        var s = scenario();
        var shield = s.GetLSCard("shield");
        var bunker = s.GetLSCard("bunker");
        s.StartGame();
        s.MoveCardsToLSSideOfTable(shield);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSUseCardAction(shield, TAKE);
        if (Vb81RestTestSupport.passUntil(s, () -> s.LSHasCardChoiceAvailable(bunker)))
            s.LSChooseCard(bunker);
        s.PassAllResponses();
        assertEquals(Zone.HAND, bunker.getZone());
        s.MoveCardsToTopOfLSReserveDeck(bunker);
        if (s.DS.equals(s.GetDecidingPlayer()))
            s.DSPass();
        assertEquals(s.LS, s.GetDecidingPlayer());
        assertEquals(false, s.LSCardActionAvailable(shield, TAKE));
    }
}
