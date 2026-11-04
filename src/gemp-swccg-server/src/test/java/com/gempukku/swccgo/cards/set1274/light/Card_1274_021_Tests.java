package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luckyshot", "1274_21");
                    put("luke", "1_19");
                    put("junk", "1_17");
                }},
                new HashMap<>() {{
                    put("codeclearance", "12_131");
                    put("motti", "1_164");
                    put("closecall", "601_75");
                    put("vader", "1_168");
                    put("outflank", "1230_46");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("cantina", "1_290");
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

    /**
     * Test-only Dark Side Effect: may lose (or cancel) itself, or lose another Effect, by its own game text.
     */
    private static AbstractNormalEffect selfLosingEffect(String title) {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            @Override
            protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
                List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
                TopLevelGameTextAction lose = new TopLevelGameTextAction(self, gameTextSourceCardId);
                lose.setText("Lose this Effect");
                lose.appendEffect(new LoseCardFromTableEffect(lose, self));
                actions.add(lose);
                TopLevelGameTextAction cancel = new TopLevelGameTextAction(self, gameTextSourceCardId);
                cancel.setText("Cancel this Effect");
                cancel.appendEffect(new CancelCardOnTableEffect(cancel, self));
                actions.add(cancel);
                PhysicalCard other = Filters.findFirstActive(game, self, Filters.and(Filters.Effect, Filters.not(Filters.sameCardId(self))));
                if (other != null) {
                    TopLevelGameTextAction loseOther = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    loseOther.setText("Lose other Effect");
                    loseOther.appendEffect(new LoseCardFromTableEffect(loseOther, other));
                    actions.add(loseOther);
                }
                return actions;
            }
        };
    }

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean lsCanPlay(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.LSAnyDecisionsAvailable() && !scn.GetLSAvailableActions().isEmpty() && scn.LSCardPlayAvailable(card, text);
    }

    @Test
    public void LuckyShotVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luckyshot").getBlueprint();

        assertEquals("Lucky Shot", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LuckyShotVPlacesOpponentsEffectLostByItsOwnGameTextOutOfPlay() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        scn.StartGame();
        var effect = MissionTestSupport.putOnTable(scn, 9741, scn.DS, selfLosingEffect("Self Losing Effect"));
        scn.MoveCardsToHand(luckyShot);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(effect, "Lose this Effect"));
        scn.DSUseCardAction(effect, "Lose this Effect");
        passUntil(scn, () -> lsCanPlay(scn, luckyShot, "out of play"));
        assertTrue(lsCanPlay(scn, luckyShot, "out of play"));
        scn.LSPlayCard(luckyShot, "out of play");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, effect);
        assertInZone(Zone.USED_PILE, luckyShot);
    }

    @Test
    public void LuckyShotVPlacesOpponentsEffectCanceledByItsOwnGameTextOutOfPlay() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        scn.StartGame();
        var effect = MissionTestSupport.putOnTable(scn, 9742, scn.DS, selfLosingEffect("Self Canceling Effect"));
        scn.MoveCardsToHand(luckyShot);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(effect, "Cancel this Effect");
        passUntil(scn, () -> lsCanPlay(scn, luckyShot, "out of play"));
        assertTrue(lsCanPlay(scn, luckyShot, "out of play"));
        scn.LSPlayCard(luckyShot, "out of play");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, effect);
    }

    @Test
    public void LuckyShotVDoesNotPlaceOutOfPlayAnEffectLostByAnotherCard() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        scn.StartGame();
        var loser = MissionTestSupport.putOnTable(scn, 9743, scn.DS, selfLosingEffect("Loser Effect"));
        var victim = MissionTestSupport.putOnTable(scn, 9744, scn.DS, selfLosingEffect("Victim Effect"));
        scn.MoveCardsToHand(luckyShot);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(loser, "Lose other Effect");
        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (lsCanPlay(scn, luckyShot, "out of play"))
                offered[0] = true;
            return offered[0] || scn.AwaitingDSDeployPhaseActions();
        });
        assertFalse(offered[0]);
        assertInZone(Zone.LOST_PILE, victim);
    }

    @Test
    public void LuckyShotVStopsDoTheyHaveACodeClearanceModifyingForfeitUntilEndOfTurn() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        var codeClearance = scn.GetDSCard("codeclearance");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(codeClearance);
        scn.MoveCardsToLocation(site, motti);
        scn.MoveCardsToHand(luckyShot);
        assertEquals(5 + 3, scn.GetForfeit(motti), scn.epsilon);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(luckyShot, "may not modify forfeit"));
        scn.LSPlayCard(luckyShot, "may not modify forfeit");
        scn.PassAllResponses();
        assertEquals(5, scn.GetForfeit(motti), scn.epsilon);

        scn.SkipToDSTurn();
        assertEquals(5 + 3, scn.GetForfeit(motti), scn.epsilon);
    }

    @Test
    public void LuckyShotVCancelsCloseCall() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        var closeCall = scn.GetDSCard("closecall");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToHand(luckyShot);
        scn.MoveCardsToHand(closeCall);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(site);
        passUntil(scn, () -> scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty()
                && scn.DSCardPlayAvailable(closeCall, "Cancel destiny and cause re-draw"));
        assertTrue(scn.DSCardPlayAvailable(closeCall, "Cancel destiny and cause re-draw"));
        scn.DSPlayCard(closeCall, "Cancel destiny and cause re-draw");
        passUntil(scn, () -> lsCanPlay(scn, luckyShot, "Cancel"));
        assertTrue(lsCanPlay(scn, luckyShot, "Cancel"));
        scn.LSPlayCard(luckyShot, "Cancel");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, luckyShot);
        // Close Call was canceled, so the battle destiny was not canceled and redrawn
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
    }

    @Test
    public void LuckyShotVCancelsOutflankAndMayActivateOneForce() {
        var scn = GetScenario();
        var luckyShot = scn.GetLSCard("luckyshot");
        var outflank = scn.GetDSCard("outflank");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var cantina = scn.GetDSCard("cantina");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToLocation(cantina, trooper2);
        scn.MoveCardsToHand(luckyShot);
        scn.MoveCardsToHand(outflank);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.LSInitiateBattle(site);
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardPlayAvailable(outflank));
        assertTrue(scn.DSCardPlayAvailable(outflank));
        scn.DSPlayCard(outflank);
        scn.DSChooseCard(trooper2);
        passUntil(scn, () -> lsCanPlay(scn, luckyShot, "Cancel"));
        assertTrue(lsCanPlay(scn, luckyShot, "Cancel"));
        int forcePileBefore = scn.GetLSForcePileCount();
        scn.LSPlayCard(luckyShot, "Cancel");
        passUntil(scn, () -> scn.LSDecisionAvailable("activate 1 Force"));
        assertTrue(scn.LSDecisionAvailable("activate 1 Force"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(cantina, trooper2.getAtLocation());
        assertEquals(forcePileBefore + 1, scn.GetLSForcePileCount());
    }
}
