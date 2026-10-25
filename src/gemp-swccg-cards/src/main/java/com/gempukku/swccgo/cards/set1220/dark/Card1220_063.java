package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Probe Telemetry
 */
public class Card1220_063 extends AbstractUsedInterrupt {
    public Card1220_063() {
        super(Side.DARK, 4, Title.Probe_Telemetry, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Probe droids use electromagnetic, seismic, acoustic, olfactory and optical sensors. They report their findings using an omnisignal unicode.");
        setGameText("Take a Probe Droid into hand from Reserve Deck; reshuffle. OR Place a non-unique card from a Lost Pile out of play.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);
        GameTextActionId takeId = GameTextActionId.LEGACY__PROBE_TELEMETRY_V__TAKE_PROBE_DROID;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take Probe Droid into hand");
            action.allowResponses("Take a Probe Droid into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Probe_Droid, true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place non-unique card out of play");
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.LOST_PILE) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.allowResponses("Place a non-unique card from " + cardPileOwner + "'s Lost Pile out of play",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, cardPileOwner, Filters.non_unique, false));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
