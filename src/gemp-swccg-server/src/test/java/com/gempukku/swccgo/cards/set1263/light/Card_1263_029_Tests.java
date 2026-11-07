package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_029_Tests {
    private static final String SAVE = "Cancel the attempt to make R2-D2 lost";
    private static final String CANCEL = "Lose 2 Force to cancel battle";

    private VirtualTableScenario scenario(boolean startObjective) {
        var starting = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario scn) {
                if (scn.LSDecisionAvailable("Choose starting interrupt")) {
                    scn.LSChooseCard(scn.GetLSCard("rescueInterrupt"));
                }
                if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("cell"))) scn.LSChooseCard(scn.GetLSCard("cell"));
                if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("immune"))) scn.LSChooseCard(scn.GetLSCard("immune"));
                scn.PassAllResponses();
            }
        };
        var ls = new HashMap<String, String>() {{
            put("rescueInterrupt", "1263_29"); put("otherCopy", "1263_29");
            put("cell", "601_129"); put("immune", "9_47"); put("ordinary", "1_66");
            put("hero", "2_14"); put("r2", "1271_35"); put("leia", "1_17"); put("ally", "1_28");
        }};
        if (!startObjective) ls.put("objective", "7_139");
        return new VirtualTableScenario(ls, new HashMap<>() {{
            put("gravel", "1_247"); put("vader", "1_168"); put("sense", "1_267");
        }}, 60, 60, startObjective ? StartingSetup.RescueThePrincessVObjective : StartingSetup.LSStartingLocation("1_136"),
                startObjective ? StartingSetup.DefaultDSSpaceSystem : StartingSetup.DSStartingLocation("1_295"),
                startObjective ? starting : StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, startObjective ? "anything_goes" : "legacy");
    }

    @Test public void RescueYouKeepsTheFinalLostOrStartingPrinting() {
        var scn = scenario(false); var b = scn.GetLSCard("rescueInterrupt").getBlueprint();
        assertEquals("I'm Here To Rescue You", b.getTitle()); assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet()); assertEquals(CardSubtype.LOST_OR_STARTING, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
    }

    @Test public void RescueYouActuallyStartsWithCellAndAnAlwaysImmuneEffectThenGoesToHand() {
        var scn = scenario(true); scn.StartGame(false);
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("rescue"), scn.GetLSCard("cell"), scn.GetLSCard("immune"));
        assertInHand(scn.GetLSCard("rescueInterrupt"));
        assertFalse(scn.GetLSCard("ordinary").getZone().isInPlay());
    }

    private VirtualTableScenario setup(boolean objective, boolean alone, String droid) {
        var scn = scenario(false); scn.StartGame();
        if (objective) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("objective"));
        var mos = scn.GetDSStartingLocation(); var battle = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(mos, scn.GetLSCard(droid));
        scn.MoveCardsToLocation(battle, scn.GetLSCard("leia"), scn.GetDSCard("vader"));
        if (!alone) scn.MoveCardsToLocation(battle, scn.GetLSCard("ally"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rescueInterrupt"), scn.GetLSCard("otherCopy"));
        scn.MoveCardsToDSHand(scn.GetDSCard("gravel"), scn.GetDSCard("sense"));
        scn.DSActivateForceCheat(7); scn.LSActivateForceCheat(7);
        return scn;
    }

    private void enterTurn(VirtualTableScenario scn, String player, Phase phase) {
        if (player.equals(scn.gameState().getCurrentPlayerId())) scn.SkipToPhase(phase);
        else if (player.equals(scn.DS)) scn.SkipToDSTurn(phase);
        else scn.SkipToLSTurn(phase);
        assertEquals(player, scn.gameState().getCurrentPlayerId());
        assertEquals(phase, scn.gameState().getCurrentPhase());
        scn.PassAllResponses();
        if (phase == Phase.BATTLE) {
            if (player.equals(scn.DS)) scn.DSActivateForceCheat(4); else scn.LSActivateForceCheat(4);
        }
    }

    private boolean has(VirtualTableScenario scn, String mode) {
        return scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(mode));
    }

    private void untilSaveOrResolved(VirtualTableScenario scn) {
        for (int i = 0; i < 18 && !has(scn, SAVE); i++) {
            var d = scn.GetCurrentDecision();
            if (d == null || !d.getText().toLowerCase().contains("response")) return;
            scn.PlayerPass(scn.GetAwaitingDecision(scn.LS) != null ? scn.LS : scn.DS);
        }
    }

    private VirtualTableScenario storm(boolean objective, String droid) {
        var scn = setup(objective, true, droid); enterTurn(scn, scn.DS, Phase.CONTROL);
        scn.DSActivateForceCheat(6); scn.PrepareDSDestiny(5);
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null) scn.LSPass();
        assertTrue(scn.GetDSAvailableActions().toString(), scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Target a character")));
        scn.DSPlayCard(scn.GetDSCard("gravel"), "Target a character");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard(droid))) scn.DSChooseCard(scn.GetLSCard(droid));
        untilSaveOrResolved(scn);
        return scn;
    }

    @Test public void RescueYouSavesRealNonHeroicArtooAndTheOtherCopyCannotCancelABattle() {
        var scn = storm(true, "r2"); assertTrue(has(scn, SAVE));
        scn.LSPlayCard(scn.GetLSCard("rescueInterrupt"), SAVE); scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("r2"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rescueInterrupt")); assertInHand(scn.GetLSCard("otherCopy"));
        scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(scn.GetLSStartingLocation());
        assertNotNull(scn.gameState().getBattleState()); assertFalse(has(scn, CANCEL));
    }

    @Test public void RescueYouDoesNotSaveHeroicArtooFromTheSameActualLoss() {
        var scn = storm(true, "hero");
        assertTrue(Filters.and(Keyword.HEROIC).accepts(scn.game(), scn.GetLSCard("hero")));
        assertFalse(has(scn, SAVE)); assertInZone(Zone.LOST_PILE, scn.GetLSCard("hero"));
    }

    @Test public void RescueYouCannotPreventActualArtooLossWithoutRescueThePrincess() {
        var scn = storm(false, "r2"); assertFalse(has(scn, SAVE));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("r2"));
    }

    @Test public void RescueYouLosesTwoForceToCancelActualBattleWithLeiaDefendingAlone() {
        var scn = setup(true, true, "r2"); enterTurn(scn, scn.DS, Phase.BATTLE);
        var site = scn.GetLSStartingLocation(); int lost = scn.GetLSLostPileCount();
        scn.DSInitiateBattle(site);
        assertTrue(has(scn, CANCEL)); scn.LSPlayCard(scn.GetLSCard("rescueInterrupt"), CANCEL);
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck()); scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck()); scn.PassAllResponses();
        assertEquals(lost + 3, scn.GetLSLostPileCount()); assertNull(scn.gameState().getBattleState());
        assertAtLocation(site, scn.GetLSCard("leia"), scn.GetDSCard("vader"));
    }

    @Test public void RescueYouCannotCancelAnActualBattleWhenLeiaHasAnAlly() {
        var scn = setup(true, false, "r2"); enterTurn(scn, scn.DS, Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetLSStartingLocation());
        assertNotNull(scn.gameState().getBattleState()); assertFalse(has(scn, CANCEL));
    }

    @Test public void RescueYouCannotCancelAnActualBattleThatLeiaInitiated() {
        var scn = setup(true, true, "r2"); enterTurn(scn, scn.LS, Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        assertNotNull(scn.gameState().getBattleState()); assertFalse(has(scn, CANCEL));
    }
}
