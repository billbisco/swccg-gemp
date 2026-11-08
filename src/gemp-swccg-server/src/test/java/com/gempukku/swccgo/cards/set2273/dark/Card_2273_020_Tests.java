package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.resolveUntil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Original A Stunning Move / A Valuable Hostage (2011-06-03 sheet), a version of 1273_20: deploys Cyborg Commander (Grievous, Hunter Of Jedi only) or The Count. */
public class Card_2273_020_Tests {
    private static StartingSetup objective(String id) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("asm", id);
                    put("quarters", "1273_17");
                    put("platform", "601_150");
                    put("prisoner", "1273_13");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                resolveUntil(scn, () -> scn.GetDSCard("prisoner").getZone() == Zone.ATTACHED
                        && scn.GetAwaitingDecision(scn.DS) != null && scn.GetAwaitingDecision(scn.DS).getText().contains("Activate"));
            }
        };
    }

    private VirtualTableScenario scenario(String id) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("hunter", "1271_1");
            put("general", "203_27");
            put("squad", "1273_3");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, objective(id),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalAStunningMoveIsTheCurrentCardWithItsOwnText() {
        for (String side : new String[]{"", "_BACK"}) {
            var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_20" + side);
            var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_20" + side);
            assertVersion(version, current, "1273_20" + side, "", LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27),
                    "A Stunning Move: Deploy Palpatine's Quarters, Private Platform, and Insidious Prisoner. For remainder of game, Sidious and Imperials are lost. Once per turn, may deploy a Blockade Flagship site from Reserve Deck; reshuffle. Blockade Flagship may not be 'blown away.' While this side up, Insidious Prisoner only follows opponent's characters when moving to Palpatine's Quarters. Once per game, may deploy (for -2 Force) Cyborg Commander or The Count to a site you occupy from Reserve Deck; reshuffle. Flip this card if Insidious Prisoner is on Blockade Flagship. A Valuable Hostage: Immediately take into hand from Reserve Deck; reshuffle (or retrieve into hand) a [Presence] droid. While this side up, immunity to attrition of opponent's Jedi, starships, and vehicles is limited to < 5. During your control phase, opponent loses 1 Force for each battleground you control. Once during your turn, may search your Force Pile and reveal any one card; reshuffle. Opponent may lose 2 Force to place that card on bottom of your Used Pile; otherwise, take it into hand. Flip this card if Insidious Prisoner not at a Blockade Flagship site.");
        }
    }

    private boolean deploys(String objectiveId, String actionText, String key) {
        var s = scenario(objectiveId);
        var quarters = s.GetDSCard("quarters");
        var target = s.GetDSCard(key);
        s.StartGame();
        s.MoveCardsToLocation(quarters, s.GetDSCard("squad"));
        s.EnsureDSForcePile(12);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfDSReserveDeck(target);
        s.DSUseCardAction(s.GetDSCard("asm"), actionText);
        resolveUntil(s, () -> target.getZone() == Zone.AT_LOCATION || s.AwaitingDSDeployPhaseActions());
        s.PassAllResponses();
        return target.getZone() == Zone.AT_LOCATION;
    }

    @Test
    public void OriginalAStunningMoveDeploysTheCardPrintedAsCyborgCommanderButNotAnotherGrievous() {
        assertTrue(deploys("2273_20", "Deploy Cyborg Commander or The Count", "hunter"));
        assertEquals(false, deploys("2273_20", "Deploy Cyborg Commander or The Count", "general"));
    }

    @Test
    public void CurrentAStunningMoveDeploysAnyGrievous() {
        assertTrue(deploys("1273_20", "Deploy Grievous or Dooku", "general"));
    }
}
