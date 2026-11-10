package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Hermi Odle (V) (Dec 2009 sheet), a version of 1230_7. */
public class Card_2230_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disarmed", "1_48");
                    put("wookieeWin", "2_53");
                }},
                new HashMap<>() {{
                    put("hermi", "2230_7");
                    put("current", "1230_7");
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
    public void HermiOdleIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("hermi").getBlueprint(),
                "Aliens at same and related sites may not have their weapons stolen or be targeted by Disarmed, and, while armed with a non-lightsaber, non-[Permanent Weapon] weapon: their weapon destiny draws are +1 and opponent's [Permanent Weapon] characters are power -1 there.",
                "1230_7", "", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 3, 21));
    }

    @Test
    public void HermiOdleIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "hermi");
                var inHand = scn.GetDSCard(currentOnTable ? "hermi" : "current");
                scn.StartGame();
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(8);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSDeployAvailable(inHand));
            }
        }
    }

    private static boolean[] disarmTargets(String hermiKey) {
        var scn = new Card_2230_007_Tests().GetScenario();
        var hermi = scn.GetDSCard(hermiKey);
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), hermi);
        var byDisarmed = Filters.canBeTargetedBy(scn.GetLSCard("disarmed"), TargetingReason.TO_BE_DISARMED);
        var byOtherCard = Filters.canBeTargetedBy(scn.GetLSCard("wookieeWin"), TargetingReason.TO_BE_DISARMED);
        return new boolean[]{byDisarmed.accepts(scn.game(), hermi), byOtherCard.accepts(scn.game(), hermi)};
    }

    @Test
    public void OriginalHermiOdleStopsOnlyTheCardDisarmed() {
        //test1: an alien here (Hermi himself) may not be targeted by Disarmed
        //test2: another card may still target him to be disarmed (Let The Wookiee Win)
        boolean[] targets = disarmTargets("hermi");
        assertFalse(targets[0]); //test1
        assertTrue(targets[1]); //test2
    }

    @Test
    public void CurrentHermiOdleStopsEveryDisarm() {
        boolean[] targets = disarmTargets("current");
        assertFalse(targets[0]);
        assertFalse(targets[1]);
    }
}
