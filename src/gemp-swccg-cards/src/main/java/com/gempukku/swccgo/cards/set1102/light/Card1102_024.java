package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardAndOrCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Interrupt
 * Subtype: Used
 * Title: Utinni!
 */
public class Card1102_024 extends AbstractUsedInterrupt {
    public Card1102_024() {
        super(Side.LIGHT, 4, Title.Utinni, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jawa trade language word for 'Come here!' Jawas work communally and scavenge for equipment.");
        setGameText("Take an Utinni Effect into hand from Reserve Deck; reshuffle. OR Take a non-unique and/or a unique (•) Jawa into hand from Reserve Deck; reshuffle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeUtinniEffect = GameTextActionId.LEGACY__UTINNI_LS_V__TAKE_UTINNI_EFFECT;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeUtinniEffect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeUtinniEffect);
            action.setText("Take Utinni Effect into hand from Reserve Deck");
            action.allowResponses("Take an Utinni Effect into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Utinni_Effect, true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId takeJawa = GameTextActionId.LEGACY__UTINNI_LS_V__TAKE_JAWA;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeJawa)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeJawa);
            action.setText("Take Jawa into hand from Reserve Deck");
            action.allowResponses("Take a non-unique and/or a unique Jawa into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardAndOrCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.and(Filters.non_unique, Filters.Jawa),
                                            Filters.and(Filters.unique, Filters.Jawa), true) {
                                        @Override
                                        public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                                            return "Choose a non-unique and/or a unique Jawa to take into hand";
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
