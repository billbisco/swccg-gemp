package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("results", "1112_23");
                    put("invasion", "14_113");
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

    private VirtualTableScenario ScenarioWithInvasionOutOfPlay() {
        var scn = GetScenario();
        scn.MoveOutOfPlay(scn.GetDSCard("invasion"));
        return scn;
    }

    @Test
    public void AtLastWeAreGettingResultsDeploysOnlyWhileInvasionIsOnTable() {
        var scn = ScenarioWithInvasionOutOfPlay();
        var results = scn.GetDSCard("results");
        var blueprint = results.getBlueprint();
        var invasion = scn.GetDSCard("invasion");

        assertEquals("At Last We Are Getting Results", blueprint.getTitle());
        assertTrue(blueprint.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, blueprint.getUniqueness());
        assertEquals(Side.DARK, blueprint.getSide());
        assertEquals(4, blueprint.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(blueprint, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintIconCheck(blueprint, new ArrayList<>() {{
            add(Icon.THEED_PALACE);
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, blueprint.getExpansionSet());
        assertEquals(Rarity.V, blueprint.getRarity());

        scn.StartGame();
        scn.MoveCardsToDSHand(results);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(results));
    }

    @Test
    public void AtLastWeAreGettingResultsDeploysOnTableWhenInvasionIsOnTable() {
        var scn = ScenarioWithInvasionOutOfPlay();
        var results = scn.GetDSCard("results");
        var invasion = scn.GetDSCard("invasion");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(invasion);
        scn.MoveCardsToDSHand(results);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(results));
        scn.DSDeployCard(results);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, results);
    }
}
