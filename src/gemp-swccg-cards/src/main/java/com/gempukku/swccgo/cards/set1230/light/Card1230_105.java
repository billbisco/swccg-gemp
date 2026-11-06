package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Ke Chu Ke Kukuta? (V)
 */
public class Card1230_105 extends AbstractUsedInterrupt {
    public Card1230_105() {
        super(Side.LIGHT, 5, "Ke Chu Ke Kukuta?", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Balka. Hachu ma blinki?'");
        setGameText("If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, may use 1 Force to take any one card into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KE_CHU_KE_KUKUTA__TAKE_ANY_CARD;

        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.information_broker))
                && GameConditions.canDrawDestiny(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Draw destiny to take a card into hand");
            action.allowResponses("Draw destiny. If destiny > opponent's characters on table, use 1 Force to take any card into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            int opponentCharacters = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.character));
                                            if (totalDestiny != null && totalDestiny > opponentCharacters
                                                    && GameConditions.canUseForce(game, playerId, 1)
                                                    && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                                                action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                                                        new YesNoDecision("Use 1 Force to take any card into hand from Reserve Deck?") {
                                                            @Override
                                                            protected void yes() {
                                                                action.appendEffect(new UseForceEffect(action, playerId, 1));
                                                                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.any, true));
                                                            }
                                                            @Override
                                                            protected void no() {
                                                            }
                                                        }));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
