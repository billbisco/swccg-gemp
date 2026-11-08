package com.gempukku.swccgo.cards.set2220.light;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.timing.results.PutCardToHandFromOffTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Shocking Information
 * Original version of 1220_153 (Virtual Block 2 release sheet, Dec 2009): "if opponent just [upload] or [download] a character"; same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_153 extends AbstractUsedInterrupt {
    public Card2220_153() {
        super(Side.LIGHT, 5, Title.Shocking_Information, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'R2-D2, you know better than to trust a strange computer.'");
        setGameText("Unless you have a senate majority, if opponent just [upload] or [download] a character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible).");
        setLegacyVersion(new LegacyVersion("1220_153", "",
                "Virtual Block 2 release sheet (2009-12-07), card #153; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        addIcons(Icon.CLOUD_CITY);
    }

    private boolean deployedWithMatchingEffect(SwccgGame game) {
        for (PlayCardState state : game.getGameState().getPlayCardStates()) {
            if (state.getPlayCardAction() == null) {
                continue;
            }
            PhysicalCard source = state.getPlayCardAction().getActionSource();
            if (source != null && Filters.and(Filters.Effect, Filters.gameTextContains("matching")).accepts(game, source)) {
                return true;
            }
        }
        return false;
    }

    private PlayInterruptAction placeTwoCardsAction(SwccgGame game, final String opponent, final PhysicalCard self, final Filter excludeFilter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Place 2 cards from hand on Used Pile");
        action.allowResponses("Opponent places 2 other cards from hand on Used Pile",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new PutCardsFromHandOnUsedPileEffect(action, opponent, 0, 2, excludeFilter, false));
                    }
                }
        );
        return action;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (GameConditions.hasSenateMajority(game, playerId)) {
            return null;
        }

        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.character)
                && effectResult instanceof PlayCardResult
                && ((PlayCardResult) effectResult).getPlayedCardFrom() == Zone.RESERVE_DECK
                && !deployedWithMatchingEffect(game)
                && GameConditions.hasHand(game, opponent)) {
            return Collections.singletonList(placeTwoCardsAction(game, opponent, self, Filters.any));
        }

        if (effectResult.getType() == EffectResult.Type.PUT_TO_HAND_FROM_OFF_TABLE
                && opponent.equals(effectResult.getPerformingPlayerId())) {
            PhysicalCard card = ((PutCardToHandFromOffTableResult) effectResult).getCard();
            if (card != null && Filters.character.accepts(game, card) && GameConditions.hasHand(game, opponent)) {
                return Collections.singletonList(placeTwoCardsAction(game, opponent, self, Filters.not(card)));
            }
        }
        return null;
    }
}
