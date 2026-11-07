package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInCardPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Final Legacy master list, page 33 (destiny 6; earlier full card has 4). */
public class Card1263_027 extends AbstractUsedInterrupt {
    private static final GameTextActionId EXCHANGE = GameTextActionId.LEGACY__DESPERATE_TACTICS__EXCHANGE_PERSONA;

    public Card1263_027() {
        super(Side.LIGHT, 6, "Desperate Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Members of the Rebel Alliance are willing to make a perilous frontal attack for their cause.");
        setGameText("If opponent just initiated battle, lose 1 Force to target a character present. Target is power +2 and loses all immunity to attrition this turn. OR Once per game, exchange a character in hand with a character of the same persona from Lost Pile or Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if (!TriggerConditions.battleInitiated(game, result, game.getOpponent(playerId))) return null;
        Filter target = Filters.and(Filters.character, Filters.presentAt(Filters.and(game.getGameState().getBattleState().getBattleLocation())));
        if (!GameConditions.canTarget(game, self, target)) return null;

        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Lose 1 Force to boost a character and cancel its attrition immunity");
        action.appendCost(new LoseForceEffect(action, playerId, 1, true));
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a character present at the battle", target) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard character = action.getPrimaryTargetCard(targetGroupId);
                        action.appendEffect(new ModifyPowerUntilEndOfTurnEffect(action, character, 2));
                        action.appendEffect(new CancelImmunityToAttritionUntilEndOfTurnEffect(action, character, null));
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.isOncePerGame(game, self, EXCHANGE) || !GameConditions.hasInHand(game, playerId, Filters.character)) return null;
        List<PlayInterruptAction> actions = new ArrayList<>();
        if (GameConditions.canSearchLostPile(game, playerId, self, EXCHANGE)) actions.add(exchange(playerId, game, self, Zone.LOST_PILE));
        if (GameConditions.canSearchReserveDeck(game, playerId, self, EXCHANGE)) actions.add(exchange(playerId, game, self, Zone.RESERVE_DECK));
        return actions;
    }

    private PlayInterruptAction exchange(String playerId, SwccgGame game, PhysicalCard self, Zone pile) {
        PlayInterruptAction action = new PlayInterruptAction(game, self, EXCHANGE);
        action.setText("Exchange a character with the same persona in " + pile.getHumanReadable());
        action.appendUsage(new OncePerGameEffect(action));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new ChooseCardFromHandEffect(action, playerId, Filters.character) {
                    @Override
                    protected void cardSelected(SwccgGame game, PhysicalCard cardInHand) {
                        Filter matchingCharacter = Filters.and(Filters.character, Filters.samePersonaAs(cardInHand));
                        // Unlike the default Lost Pile wrapper, both printed routes reshuffle the searched pile.
                        action.appendEffect(new ExchangeCardsInHandWithCardInCardPileEffect(action, playerId, pile, 1, 1,
                                cardInHand, matchingCharacter, true) {});
                    }
                });
            }
        });
        return action;
    }
}
