package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("didit", "11_25");
                    put("luke", "1_19");
                    put("cantina", "1_128");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("terminal", "1103_42");
                    put("fear", "13_69");
                    put("s1", "13_81");
                    put("s2", "13_54");
                    put("s3", "13_84");
                    put("s4", "13_51");
                    put("boonta", "11_79");
                    put("storm1", "1_194");
                    put("storm2", "1_194");
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
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("terminal", "1103_42");
                    put("fear", "13_69");
                    put("s1", "13_81");
                    put("s2", "13_54");
                    put("s3", "13_84");
                    put("s4", "13_51");
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
    public void ReactorTerminalVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("terminal").getBlueprint();

        assertEquals("Reactor Terminal", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void ReactorTerminalVTreatsThreeOnFearIsMyAllyAsFour() {
        var scn = GetScenario();
        var terminal = scn.GetDSCard("terminal");
        var fear = scn.GetDSCard("fear");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fear);
        scn.MoveCardsToDSSideOfTable(terminal);

        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), fear,
                ModifyGameTextType.LEGACY__FEAR_IS_MY_ALLY__THREE_IS_FOUR));
    }

    @Test
    public void ReactorTerminalVLetsFearPlayAFourthStackedDefensiveShield() {
        var scn = GetPlayScenario();
        var terminal = scn.GetDSCard("terminal");
        var fear = scn.GetDSCard("fear");
        var s1 = scn.GetDSCard("s1");
        var s2 = scn.GetDSCard("s2");
        var s3 = scn.GetDSCard("s3");
        var s4 = scn.GetDSCard("s4");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fear);
        scn.MoveCardsToDSSideOfTable(terminal);
        scn.StackCardsOn(fear, s1, s2, s3, s4);
        awaitDSControl(scn);

        playStackedShield(scn, fear, s1);
        playStackedShield(scn, fear, s2);
        playStackedShield(scn, fear, s3);
        playStackedShield(scn, fear, s4);
        assertFalse(scn.DSCardActionAvailable(fear, "Defensive Shield"));
    }

    @Test
    public void ReactorTerminalVFearCannotPlayAFourthStackedShieldWithoutIt() {
        var scn = GetPlayScenario();
        var fear = scn.GetDSCard("fear");
        var s1 = scn.GetDSCard("s1");
        var s2 = scn.GetDSCard("s2");
        var s3 = scn.GetDSCard("s3");
        var s4 = scn.GetDSCard("s4");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fear);
        scn.StackCardsOn(fear, s1, s2, s3, s4);
        awaitDSControl(scn);

        playStackedShield(scn, fear, s1);
        playStackedShield(scn, fear, s2);
        playStackedShield(scn, fear, s3);
        assertFalse(scn.DSCardActionAvailable(fear, "Defensive Shield"));
        assertFalse(s4.getZone().isInPlay());
    }

    @Test
    public void ReactorTerminalVOwnerLosesNoForceToBoontaAndOpponentRetrievalMayNotBeCanceled() {
        var scn = GetScenario();
        var terminal = scn.GetDSCard("terminal");
        var boonta = scn.GetDSCard("boonta");
        var didit = scn.GetLSCard("didit");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(terminal);
        scn.MoveCardsToDSSideOfTable(boonta);
        scn.MoveCardsToLSSideOfTable(didit);

        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.DS, boonta), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), boonta, scn.LS));
        assertFalse(scn.game().getModifiersQuerying().isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), didit, scn.LS));
    }

    @Test
    public void ReactorTerminalVSuspendsIDidItWhileOccupyingMoreBattlegrounds() {
        var scn = GetScenario();
        var terminal = scn.GetDSCard("terminal");
        var didit = scn.GetLSCard("didit");
        var storm1 = scn.GetDSCard("storm1");
        var storm2 = scn.GetDSCard("storm2");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSCard("cantina");
        var farm = scn.GetLSCard("farm");
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(terminal);
        scn.MoveCardsToLSSideOfTable(didit);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(cantina, storm1);
        scn.MoveCardsToLocation(chasm, luke);
        AdvanceTableChanged(scn);
        assertFalse(didit.isSuspended());

        scn.MoveCardsToLocation(farm, storm2);
        AdvanceTableChanged(scn);
        assertTrue(didit.isSuspended());
    }

    private void awaitDSControl(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.CONTROL);
        awaitDSControlWithoutSkippingTurn(scn);
    }

    private void awaitDSControlWithoutSkippingTurn(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    private void playStackedShield(VirtualTableScenario scn, PhysicalCardImpl starting, PhysicalCardImpl shield) {
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(starting, "Defensive Shield"));
        scn.DSUseCardAction(starting, "Defensive Shield");
        scn.DSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
        awaitDSControlWithoutSkippingTurn(scn);
    }

    private void AdvanceTableChanged(VirtualTableScenario scn) {
        if (scn.gameState().getCurrentPhase() == Phase.DEPLOY) {
            scn.SkipToPhase(Phase.BATTLE);
        } else {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }
}
