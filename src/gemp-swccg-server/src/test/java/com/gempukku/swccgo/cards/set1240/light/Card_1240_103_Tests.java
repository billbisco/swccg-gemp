package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_103_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("station", "1240_103");
                    put("tat", "1_127");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void RebelCellMonitoringStationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("station").getBlueprint();

        assertEquals("Rebel Cell - Monitoring Station", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SPECIAL_EDITION);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelCellMonitoringStationDoesNotDeployWithoutHiddenLandingSite() {
        var scn = GetScenario();
        var station = scn.GetLSCard("station");

        scn.StartGame();
        scn.MoveCardsToLSHand(station);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(station));
    }

    @Test
    public void RebelCellMonitoringStationDeploysToSamePlanetAsHiddenLandingSite() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("tat"));
        scn.MoveCardsToLSHand(hls, station);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(Zone.LOCATIONS, hls.getZone());

        assertTrue(scn.LSDeployAvailable(station));
        scn.LSDeployCard(station);
        assertTrue(scn.LSHasCardChoiceAvailable(hls));
        scn.LSChooseCard(hls);
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, station.getZone());
        assertEquals("Tatooine", station.getPartOfSystem());
    }
}
