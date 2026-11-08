package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.*;

/** Dark Waters & Krayt Dragon Bones PC-E1 version of 1261_33. */
public class Card_2261_1033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_1033", "current", "1261_33")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_33", "PC-E1",
                LocalDate.of(2010, 7, 26), LocalDate.of(2012, 7, 21),
                "Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)");
    }

    private VirtualTableScenario downloadScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("waters", "2261_1033", "pit", "6_166", "krayt", "7_211", "krayt2", "7_211",
                        "womprat", "7_215", "ysalamir", "1240_17", "trooper", "1_194")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private static List<String> selectableBlueprints(VirtualTableScenario scn) {
        var params = scn.DSGetDecision().getDecisionParameters();
        var ids = params.get("blueprintId");
        var selectable = params.get("selectable");
        List<String> result = new ArrayList<>();
        for (int i = 0; ids != null && i < ids.length; i++)
            if (selectable == null || "true".equals(selectable[i]))
                result.add(ids[i]);
        return result;
    }

    private static boolean dsHas(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static final String DEPLOY_CREATURE = "Deploy creature from Reserve Deck";

    @Test(timeout = 15000)
    public void mayDeployNonSelectiveFerociousCreatureHereFromReserveDeckEvenOutsideItsHabitat() {
        var scn = downloadScenario();
        var waters = scn.GetDSCard("waters");
        var pit = scn.GetDSCard("pit");
        var krayt = scn.GetDSCard("krayt");
        var womprat = scn.GetDSCard("womprat");
        var ysalamir = scn.GetDSCard("ysalamir");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.AttachCardsTo(pit, waters);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(12);
        scn.MoveCardsToTopOfDSReserveDeck(krayt, womprat, ysalamir);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(waters, DEPLOY_CREATURE));
        scn.DSUseCardAction(waters, DEPLOY_CREATURE);
        scn.PassAllResponses();
        var offered = selectableBlueprints(scn);
        assertTrue(offered.toString(), offered.contains(krayt.getBlueprintId(true)));
        assertFalse("selective creature offered", offered.contains(womprat.getBlueprintId(true)));
        assertFalse("non-ferocious creature offered", offered.contains(ysalamir.getBlueprintId(true)));
        scn.DSChooseCard(krayt);
        scn.PassAllResponses();
        // Krayt Dragon's own habitat (deserts and Tatooine exterior sites) does not include the interior Rancor Pit.
        assertSame(pit, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), krayt));
        assertEquals(force - krayt.getBlueprint().getDeployCost().intValue(), scn.GetDSForcePileCount());
        assertEquals(Zone.AT_LOCATION, krayt.getZone());
    }

    @Test(timeout = 15000)
    public void thisClauseGivesNoDeployHereFromHandAndNoDownloadOutsideDeployPhase() {
        var scn = downloadScenario();
        var waters = scn.GetDSCard("waters");
        var pit = scn.GetDSCard("pit");
        var krayt2 = scn.GetDSCard("krayt2");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.AttachCardsTo(pit, waters);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(krayt2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(12);
        var game = scn.game();
        assertFalse(krayt2.getBlueprint().getHabitatFilter(game, krayt2).accepts(game, pit));
        if (scn.DSDeployAvailable(krayt2)) {
            scn.DSDeployCard(krayt2);
            assertFalse(scn.DSHasCardChoiceAvailable(pit));
        }

        var battle = downloadScenario();
        var waters2 = battle.GetDSCard("waters");
        battle.StartGame();
        battle.MoveLocationToTable(battle.GetDSCard("pit"));
        battle.AttachCardsTo(battle.GetDSCard("pit"), waters2);
        battle.MoveCardsToLocation(battle.GetDSStartingLocation(), battle.GetDSCard("trooper"));
        battle.SkipToDSTurn(Phase.BATTLE);
        assertFalse(dsHas(battle, waters2, DEPLOY_CREATURE));
    }
}
