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

    @Test
    public void UnchartedSettlementsCancelsSFoilsWhenPlayed() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("sfoils", "7_75");
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
        var sfoils = scn.GetLSCard("sfoils");

        scn.StartGame();
        scn.MoveCardsToLSHand(sfoils);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(sfoils));
        scn.LSPlayCard(sfoils);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, sfoils.getZone());
    }

    @Test
    public void UnchartedSettlementsCancelsOrganizedAttackWhenPlayed() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("organized", "7_97");
                    put("xwing", "1_146");
                    put("biggs", "1_3");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
        var yavin = scn.GetLSCard("yavin");
        var organized = scn.GetLSCard("organized");
        var xwing = scn.GetLSCard("xwing");
        var biggs = scn.GetLSCard("biggs");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLSHand(organized);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.BoardAsPilot(xwing, biggs);
        scn.MoveCardsToOpponentLocation(yavin, tie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(yavin);
        scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(organized));
        scn.LSPlayUsedInterrupt(organized);
        scn.PassAllResponses();
        assertTrue(organized.getZone() == Zone.TOP_OF_LOST_PILE || organized.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void UnchartedSettlementsCancelsEllorrsMadakVWhenPlayed() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("ellorrs", "601_263");
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
        var ellorrs = scn.GetLSCard("ellorrs");

        scn.StartGame();
        scn.MoveCardsToLSHand(ellorrs);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(ellorrs));
        scn.LSPlayCard(ellorrs);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, ellorrs.getZone());
    }

    @Test
    public void UnchartedSettlementsCancelsYourDestinyWhenNonRebelPairChosen() {
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
                HiddenBaseAlienPairObjective,
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
        assertEquals(Zone.TOP_OF_LOST_PILE, destiny.getZone());
    }

    private VirtualTableScenario FlipScenario(StartingSetup objective) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("station", "1240_103");
                    put("perimeter", "1240_104");
                    put("luke", "1_19");
                    put("biggs", "1_3");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
                }},
                10, 10,
                objective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void OccupyThreeRebelCellSites(VirtualTableScenario scn) {
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(station, scn.GetLSCard("biggs"));
        scn.MoveCardsToLocation(perimeter, scn.GetLSCard("leia"));
    }

    @Test
    public void UnchartedSettlementsYourDestinyStaysAfterHiddenBaseFlipsWhenRebelPairChosen() {
        var scn = FlipScenario(HiddenBaseVObjective);
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var uncharted = scn.GetLSCard("uncharted");
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        OccupyThreeRebelCellSites(scn);
        scn.MoveCardsToDSSideOfTable(destiny);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());
        assertEquals(Zone.ATTACHED, uncharted.getZone());

        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());
        assertEquals(Zone.SIDE_OF_TABLE, destiny.getZone());
    }

    @Test
    public void UnchartedSettlementsCancelsYourDestinyAfterHiddenBaseFlipsWhenNonRebelPairChosen() {
        var scn = FlipScenario(HiddenBaseAlienPairObjective);
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var uncharted = scn.GetLSCard("uncharted");
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        OccupyThreeRebelCellSites(scn);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());
        assertEquals(Zone.ATTACHED, uncharted.getZone());

        scn.MoveCardsToDSSideOfTable(destiny);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.TOP_OF_LOST_PILE, destiny.getZone());
    }

    @Test
    public void UnchartedSettlementsMayNotDeployMaintenanceCards() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("chewie", "13_9");
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
        var chewie = scn.GetLSCard("chewie");

        scn.StartGame();
        scn.MoveCardsToLSHand(chewie);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(chewie));
    }

    @Test
    public void UnchartedSettlementsEndorForceGenerationMayNotBeCanceled() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("endor", "8_68");
                    put("planning", "9_41");
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
        var endor = scn.GetLSCard("endor");
        var planning = scn.GetLSCard("planning");
        var uncharted = scn.GetLSCard("uncharted");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveCardsToLSSideOfTable(planning);
        float withUncharted = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), endor, scn.DS);
        scn.MoveOutOfPlay(uncharted);
        float canceled = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), endor, scn.DS);
        assertTrue(Math.round(withUncharted) > 0);
        assertEquals(0, Math.round(canceled));
    }

    @Test
    public void UnchartedSettlementsNoPlus2WithLeaveThemToMe() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("atst", "3_156");
                    put("leave", "9_128");
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
        var atst = scn.GetDSCard("atst");
        var leave = scn.GetDSCard("leave");
        var start = scn.GetDSCard("starting-location");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(leave);
        float toStart = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), atst, atst, start, false, null, false, 0, null, false);
        float toHls = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), atst, atst, hls, false, null, false, 0, null, false);
        assertEquals(Math.round(toStart), Math.round(toHls));
    }

    @Test
    public void UnchartedSettlementsOpponentCombatVehicleDeploysPlus2ToRebelCellSites() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("atst", "3_156");
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
        var atst = scn.GetDSCard("atst");
        var start = scn.GetDSCard("starting-location");

        scn.StartGame();
        float toStart = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), atst, atst, start, false, null, false, 0, null, false);
        float toHls = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), atst, atst, hls, false, null, false, 0, null, false);
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

    StartingSetup HiddenBaseAlienPairObjective = new StartingSetup() {
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
                    scn.LSChoose("Alien / Independent starship");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
