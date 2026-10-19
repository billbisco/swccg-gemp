package com.gempukku.swccgo.cards.set1281.light;

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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("charm", "1281_35");
                    put("lando", "601_50");
                    put("leia", "1_17");
                    put("peek1", "1_11");
                    put("peek2", "1_19");
                    put("peek3", "1_27");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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

    @Test
    public void ScoundrelsCharmStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("charm").getBlueprint();

        assertEquals("Scoundrel's Charm", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SCOUNDREL);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsCharmPeeksAtTopThreeOfForcePileTakesOneAndReshufflesOncePerTurn() {
        var scn = GetScenario();
        var charm = scn.GetLSCard("charm");
        var lando = scn.GetLSCard("lando");
        var peek1 = scn.GetLSCard("peek1");
        var peek2 = scn.GetLSCard("peek2");
        var peek3 = scn.GetLSCard("peek3");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, charm);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSForcePile(peek3, peek2, peek1);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(charm, "Peek at top 3 cards of Force Pile"));
        scn.LSUseCardAction(charm, "Peek at top 3 cards of Force Pile");
        scn.LSDecided("    0");
        scn.PassAllResponses();

        assertEquals(Zone.HAND, peek1.getZone());
        assertTrue(peek2.getZone() == Zone.FORCE_PILE || peek2.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertTrue(peek3.getZone() == Zone.FORCE_PILE || peek3.getZone() == Zone.TOP_OF_FORCE_PILE);
        String peekLog = String.join(" | ", scn.gameState().getLastMessages());
        assertTrue("expected Force Pile reshuffle, log=" + peekLog, peekLog.toLowerCase().contains("shuffles force pile"));
        recoverLsPhaseActions(scn);
        assertTrue("once-per-turn must be checked in a card-action window. ls=" + scn.LSGetDecision()
                        + " ds=" + scn.DSGetDecision(),
                scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(charm, "Peek at top 3 cards of Force Pile"));
    }

    @Test
    public void ScoundrelsCharmAddsOneToOpponentsJustDrawnDestiny() {
        var scn = GetScenario();
        var charm = scn.GetLSCard("charm");
        var lando = scn.GetLSCard("lando");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, vader);
        scn.AttachCardsTo(lando, charm);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(vader, 1, scn.DS, true));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(1);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue("expected battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("battle destiny?"));
        assertEquals(2, scn.GetDSBattleDestinyCount());
        scn.DSChooseYes();
        stopAtLsDestinyDrawn(scn);
        int forceBeforeAdd = scn.GetLSForcePileCount();
        assertTrue(forceBeforeAdd >= 1);
        assertTrue("Add missing. ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.LSCardActionAvailable(charm, "Add 1 to opponent's destiny"));
        scn.LSUseCardAction(charm, "Add 1 to opponent's destiny");
        scn.PassForceUseResponses();
        String addLog = String.join(" | ", scn.gameState().getLastMessages());
        assertTrue("Charm add missing, log=" + addLog, addLog.contains("adds 1 to make the battle destiny"));
        assertEquals(forceBeforeAdd - 1, scn.GetLSForcePileCount());
        finishCurrentDestinyDraw(scn);
        stopAtLsDestinyDrawn(scn);
        assertTrue("second DS destiny must be an LS card-action window. ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision() + " actions=" + scn.GetLSAvailableActions(),
                scn.LSGetDecision() != null);
        assertFalse(scn.LSCardActionAvailable(charm, "Add 1 to opponent's destiny"));
        assertFalse(scn.LSCardActionAvailable(charm, "Subtract 1 from opponent's destiny"));
    }

    @Test
    public void ScoundrelsCharmSubtractsOneFromOpponentsJustDrawnDestiny() {
        var scn = GetScenario();
        var charm = scn.GetLSCard("charm");
        var lando = scn.GetLSCard("lando");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, vader);
        scn.AttachCardsTo(lando, charm);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        drawDsBattleDestinyAndStopAtLsDrawn(scn, 5);
        int forceBeforeSub = scn.GetLSForcePileCount();
        assertTrue(forceBeforeSub >= 1);
        assertTrue(scn.LSCardActionAvailable(charm, "Subtract 1 from opponent's destiny"));
        scn.LSUseCardAction(charm, "Subtract 1 from opponent's destiny");
        scn.PassForceUseResponses();
        scn.PassResponses("optional");
        scn.PassAllResponses();
        String subLog = String.join(" | ", scn.gameState().getLastMessages());
        assertTrue("Charm subtract missing, log=" + subLog, subLog.contains("subtracts 1 to make the battle destiny"));
        assertEquals("ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision() + " log=" + subLog,
                5, scn.GetDSTotalDestiny());
        assertEquals(forceBeforeSub - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void ScoundrelsCharmDoesNotOfferOnOwnDestiny() {
        var scn = GetScenario();
        var charm = scn.GetLSCard("charm");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, leia, trooper);
        scn.AttachCardsTo(lando, charm);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue("expected LS battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("battle destiny?"));
        scn.PrepareLSDestiny(2);
        scn.LSChooseYes();
        stopAtLsDestinyDrawn(scn);
        assertTrue("own destiny must be an LS card-action window. ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision() + " actions=" + scn.GetLSAvailableActions(),
                scn.LSGetDecision() != null);
        assertFalse(scn.LSCardActionAvailable(charm, "Add 1 to opponent's destiny"));
        assertFalse(scn.LSCardActionAvailable(charm, "Subtract 1 from opponent's destiny"));
    }

    private void drawDsBattleDestinyAndStopAtLsDrawn(VirtualTableScenario scn, int destiny) {
        scn.PrepareDSDestiny(destiny);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue("expected battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        stopAtLsDestinyDrawn(scn);
    }

    private void stopAtLsDestinyDrawn(VirtualTableScenario scn) {
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.DSPass();
        assertTrue("expected LS destiny-drawn window, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.LSGetDecision() != null);
    }

    private void finishCurrentDestinyDraw(VirtualTableScenario scn) {
        assertTrue("expected DS leftover optional after Charm, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("optional"));
        scn.DSPass();
        scn.LSPass();
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.PassResponses("DRAWING_DESTINY_COMPLETE");
    }

    private void recoverLsPhaseActions(VirtualTableScenario scn) {
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue("expected LS phase actions, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.AwaitingLSControlPhaseActions());
    }
}
