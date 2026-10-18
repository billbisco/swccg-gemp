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

public class Card_1240_105_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("room", "1240_105");
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
    public void RebelCellSituationRoomStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("room").getBlueprint();

        assertEquals("Rebel Cell - Situation Room", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.A_NEW_HOPE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelCellSituationRoomDoesNotDeployWithoutHiddenLandingSite() {
        var scn = GetScenario();
        var room = scn.GetLSCard("room");

        scn.StartGame();
        scn.MoveCardsToLSHand(room);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(room));
    }

    @Test
    public void RebelCellSituationRoomDeploysToSamePlanetAsHiddenLandingSite() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("room", "1240_105");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");

        scn.StartGame();
        scn.MoveCardsToLSHand(room);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertEquals(Zone.LOCATIONS, hls.getZone());
        assertEquals("Yavin 4", hls.getPartOfSystem());
        assertTrue(scn.LSDeployAvailable(room));
        scn.LSDeployCard(room);
        if (scn.LSDecisionAvailable("Choose a location")) {
            scn.LSChooseCard(hls);
        }
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, room.getZone());
        assertEquals("Yavin 4", room.getPartOfSystem());
    }

    StartingSetup HiddenBaseVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("hiddenbase"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("battleground")
                        || scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck")) {
                    scn.LSChooseCard(scn.GetLSCard("yavin"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Hidden Landing Site")) {
                    scn.LSChooseCard(scn.GetLSCard("hls"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Uncharted Settlements")) {
                    scn.LSChooseCard(scn.GetLSCard("uncharted"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Choose pair")
                        || scn.LSDecisionAvailable("Choose pair you may deploy")) {
                    scn.LSChoose("Rebel / Rebel starship");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
