package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AttemptingPersonaReplaceCharacterResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Interrupt
 * Subtype: Used
 * Title: Lando System?
 */
public class Card1105_042 extends AbstractUsedInterrupt {
    public Card1105_042() {
        super(Side.DARK, 4, Title.Lando_System, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Lando's not a system, he's a man.' Regardless of where Rebels flee, dark agents are sure to follow.");
        setGameText("Take Lando or a card with 'Lando' in game text into hand from Reserve Deck; reshuffle. OR Cancel an attempt to replace your Lando; opponent's Lando is lost.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LANDO_SYSTEM_V__TAKE_LANDO;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Lando or 'Lando' card into hand");
            action.allowResponses("Take Lando or a card with 'Lando' in game text into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.or(Filters.Lando, Filters.gameTextContains("Lando")), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.attemptingPersonaReplaceCharacter(game, effectResult, Filters.and(Filters.your(self), Filters.Lando))) {
            final AttemptingPersonaReplaceCharacterResult result = (AttemptingPersonaReplaceCharacterResult) effectResult;
            final PhysicalCard oldLando = result.getOldCharacter();
            final PhysicalCard newLando = result.getNewCharacter();
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel replace; opponent's Lando is lost");
            action.addAnimationGroup(oldLando);
            action.allowResponses("Cancel attempt to replace " + GameUtils.getCardLink(oldLando),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PreventEffectOnCardEffect(action, result.getPreventableCardEffect(), oldLando,
                                            "Cancel attempt to replace " + GameUtils.getCardLink(oldLando)));
                            if (newLando != null && Filters.and(Filters.opponents(self), Filters.Lando).accepts(game, newLando)) {
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, newLando));
                            }
                            else if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(playerId), Filters.Lando))) {
                                PhysicalCard opponentsLando = Filters.findFirstActive(game, self, Filters.and(Filters.opponents(self), Filters.Lando));
                                if (opponentsLando != null) {
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, opponentsLando));
                                }
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
