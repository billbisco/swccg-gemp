package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.CancelPreviousBattleDestinyDrawsEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Interrupt
 * Subtype: Used
 * Title: Changing The Odds
 */
public class Card1112_003 extends AbstractUsedInterrupt {
    public Card1112_003() {
        super(Side.LIGHT, 5, Title.Changing_The_Odds, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Qui-Gon used his Jedi abilities to insure that there was no 'chance' involved with the use of Watto's chance cube.");
        setGameText("If a player is about to draw destiny for an Epic Event, Interrupt, Objective, or weapon, that player must first activate 1 Force and shuffle their Reserve Deck. OR If both players just drew one battle destiny, cancel and redraw both destiny draws.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
        AboutToDrawDestinyCardResult about = (AboutToDrawDestinyCardResult) effectResult;
        PhysicalCard source = about.getActionSource();
        String drawingPlayer = effectResult.getPerformingPlayerId();
        if (source != null && drawingPlayer != null && !about.isSubstituteDestiny()
                && Filters.or(CardType.EPIC_EVENT, CardType.INTERRUPT, CardType.OBJECTIVE, Filters.weapon).accepts(game, source)
                && GameConditions.canActivateForce(game, drawingPlayer) && GameConditions.hasReserveDeck(game, drawingPlayer)) {
            final String who = drawingPlayer;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate 1 Force and shuffle Reserve Deck");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ActivateForceEffect(action, who, 1));
                    action.appendEffect(new ShuffleReserveDeckEffect(action, who));
                }
            });
            actions.add(action);
        }
        }

        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.didBothPlayersDrawOneBattleDestiny(game)) {

        final String opponent = game.getOpponent(playerId);
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Cancel and redraw both battle destinies");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new CancelPreviousBattleDestinyDrawsEffect(action, playerId, 0));
                action.appendEffect(new CancelPreviousBattleDestinyDrawsEffect(action, opponent, 0));
                action.appendEffect(new DrawDestinyEffect(action, playerId, 1, DestinyType.BATTLE_DESTINY) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                    }
                });
                action.appendEffect(new DrawDestinyEffect(action, opponent, 1, DestinyType.BATTLE_DESTINY) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                    }
                });
            }
        });
            actions.add(action);
        }
        return actions;
    }
}
