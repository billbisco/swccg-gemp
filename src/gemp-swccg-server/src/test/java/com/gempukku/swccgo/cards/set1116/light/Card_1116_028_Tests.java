package com.gempukku.swccgo.cards.set1116.light;

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

public class Card_1116_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
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
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_16, card.getExpansionSet());
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
    public void CorellianEngineeringCorporationDeploysWithVirtualSet9WatchYourStep() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
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
                    put("cec", "1116_28");
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

    @Test
    public void CorellianEngineeringCorporationBattleDestinyPlusXWhileYouControlCorellia() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("city", "7_125");
                    put("commander", "2_17");
                    put("luke", "1_19");
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
        var city = scn.GetLSCard("city");
        var commander = scn.GetLSCard("commander");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        var dsStart = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cec);
        scn.MoveCardsToLocation(city, commander);
        scn.MoveCardsToLocation(dsStart, luke);
        scn.MoveCardsToOpponentLocation(dsStart, trooper);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dsStart);
        scn.PrepareLSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(5, scn.GetLSTotalDestiny());
    }

    @Test
    public void CorellianEngineeringCorporationBattleDestinyNotPlusXWithoutCorellia() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("city", "7_125");
                    put("commander", "2_17");
                    put("luke", "1_19");
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
        var city = scn.GetLSCard("city");
        var commander = scn.GetLSCard("commander");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");

        var dsStart = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cec);
        scn.MoveCardsToLocation(city, commander);
        scn.MoveOutOfPlay(falcon);
        scn.MoveOutOfPlay(han);
        scn.MoveCardsToLocation(dsStart, luke);
        scn.MoveCardsToOpponentLocation(dsStart, trooper);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dsStart);
        scn.PrepareLSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(4, scn.GetLSTotalDestiny());
    }

    @Test
    public void CorellianEngineeringCorporationReducesForceLossByXOncePerTurn() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("city", "7_125");
                    put("commander", "2_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("plaza", "7_270");
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
        var city = scn.GetLSCard("city");
        var commander = scn.GetLSCard("commander");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var plaza = scn.GetDSCard("plaza");
        var dsStart = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cec);
        scn.MoveCardsToLocation(city, commander);
        scn.MoveLocationToTable(plaza);
        scn.MoveCardsToLocation(dsStart, trooper);
        scn.MoveCardsToLocation(plaza, trooper2);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(dsStart);
        scn.PassResponses("FORCE_DRAIN_INITIATED");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        assertTrue(scn.LSActionAvailable("Reduce Force loss"));
        scn.LSChooseAction("Reduce Force loss");
        scn.PassResponses("optional");
        scn.PassForceDrainPendingResponses();
        scn.PassForceDrainEndResponses();
        assertEquals(lifeBefore, scn.GetLSLifeForceRemaining());
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());

        scn.DSForceDrainAt(plaza);
        scn.PassResponses("FORCE_DRAIN_INITIATED");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Reduce Force loss")));
    }

    @Test
    public void CorellianEngineeringCorporationDoesNotReduceForceLossFromYourCard() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("cec", "1116_28");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("city", "7_125");
                    put("commander", "2_17");
                    put("bones", "3_38");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
        var city = scn.GetLSCard("city");
        var commander = scn.GetLSCard("commander");
        var bones = scn.GetLSCard("bones");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var dsStart = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cec);
        scn.MoveCardsToLSSideOfTable(bones);
        scn.MoveCardsToLocation(city, commander);
        scn.MoveCardsToLocation(dsStart, luke);
        scn.MoveCardsToOpponentLocation(dsStart, vader);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dsStart);
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("optional");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_LOSE_FORCE"));
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Reduce Force loss")));
    }

    StartingSetup WatchYourStepVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("wys", "1109_24");
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
