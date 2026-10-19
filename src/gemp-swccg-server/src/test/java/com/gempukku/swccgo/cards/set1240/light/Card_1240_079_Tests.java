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

public class Card_1240_079_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1240_79");
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
    public void CorellianEngineeringCorporationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cec").getBlueprint();

        assertEquals("Corellian Engineering Corporation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorellianEngineeringCorporationDoesNotDeployWithoutWatchYourStep() {
        var scn = GetScenario();
        var cec = scn.GetLSCard("cec");

        scn.StartGame();
        scn.MoveCardsToLSHand(cec);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(cec));
    }

    @Test
    public void CorellianEngineeringCorporationDeploysWithVirtualBlock4WatchYourStep() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1240_79");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("city", "7_125");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                WatchYourStepVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var cec = scn.GetLSCard("cec");
        var wys = scn.GetLSCard("wys");

        scn.StartGame();
        assertEquals(Zone.SIDE_OF_TABLE, wys.getZone());
        scn.MoveCardsToLSHand(cec);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(cec));
        scn.LSPlayCard(cec);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, cec.getZone());
    }

    @Test
    public void CorellianEngineeringCorporationDoesNotDeployWithNonVirtualWatchYourStep() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1240_79");
                    put("wys", "10_026");
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
        var cec = scn.GetLSCard("cec");
        var wys = scn.GetLSCard("wys");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(wys);
        scn.MoveCardsToLSHand(cec);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(cec));
    }

    StartingSetup WatchYourStepVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("wys", "601_146");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("wys"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("Choose Corellia system")) {
                    scn.LSChooseCard(scn.GetLSCard("corellia"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("unpiloted freighter")) {
                    scn.LSChooseCard(scn.GetLSCard("falcon"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Corellian pilot")) {
                    scn.LSChooseCard(scn.GetLSCard("han"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Spaceport City")) {
                    scn.LSChooseCard(scn.GetLSCard("city"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("capacity slot")) {
                    scn.LSChoose("Pilot");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
