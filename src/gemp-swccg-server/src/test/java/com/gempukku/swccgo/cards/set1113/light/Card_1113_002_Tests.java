package com.gempukku.swccgo.cards.set1113.light;

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

public class Card_1113_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chasm", "1113_2");
                    put("starting", "13_5");
                    put("shield", "13_1");
                    put("luke", "1_19");
                    put("smoke", "5_69");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tarkin", "1_179");
                    put("vader", "1_168");
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
    public void ChasmVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("chasm").getBlueprint();

        assertEquals("Chasm", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChasmVPlaysADefensiveShieldFromStartingEffectOncePerGame() {
        var scn = GetScenario();
        var chasm = scn.GetLSCard("chasm");
        var starting = scn.GetLSCard("starting");
        var shield = scn.GetLSCard("shield");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(chasm);
        scn.MoveCardsToLSSideOfTable(starting);
        scn.StackCardsOn(starting, shield);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(chasm, "Defensive Shield"));
        scn.LSUseCardAction(chasm, "Defensive Shield");
        scn.LSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(chasm, "Defensive Shield"));
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
     * Light initiates battle at the Light starting site with Luke against Tarkin and Vader, with Luke's power raised
     * so that both sides have power 10 and only battle destiny decides attrition and battle damage. Arrives at the
     * start of the battle destiny draws.
     */
    private void startEqualPowerBattleAgainstTarkinAndVader(VirtualTableScenario scn) {
        var luke = scn.GetLSCard("luke");
        var tarkin = scn.GetDSCard("tarkin");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, luke, tarkin, vader);
        // Luke (power 3) vs Tarkin (4) + Vader (6)
        scn.ApplyAdHocModifier(new PowerModifier(luke, Filters.sameCardId(luke), 7));
        scn.SkipToLSTurn(Phase.BATTLE);
    }

    private void initiateBattleAndSkipToPowerSegment(VirtualTableScenario scn) {
        var site = scn.GetLSStartingLocation();
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(10, scn.GetLSTotalPower());
        assertEquals(10, scn.GetDSTotalPower());
    }

    /**
     * Light draws one battle destiny card and Tarkin (with Vader) cancels it. Ends with both players having passed
     * the DESTINY_DRAWN responses of that draw.
     */
    private void lightDrawsBattleDestinyAndTarkinCancelsIt(VirtualTableScenario scn) {
        var tarkin = scn.GetDSCard("tarkin");

        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.DSDecisionAvailable("for battle destiny"));
        assertTrue(scn.DSCardActionAvailable(tarkin, "Cancel opponent's destiny"));
        scn.DSUseCardAction(tarkin, "Cancel opponent's destiny");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
    }

    /**
     * Light draws one battle destiny card without it being canceled. Ends with both players having passed the
     * COMPLETE_DESTINY_DRAW responses of that draw.
     */
    private void lightDrawsBattleDestinyUncanceled(VirtualTableScenario scn) {
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");
    }

    /**
     * After Light's battle destiny draws are complete, Dark declines to draw battle destiny. Ends at the
     * BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS response offered to Dark (Chasm's required trigger, if any,
     * has already fired at that point).
     */
    private void darkDeclinesBattleDestiny(VirtualTableScenario scn) {
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.DSChooseNo();
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS"));
    }

    @Test
    public void ChasmVUsesOneForTotalBattleDestinyWhenAllBattleDestiniesWereCanceled() {
        var scn = GetScenario();
        var chasm = scn.GetLSCard("chasm");
        var tarkin = scn.GetDSCard("tarkin");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(chasm);
        startEqualPowerBattleAgainstTarkinAndVader(scn);
        scn.PrepareLSDestiny(5);
        initiateBattleAndSkipToPowerSegment(scn);

        assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.LSChooseYes();
        lightDrawsBattleDestinyAndTarkinCancelsIt(scn);
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(0, scn.GetLSTotalDestiny());

        darkDeclinesBattleDestiny(scn);
        // Chasm's required trigger has fired: Light has a total battle destiny of 1 instead of nothing
        assertEquals(1, scn.GetLSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        scn.DSPass();
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.DSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(1, scn.GetLSTotalDestiny());
        assertEquals(0, scn.GetDSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.DS), scn.epsilon);
        assertEquals(1, scn.GetUnpaidDSAttrition());
        assertEquals(0, scn.GetUnpaidLSAttrition());
        scn.DSPass();
        passResponse(scn, scn.LS, "INITIAL_ATTRITION_CALCULATED");

        // Dark actually has to satisfy that attrition
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertEquals(1, scn.GetUnpaidDSAttrition());
        int lostBefore = scn.GetDSLostPileCount();
        scn.DSPayAttritionFromCardInPlay(tarkin);
        assertEquals(lostBefore + 1, scn.GetDSLostPileCount());
        assertFalse(scn.IsCardActive(tarkin));
    }

    @Test
    public void WithoutChasmVAllBattleDestiniesCanceledMeansNoTotalBattleDestinyAndNoAttrition() {
        var scn = GetScenario();

        scn.StartGame();
        startEqualPowerBattleAgainstTarkinAndVader(scn);
        scn.PrepareLSDestiny(5);
        initiateBattleAndSkipToPowerSegment(scn);

        assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.LSChooseYes();
        lightDrawsBattleDestinyAndTarkinCancelsIt(scn);
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));

        darkDeclinesBattleDestiny(scn);
        assertEquals(0, scn.GetLSTotalDestiny());
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        scn.DSPass();
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.DSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(0, scn.GetLSTotalDestiny());
        assertEquals(0, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.DS), scn.epsilon);
        assertEquals(0, scn.GetUnpaidDSAttrition());
        assertEquals(0, scn.GetUnpaidLSAttrition());
        scn.DSPass();
        passResponse(scn, scn.LS, "INITIAL_ATTRITION_CALCULATED");

        // Equal power and nothing to pay: the battle simply ends
        assertFalse(scn.IsActiveBattle());
        assertTrue(scn.AwaitingDSBattlePhaseActions());
    }

    @Test
    public void ChasmVDoesNotReplaceTotalBattleDestinyWhenOnlySomeBattleDestiniesWereCanceled() {
        var scn = GetScenario();
        var chasm = scn.GetLSCard("chasm");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(chasm);
        startEqualPowerBattleAgainstTarkinAndVader(scn);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(luke, 1, scn.LS, true));
        // Light draws 3 first (canceled by Tarkin), then 5
        scn.PrepareLSDestiny(5);
        scn.PrepareLSDestiny(3);
        initiateBattleAndSkipToPowerSegment(scn);

        assertEquals(2, scn.GetLSBattleDestinyCount());
        assertTrue(scn.LSDecisionAvailable("Do you want to draw 2 battle destiny?"));
        scn.LSChooseYes();
        lightDrawsBattleDestinyAndTarkinCancelsIt(scn);
        lightDrawsBattleDestinyUncanceled(scn);
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");
        assertEquals(2, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(5, scn.GetLSTotalDestiny());

        darkDeclinesBattleDestiny(scn);
        // Not all of Light's battle destinies were canceled, so Chasm leaves the surviving total alone
        assertEquals(5, scn.GetLSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        scn.DSPass();
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertTrue(scn.DSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(5, scn.GetLSTotalDestiny());
        assertEquals(5, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.DS), scn.epsilon);
        assertEquals(5, scn.GetUnpaidDSAttrition());
    }

    /**
     * Light has two battle destiny draws but may not draw more than one. Arrives at Light's "Do you want to draw"
     * decision with Smoke Screen in hand.
     */
    private void startLimitedTwoDrawBattle(VirtualTableScenario scn) {
        var chasm = scn.GetLSCard("chasm");
        var luke = scn.GetLSCard("luke");
        var smoke = scn.GetLSCard("smoke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(chasm);
        scn.MoveCardsToLSHand(smoke);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(luke, 1, scn.LS, true));
        scn.ApplyAdHocModifier(new MayNotDrawMoreThanBattleDestinyModifier(luke, 1, scn.LS));
        scn.SkipToLSTurn(Phase.BATTLE);
        // Only one destiny card is ever drawn in these tests (the other draw is substituted or never happens)
        scn.PrepareLSDestiny(3);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertEquals(2, scn.GetLSBattleDestinyCount());
        assertEquals(1, scn.GetLSBattleDestinyLimit());
        assertTrue(scn.LSDecisionAvailable("Do you want to draw 2 battle destiny (limited to 1 draw)?"));
    }

    @Test
    public void ChasmVSubstitutedBattleDestinyDoesNotCountAgainstBattleDestinyLimit() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var smoke = scn.GetLSCard("smoke");

        startLimitedTwoDrawBattle(scn);
        scn.LSChooseYes();
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("About to draw battle destiny"));
        assertTrue(scn.LSCardPlayAvailable(smoke, "Substitute destiny"));
        scn.LSPlayCard(smoke, "Substitute destiny");
        assertTrue(scn.LSDecisionAvailable("Choose character"));
        scn.LSChooseCard(luke);
        passResponse(scn, scn.DS, "Smoke Screen");
        passResponse(scn, scn.LS, "Smoke Screen");
        passResponse(scn, scn.DS, "PUT_IN_CARD_PILE_FROM_OFF_TABLE");
        passResponse(scn, scn.LS, "PUT_IN_CARD_PILE_FROM_OFF_TABLE");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");

        // The substituted destiny (Luke's ability of 4) did not count against the limit of 1, so a second draw starts
        assertTrue(scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        lightDrawsBattleDestinyUncanceled(scn);
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");

        assertTrue(scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertEquals(2, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(2, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(4 + 3, scn.GetLSTotalDestiny());
        scn.DSPass();
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        // Dark (a lone stormtrooper) is not offered battle destiny
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        passResponse(scn, scn.DS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        passResponse(scn, scn.LS, "BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        assertTrue(scn.DSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        assertEquals(7, scn.GetLSTotalDestiny());
        assertEquals(7, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void ChasmVDrawnBattleDestinyStillCountsAgainstBattleDestinyLimit() {
        var scn = GetScenario();
        var smoke = scn.GetLSCard("smoke");

        startLimitedTwoDrawBattle(scn);
        scn.LSChooseYes();
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("About to draw battle destiny"));
        assertTrue(scn.LSCardPlayAvailable(smoke, "Substitute destiny"));
        scn.LSDecline();
        passResponse(scn, scn.DS, "DESTINY_DRAWN");
        passResponse(scn, scn.LS, "DESTINY_DRAWN");
        passResponse(scn, scn.DS, "COMPLETE_DESTINY_DRAW");
        passResponse(scn, scn.LS, "COMPLETE_DESTINY_DRAW");

        // The drawn card counted against the limit of 1, so there is no second draw
        assertFalse(scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        passResponse(scn, scn.DS, "DRAWING_DESTINY_COMPLETE");
        passResponse(scn, scn.LS, "DRAWING_DESTINY_COMPLETE");

        assertTrue(scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(3, scn.GetLSTotalDestiny());
    }
}
