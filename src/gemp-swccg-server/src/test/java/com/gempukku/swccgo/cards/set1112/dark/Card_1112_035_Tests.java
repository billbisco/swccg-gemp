package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.common.Agenda;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("janus", "1112_35");
                    put("room", "1240_51");
                }},
                10,
                10,
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
    public void JanusGreejatusStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("janus").getBlueprint();

        assertEquals("Janus Greejatus", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.IMPERIAL_COUNCIL_MEMBER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.DEATH_STAR_II);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JanusGreejatusHasLogisticsAndRuleAndCouncilKeyword() {
        var scn = GetScenario();
        var janus = scn.GetDSCard("janus");
        var room = scn.GetDSCard("room");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, janus);

        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), janus, Agenda.LOGISTICS));
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), janus, Agenda.RULE));
        assertTrue(scn.HasKeyword(janus, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertEquals(2, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
    }

    @Test
    public void JanusGreejatusAddsTwoToDestinyForFourForce() {
        var scn = GetBattleScenario();
        var janus = scn.GetDSCard("janus");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, janus, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.DSInitiateBattle(room);
        scn.PassAllResponses();
        drawDsBattleDestinyAndStopAtDrawn(scn, 1);
        assertTrue("Add 2 missing. ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions()
                        + " log=" + String.join(" | ", scn.gameState().getLastMessages()),
                scn.DSCardActionAvailable(janus, "Add 2 to destiny"));
        scn.DSUseCardAction(janus, "Add 2 to destiny");
        scn.PassAllResponses();
        assertEquals(3, scn.GetDSTotalDestiny());
    }

    @Test
    public void JanusGreejatusCancelsAndRedrawsDestinyForTwoForce() {
        var scn = GetBattleScenario();
        var janus = scn.GetDSCard("janus");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, janus, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(room);
        scn.PassAllResponses();
        drawDsBattleDestinyAndStopAtDrawn(scn, 1);
        assertTrue("Cancel and redraw missing. ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " log=" + String.join(" | ", scn.gameState().getLastMessages()),
                scn.DSCardActionAvailable(janus, "Cancel and redraw destiny"));
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(janus, "Cancel and redraw destiny");
        scn.PassAllResponses();
        assertEquals(5, scn.GetDSTotalDestiny());
    }

    @Test
    public void JanusGreejatusDoesNotOfferWhenDestinyIsNotLessThanInfluence() {
        var scn = GetBattleScenario();
        var janus = scn.GetDSCard("janus");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, janus, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.DSInitiateBattle(room);
        scn.PassAllResponses();
        drawDsBattleDestinyAndStopAtDrawn(scn, 2);
        assertFalse(scn.DSCardActionAvailable(janus, "Add 2 to destiny"));
        assertFalse(scn.DSCardActionAvailable(janus, "Cancel and redraw destiny"));
        scn.PassAllResponses();
        assertEquals(2, scn.GetDSTotalDestiny());
    }

    @Test
    public void JanusGreejatusDestinyModifyIsOncePerTurn() {
        var scn = GetBattleScenario();
        var janus = scn.GetDSCard("janus");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, janus, trooper, luke);
        scn.AttachCardsTo(trooper, blaster);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(5);
        scn.PrepareDSDestiny(1);
        scn.DSInitiateBattle(room);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        passCostAndAboutToDrawThenLsDestinyDrawn(scn);
        assertTrue("Add 2 missing on weapon destiny. ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " text=" + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText())
                        + " actions=" + scn.GetDSAvailableActions()
                        + " log=" + String.join(" | ", scn.gameState().getLastMessages()),
                scn.DSCardActionAvailable(janus, "Add 2 to destiny"));
        scn.DSUseCardAction(janus, "Add 2 to destiny");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        drawDsBattleDestinyAndStopAtDrawn(scn, 1);
        assertFalse(scn.DSCardActionAvailable(janus, "Add 2 to destiny"));
        assertFalse(scn.DSCardActionAvailable(janus, "Cancel and redraw destiny"));
    }

    private void drawDsBattleDestinyAndStopAtDrawn(VirtualTableScenario scn, int destiny) {
        scn.PrepareDSDestiny(destiny);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue("expected battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        passCostAndAboutToDrawThenLsDestinyDrawn(scn);
    }

    /**
     * Card_14_003: after DS draws, COST then ABOUT_TO are LS/DS/LS/DS, which lands on LS DESTINY_DRAWN.
     * Janus is DS's own just-drawn optional, so one more LSPass reaches the DS window.
     */
    private void passCostAndAboutToDrawThenLsDestinyDrawn(VirtualTableScenario scn) {
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
    }

    private VirtualTableScenario GetBattleScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("janus", "1112_35");
                    put("room", "1240_51");
                    put("blaster", "1_317");
                    put("trooper", "1_194");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }
}
