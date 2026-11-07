package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Interrupt
 * Subtype: Used
 * Title: Operational As Planned (V)
 */
public class Card1263_012 extends AbstractUsedInterrupt {
    public Card1263_012() {
        super(Side.DARK, 5, Title.Operational_As_Planned, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'We shall double our efforts.'");
        setGameText("Once per turn, if taken into hand using Protocol Failure, may immediately reveal this Interrupt to return Protocol Failure from Lost Pile to hand. Use 1 Force to take [Virtual Block 6] Grotto Werribee or an Immediate Effect into hand from Reserve Deck; reshuffle. OR Use 2 Force to take an Effect that deploys for free into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeGrottoActionId = GameTextActionId.LEGACY__OPERATIONAL_AS_PLANNED_V__TAKE_GROTTO_OR_IMMEDIATE_EFFECT;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeGrottoActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Filter cardFilter = Filters.or(Filters.and(Filters.title("Grotto Werribee"), Icon.LEGACY_BLOCK_6), Filters.Immediate_Effect);
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeGrottoActionId);
            action.setText("Take Grotto Werribee or an Immediate Effect into hand");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.allowResponses("Take [Virtual Block 6] Grotto Werribee or an Immediate Effect into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardFilter, true));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId takeEffectActionId = GameTextActionId.LEGACY__OPERATIONAL_AS_PLANNED_V__TAKE_FREE_EFFECT;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeEffectActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            final Filter cardFilter = Filters.and(CardType.EFFECT, Filters.deploysForFree);
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeEffectActionId);
            action.setText("Take an Effect that deploys for free into hand");
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.allowResponses("Take an Effect that deploys for free into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardFilter, true));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
