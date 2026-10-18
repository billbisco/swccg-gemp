package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1240_107_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
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

    @Test
    public void HiddenBaseStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hiddenbase").getBlueprint();

        assertEquals("Hidden Base", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HiddenBaseDeploysBattlegroundSystemHiddenLandingSiteAndUnchartedSettlements() {
        var scn = GetScenario();
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var yavin = scn.GetLSCard("yavin");
        var hls = scn.GetLSCard("hls");
        var uncharted = scn.GetLSCard("uncharted");

        scn.StartGame();

        assertEquals(Zone.SIDE_OF_TABLE, hiddenbase.getZone());
        assertEquals(Zone.LOCATIONS, yavin.getZone());
        assertEquals(Zone.LOCATIONS, hls.getZone());
        assertEquals(Zone.ATTACHED, uncharted.getZone());
    }
}
