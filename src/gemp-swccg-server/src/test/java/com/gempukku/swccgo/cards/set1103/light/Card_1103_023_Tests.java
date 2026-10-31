package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("traffic", "1103_23");
                    put("unusual", "13_5");
                    put("s1", "1200_7");
                    put("s2", "1200_8");
                    put("s3", "1200_10");
                    put("s4", "1200_11");
                    put("boonta", "11_24");
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("obi", "1_21");
                    put("cantina", "1_128");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("wattos", "11_75");
                    put("storm", "1_194");
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

    protected VirtualTableScenario GetPlayScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("traffic", "1103_23");
                    put("unusual", "13_5");
                    put("s1", "13_1");
                    put("s2", "13_3");
                    put("s3", "13_4");
                    put("s4", "13_6");
                }},
                new HashMap<>() {{
                    put("storm", "1_194");
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
    public void TrafficControlVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("traffic").getBlueprint();

        assertEquals("Traffic Control", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TrafficControlVTreatsThreeOnAnUnusualAmountOfFearAsFour() {
        var scn = GetScenario();
        var traffic = scn.GetLSCard("traffic");
        var unusual = scn.GetLSCard("unusual");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(unusual);
        scn.MoveCardsToLSSideOfTable(traffic);

        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), unusual,
                ModifyGameTextType.LEGACY__AN_UNUSUAL_AMOUNT_OF_FEAR__THREE_IS_FOUR));
    }

    @Test
    public void TrafficControlVLetsUnusualPlayAFourthStackedDefensiveShield() {
        var scn = GetPlayScenario();
        var traffic = scn.GetLSCard("traffic");
        var unusual = scn.GetLSCard("unusual");
        var s1 = scn.GetLSCard("s1");
        var s2 = scn.GetLSCard("s2");
        var s3 = scn.GetLSCard("s3");
        var s4 = scn.GetLSCard("s4");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(unusual);
        scn.MoveCardsToLSSideOfTable(traffic);
        scn.StackCardsOn(unusual, s1, s2, s3, s4);
        awaitLSControl(scn);

        playStackedShield(scn, unusual, s1);
        playStackedShield(scn, unusual, s2);
        playStackedShield(scn, unusual, s3);
        playStackedShield(scn, unusual, s4);
        assertFalse(scn.LSCardActionAvailable(unusual, "Defensive Shield"));
    }

    @Test
    public void TrafficControlVUnusualCannotPlayAFourthStackedShieldWithoutIt() {
        var scn = GetPlayScenario();
        var unusual = scn.GetLSCard("unusual");
        var s1 = scn.GetLSCard("s1");
        var s2 = scn.GetLSCard("s2");
        var s3 = scn.GetLSCard("s3");
        var s4 = scn.GetLSCard("s4");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(unusual);
        scn.StackCardsOn(unusual, s1, s2, s3, s4);
        awaitLSControl(scn);

        playStackedShield(scn, unusual, s1);
        playStackedShield(scn, unusual, s2);
        playStackedShield(scn, unusual, s3);
        assertFalse(scn.LSCardActionAvailable(unusual, "Defensive Shield"));
        assertFalse(s4.getZone().isInPlay());
    }

    @Test
    public void TrafficControlVOwnerLosesNoForceToBoontaAndOpponentRetrievalMayNotBeCanceled() {
        var scn = GetScenario();
        var traffic = scn.GetLSCard("traffic");
        var boonta = scn.GetLSCard("boonta");
        var wattos = scn.GetDSCard("wattos");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(traffic);
        scn.MoveCardsToLSSideOfTable(boonta);
        scn.MoveCardsToDSSideOfTable(wattos);

        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.LS, boonta), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), boonta, scn.DS));
        assertFalse(scn.game().getModifiersQuerying().isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), wattos, scn.DS));
    }

    @Test
    public void TrafficControlVSuspendsWattosBoxWhileOccupyingThreeBattlegrounds() {
        var scn = GetScenario();
        var traffic = scn.GetLSCard("traffic");
        var wattos = scn.GetDSCard("wattos");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var obi = scn.GetLSCard("obi");
        var cantina = scn.GetLSCard("cantina");
        var farm = scn.GetLSCard("farm");
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(traffic);
        scn.MoveCardsToDSSideOfTable(wattos);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(chasm, luke);
        scn.MoveCardsToLocation(cantina, leia);
        AdvanceTableChanged(scn);
        assertFalse(wattos.isSuspended());

        scn.MoveCardsToLocation(farm, obi);
        AdvanceTableChanged(scn);
        assertTrue(wattos.isSuspended());
    }

    private void awaitLSControl(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.CONTROL);
        awaitLSControlWithoutSkippingTurn(scn);
    }

    private void awaitLSControlWithoutSkippingTurn(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) {
            if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable() && !scn.AwaitingLSControlPhaseActions()) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    private void playStackedShield(VirtualTableScenario scn, PhysicalCardImpl starting, PhysicalCardImpl shield) {
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(starting, "Defensive Shield"));
        scn.LSUseCardAction(starting, "Defensive Shield");
        scn.LSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
        awaitLSControlWithoutSkippingTurn(scn);
    }

    private void AdvanceTableChanged(VirtualTableScenario scn) {
        if (scn.gameState().getCurrentPhase() == Phase.DEPLOY) {
            scn.SkipToPhase(Phase.BATTLE);
        } else {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }
}
