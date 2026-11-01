package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1113_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("solo", "8_15");
                    put("chewie", "2_3");
                }},
                new HashMap<>() {{
                    put("abyss", "1113_13");
                    put("starting", "13_69");
                    put("shield", "13_81");
                    put("tarkin", "1_179");
                    put("trooper", "1_194");
                    put("sando", "13_85");
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
    public void AbyssVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("abyss").getBlueprint();

        assertEquals("Abyss", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.DEFENSIVE_SHIELD);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AbyssVPlaysADefensiveShieldFromStartingEffectOncePerGame() {
        var scn = GetScenario();
        var abyss = scn.GetDSCard("abyss");
        var starting = scn.GetDSCard("starting");
        var shield = scn.GetDSCard("shield");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(abyss);
        scn.MoveCardsToDSSideOfTable(starting);
        scn.StackCardsOn(starting, shield);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(abyss, "Defensive Shield"));
        scn.DSUseCardAction(abyss, "Defensive Shield");
        scn.DSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());

        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(abyss, "Defensive Shield"));
    }

    /**
     * Asserts that the given player is currently being offered the given optional response, then passes it.
     */
    private static void passResponse(VirtualTableScenario scn, String player, String text) {
        assertTrue(player + " should be deciding on '" + text + "' but is deciding on: " + scn.GetAwaitingDecision(player),
                scn.DecisionAvailable(player, text));
        scn.PlayerPass(player);
    }

    /**
     * Dark initiates battle at the Dark starting site with Tarkin and a stormtrooper (ability 4, so one battle destiny)
     * against General Solo and Chewbacca (who together may cancel one of Dark's battle destinies), with Tarkin's power
     * raised so that both sides have power 11 and only battle destiny decides attrition and battle damage.
     */
    private void startEqualPowerBattleAgainstSoloAndChewie(VirtualTableScenario scn) {
        var tarkin = scn.GetDSCard("tarkin");
        var trooper = scn.GetDSCard("trooper");
        var solo = scn.GetLSCard("solo");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLocation(site, tarkin, trooper, solo, chewie);
        // Tarkin (power 4) + stormtrooper (1) vs General Solo (4) + Chewbacca (6, +1 with Han)
        scn.ApplyAdHocModifier(new PowerModifier(tarkin, Filters.sameCardId(tarkin), 6));
        scn.SkipToDSTurn(Phase.BATTLE);
    }

    private void initiateBattleAndSkipToPowerSegment(VirtualTableScenario scn) {
        var site = scn.GetDSStartingLocation();
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(11, scn.GetLSTotalPower());
        assertEquals(11, scn.GetDSTotalPower());
    }

    /**
     * Dark draws one battle destiny card and General Solo (with Chewie) uses 2 Force to cancel it. Ends with both
     * players having passed the DESTINY_DRAWN responses of that draw.
     */
    private void darkDrawsBattleDestinyAndSoloCancelsIt(VirtualTableScenario scn) {
        var solo = scn.GetLSCard("solo");

        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("for battle destiny"));
        assertTrue(scn.LSCardActionAvailable(solo, "Cancel opponent's battle destiny"));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSUseCardAction(solo, "Cancel opponent's battle destiny");
        passResponse(scn, scn.DS, "Use 2 Force");
        passResponse(scn, scn.LS, "Use 2 Force");
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
    }

    /**
     * Dark draws one battle destiny card without it being canceled. Ends with both players having passed the
     * COMPLETE_DESTINY_DRAW responses of that draw.
     */
    private void darkDrawsBattleDestinyUncanceled(VirtualTableScenario scn) {
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");
    }

    /**
     * After Dark's battle destiny draws are complete, Light declines to draw battle destiny. Ends at the
     * BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS response offered to Light (Abyss's required trigger, if any,
     * has already fired at that point).
     */
    private void lightDeclinesBattleDestiny(VirtualTableScenario scn) {
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.LSChooseNo();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS"));
    }

    @Test
    public void AbyssVUsesOneForTotalBattleDestinyWhenAllBattleDestiniesWereCanceled() {
        var scn = GetScenario();
        var abyss = scn.GetDSCard("abyss");
        var solo = scn.GetLSCard("solo");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(abyss);
        startEqualPowerBattleAgainstSoloAndChewie(scn);
        scn.PrepareDSDestiny(5);
        initiateBattleAndSkipToPowerSegment(scn);

        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.DSChooseYes();
        darkDrawsBattleDestinyAndSoloCancelsIt(scn);
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(0, scn.GetDSTotalDestiny());

        lightDeclinesBattleDestiny(scn);
        // Abyss's required trigger has fired: Dark has a total battle destiny of 1 instead of nothing
        assertEquals(1, scn.GetDSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        scn.LSPass();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(1, scn.GetDSTotalDestiny());
        assertEquals(0, scn.GetLSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon);
        assertEquals(1, scn.GetUnpaidLSAttrition());
        assertEquals(0, scn.GetUnpaidDSAttrition());
        scn.LSPass();
        passResponse(scn, scn.DS, "INITIAL_ATTRITION_CALCULATED");

        // Light actually has to satisfy that attrition
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(1, scn.GetUnpaidLSAttrition());
        int lostBefore = scn.GetLSLostPileCount();
        scn.LSPayAttritionFromCardInPlay(solo);
        assertEquals(lostBefore + 1, scn.GetLSLostPileCount());
        assertFalse(scn.IsCardActive(solo));
    }

    @Test
    public void WithoutAbyssVAllBattleDestiniesCanceledMeansNoTotalBattleDestinyAndNoAttrition() {
        var scn = GetScenario();

        scn.StartGame();
        startEqualPowerBattleAgainstSoloAndChewie(scn);
        scn.PrepareDSDestiny(5);
        initiateBattleAndSkipToPowerSegment(scn);

        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.DSChooseYes();
        darkDrawsBattleDestinyAndSoloCancelsIt(scn);
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));

        lightDeclinesBattleDestiny(scn);
        assertEquals(0, scn.GetDSTotalDestiny());
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        scn.LSPass();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(0, scn.GetDSTotalDestiny());
        assertEquals(0, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon);
        assertEquals(0, scn.GetUnpaidLSAttrition());
        assertEquals(0, scn.GetUnpaidDSAttrition());
        scn.LSPass();
        passResponse(scn, scn.DS, "INITIAL_ATTRITION_CALCULATED");

        // Equal power and nothing to pay: the battle simply ends
        assertFalse(scn.IsActiveBattle());
        assertTrue(scn.AwaitingLSBattlePhaseActions());
    }

    @Test
    public void AbyssVDoesNotReplaceTotalBattleDestinyWhenOnlySomeBattleDestiniesWereCanceled() {
        var scn = GetScenario();
        var abyss = scn.GetDSCard("abyss");
        var tarkin = scn.GetDSCard("tarkin");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(abyss);
        startEqualPowerBattleAgainstSoloAndChewie(scn);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(tarkin, 1, scn.DS, true));
        // Dark draws 3 first (canceled by General Solo), then 5
        scn.PrepareDSDestiny(5);
        scn.PrepareDSDestiny(3);
        initiateBattleAndSkipToPowerSegment(scn);

        assertEquals(2, scn.GetDSBattleDestinyCount());
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 2 battle destiny?"));
        scn.DSChooseYes();
        darkDrawsBattleDestinyAndSoloCancelsIt(scn);
        darkDrawsBattleDestinyUncanceled(scn);
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");
        assertEquals(2, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(5, scn.GetDSTotalDestiny());

        lightDeclinesBattleDestiny(scn);
        // Not all of Dark's battle destinies were canceled, so Abyss leaves the surviving total alone
        assertEquals(5, scn.GetDSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        scn.LSPass();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(5, scn.GetDSTotalDestiny());
        assertEquals(5, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon);
        assertEquals(5, scn.GetUnpaidLSAttrition());
    }

    /**
     * Dark has two battle destiny draws but may not draw more than one, and Sando Aqua Monster holds a destiny 4
     * card to substitute. Arrives at Dark's "Do you want to draw" decision.
     */
    private void startLimitedTwoDrawBattle(VirtualTableScenario scn) {
        var abyss = scn.GetDSCard("abyss");
        var sando = scn.GetDSCard("sando");
        var tarkin = scn.GetDSCard("tarkin");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(abyss, sando);
        scn.StackCardsOn(sando, scn.GetDSDestiny(4));
        scn.MoveCardsToLocation(site, tarkin, trooper, luke);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(tarkin, 1, scn.DS, true));
        scn.ApplyAdHocModifier(new MayNotDrawMoreThanBattleDestinyModifier(tarkin, 1, scn.DS));
        scn.SkipToDSTurn(Phase.BATTLE);
        // Only one destiny card is ever drawn in these tests (the other draw is substituted or never happens)
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertEquals(2, scn.GetDSBattleDestinyCount());
        assertEquals(1, scn.GetDSBattleDestinyLimit());
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 2 battle destiny (limited to 1 draw)?"));
    }

    @Test
    public void AbyssVSubstitutedBattleDestinyDoesNotCountAgainstBattleDestinyLimit() {
        var scn = GetScenario();
        var sando = scn.GetDSCard("sando");

        startLimitedTwoDrawBattle(scn);
        scn.DSChooseYes();
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.DSDecisionAvailable("About to draw battle destiny"));
        assertTrue(scn.DSCardActionAvailable(sando, "Substitute destiny"));
        scn.DSUseCardAction(sando, "Substitute destiny");
        passResponse(scn, scn.LS, "REMOVED_FROM_STACKED");
        passResponse(scn, scn.DS, "REMOVED_FROM_STACKED");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");

        // The substituted destiny (4) did not count against the limit of 1, so a second draw starts
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        darkDrawsBattleDestinyUncanceled(scn);
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");

        assertTrue(scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertEquals(2, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(2, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(4 + 3, scn.GetDSTotalDestiny());
        scn.LSPass();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.LSChooseNo();
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(7, scn.GetDSTotalDestiny());
        assertEquals(7, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void AbyssVDrawnBattleDestinyStillCountsAgainstBattleDestinyLimit() {
        var scn = GetScenario();
        var sando = scn.GetDSCard("sando");

        startLimitedTwoDrawBattle(scn);
        scn.DSChooseYes();
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.DSDecisionAvailable("About to draw battle destiny"));
        assertTrue(scn.DSCardActionAvailable(sando, "Substitute destiny"));
        scn.DSDecline();
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");

        // The drawn card counted against the limit of 1, so there is no second draw
        assertFalse(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");

        assertTrue(scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(3, scn.GetDSTotalDestiny());
    }
}
