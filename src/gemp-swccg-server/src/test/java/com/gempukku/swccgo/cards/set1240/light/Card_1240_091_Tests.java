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

public class Card_1240_091_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("uncharted", "1240_91");
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
    public void UnchartedSettlementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("uncharted").getBlueprint();

        assertEquals("Uncharted Settlements", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.REFLECTIONS_II);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UnchartedSettlementsDoesNotDeployWithoutHiddenBase() {
        var scn = GetScenario();
        var uncharted = scn.GetLSCard("uncharted");

        scn.StartGame();
        scn.MoveCardsToLSHand(uncharted);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(uncharted));
    }

    @Test
    public void UnchartedSettlementsCancelsRevolutionOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("revolution", "1_62");
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
        var revolution = scn.GetLSCard("revolution");
        var uncharted = scn.GetLSCard("uncharted");

        scn.StartGame();
        assertEquals(Zone.ATTACHED, uncharted.getZone());
        scn.MoveCardsToLSSideOfTable(revolution);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(Zone.TOP_OF_LOST_PILE, revolution.getZone());
    }

    @Test
    public void UnchartedSettlementsDoesNotDeployOnNonVirtualHiddenBase() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("uncharted", "1240_91");
                    put("hiddenbase", "7_136");
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
        var uncharted = scn.GetLSCard("uncharted");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hiddenbase);
        scn.MoveCardsToLSHand(uncharted);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(uncharted));
    }

    @Test
    public void UnchartedSettlementsOpponentGeneratesNoForceAtHiddenLandingSite() {
        var scn = new VirtualTableScenario(
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
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        float dsGen = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), hls, scn.DS);
        assertEquals(0, Math.round(dsGen));
    }

    @Test
    public void UnchartedSettlementsYourDestinyStaysWhenRebelPairChosen() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
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
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(destiny);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(Zone.SIDE_OF_TABLE, destiny.getZone());
    }

    @Test
    public void UnchartedSettlementsMayNotDeployOperatives() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("operative", "7_13");
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
        var operative = scn.GetLSCard("operative");

        scn.StartGame();
        scn.MoveCardsToLSHand(operative);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(operative));
    }

    @Test
    public void UnchartedSettlementsOpponentCombatVehiclesDeployPlus2ToRebelCellSites() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
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
        var vader = scn.GetDSCard("vader");
        var start = scn.GetDSCard("starting-location");

        scn.StartGame();
        float toStart = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), vader, vader, start, false, null, false, 0, null, false);
        float toHls = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), vader, vader, hls, false, null, false, 0, null, false);
        assertEquals(Math.round(toStart) + 2, Math.round(toHls));
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
