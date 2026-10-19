package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SystemsWillSlipThroughYourFingersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetLSCard("hiddenbase").getOtherSideBlueprint();

        assertEquals("Systems Will Slip Through Your Fingers", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
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

    @Test
    public void HiddenBaseRebelPairMayNotDeployAlienWithAbility() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("boshek", "1_4");
                    put("luke", "1_19");
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
        var boshek = scn.GetLSCard("boshek");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSHand(boshek, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(boshek));
        assertTrue(scn.LSCardPlayAvailable(luke));
    }

    @Test
    public void HiddenBaseDeploysBattlegroundSystemOncePerTurn() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("corellia", "2_61");
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
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var corellia = scn.GetLSCard("corellia");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(corellia);

        assertTrue(scn.LSCardActionAvailable(hiddenbase, "Deploy battleground system from Reserve Deck"));
        scn.LSUseCardAction(hiddenbase, "Deploy battleground system from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(corellia)) {
            scn.LSChooseCard(corellia);
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, corellia.getZone());
        if (!scn.AwaitingLSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(hiddenbase, "Deploy battleground system from Reserve Deck"));
    }

    @Test
    public void HiddenBaseDeploysRebelCellSiteAndCharacterSameTurn() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("station", "1240_103");
                    put("biggs", "1_3");
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
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var station = scn.GetLSCard("station");
        var biggs = scn.GetLSCard("biggs");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(station, biggs);

        assertTrue(scn.LSCardActionAvailable(hiddenbase, "Deploy Rebel Cell site from Reserve Deck"));
        scn.LSUseCardAction(hiddenbase, "Deploy Rebel Cell site from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(station)) {
            scn.LSChooseCard(station);
        }
        if (scn.LSHasCardChoiceAvailable(hls)) {
            scn.LSChooseCard(hls);
        }
        if (scn.LSDecisionAvailable("On which side")) {
            scn.LSChoose("Left");
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, station.getZone());
        if (!scn.AwaitingLSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());

        assertTrue(scn.LSCardActionAvailable(hiddenbase, "Deploy character of ability 2 or 3 to a Rebel Cell site"));
        scn.LSUseCardAction(hiddenbase, "Deploy character of ability 2 or 3 to a Rebel Cell site");
        if (scn.LSHasCardChoiceAvailable(biggs)) {
            scn.LSChooseCard(biggs);
        }
        if (scn.LSHasCardChoiceAvailable(hls)) {
            scn.LSChooseCard(hls);
        }
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, biggs.getZone());
    }

    @Test
    public void HiddenBaseFlipsWhenYouOccupyThreeRebelCellSites() {
        var scn = new VirtualTableScenario(
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
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, luke);
        scn.MoveCardsToLocation(station, biggs);
        scn.MoveCardsToLocation(perimeter, leia);
        assertEquals(Zone.LOCATIONS, station.getZone());
        assertEquals(Zone.LOCATIONS, perimeter.getZone());
        assertEquals(Zone.AT_LOCATION, luke.getZone());
        assertEquals(Zone.AT_LOCATION, biggs.getZone());
        assertEquals(Zone.AT_LOCATION, leia.getZone());
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());
    }

    @Test
    public void HiddenBaseBackForceDrainsPlus1AndAbility2IsImmuneToAttritionLessThan4() {
        var scn = new VirtualTableScenario(
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
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, luke);
        scn.MoveCardsToLocation(station, biggs);
        scn.MoveCardsToLocation(perimeter, leia);

        float drainBefore = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), yavin, scn.LS);
        scn.SkipToPhase(Phase.CONTROL);
        float drainAfter = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), yavin, scn.LS);
        assertEquals(Math.round(drainBefore) + 1, Math.round(drainAfter));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), biggs), scn.epsilon);
    }

    @Test
    public void HiddenBaseBackFlipsWhenOpponentControlsTwoRebelCellSites() {
        var scn = new VirtualTableScenario(
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
                    put("trooper2", "1_194");
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
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, luke);
        scn.MoveCardsToLocation(station, biggs);
        scn.MoveCardsToLocation(perimeter, leia);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());

        scn.MoveOutOfPlay(luke);
        scn.MoveOutOfPlay(biggs);
        scn.MoveCardsToOpponentLocation(hls, trooper);
        scn.MoveCardsToOpponentLocation(station, trooper2);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals("Hidden Base", hiddenbase.getBlueprint().getTitle());
    }
}
