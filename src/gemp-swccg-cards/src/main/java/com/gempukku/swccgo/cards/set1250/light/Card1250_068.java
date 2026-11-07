package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardToTargetEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 Wookiee Guide (V), printing 68. */
public class Card1250_068 extends AbstractUsedInterrupt {
    public Card1250_068() {
        super(Side.LIGHT, 4, "Wookiee Guide", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Chewie felt right at home in the forests of Endor, which closely resemble the environment on his homeworld of Kashyyyk.");
        setGameText("Stack a Wookiee from your Lost Pile on Grrrghrrrgh!. OR Deploy a Wookiee stacked on Grrrghrrrgh! to Kashyyyk (as if from hand). OR Once per game, draw X cards from top of Reserve Deck, where X = number of cards stacked on Grrrghrrrgh!.");
        addIcons(Icon.ENDOR, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        PhysicalCard roar = Filters.findFirstActive(game, self, Filters.and(Filters.your(playerId), Filters.title("Grrrghrrrgh!")));
        if (roar == null) return null;

        List<PlayInterruptAction> actions = new ArrayList<>(3);
        if (game.getGameState().getLostPile(playerId).stream().anyMatch(card -> Filters.Wookiee.accepts(game, card))) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Stack a Wookiee on Grrrghrrrgh!");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    if (GameConditions.canSpot(game, self, roar)) {
                        action.appendEffect(new StackCardFromLostPileEffect(action, roar, Filters.Wookiee, false));
                    }
                }
            });
            actions.add(action);
        }

        Filter target = Filters.locationAndCardsAtLocation(Filters.Kashyyyk_location);
        Filter eligible = Filters.and(Filters.your(playerId), Filters.Wookiee,
                Filters.deployableToTarget(self, target, false, 0));
        if (GameConditions.hasStackedCards(game, roar, eligible)) {
            actions.add(deployStackedWookiee(game, self, roar, target, eligible));
        }

        GameTextActionId id = GameTextActionId.LEGACY__WOOKIEE_GUIDE_V__DRAW_FOR_STACK;
        if (GameConditions.hasStackedCards(game, roar) && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isOncePerGame(game, self, id)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, id);
            action.setText("Draw cards for the Grrrghrrrgh! stack");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    int count = game.getGameState().getStackedCards(roar).size();
                    if (count > 0) {
                        action.appendEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, count));
                    }
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private PlayInterruptAction deployStackedWookiee(SwccgGame game, PhysicalCard self, PhysicalCard roar,
            Filter target, Filter eligible) {
        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Deploy a stacked Wookiee to Kashyyyk");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                if (!GameConditions.canSpot(game, self, roar)) return;
                action.appendEffect(new ChooseStackedCardEffect(action, action.getPerformingPlayer(), roar, eligible) {
                    @Override
                    protected void cardSelected(PhysicalCard chosen) {
                        action.appendEffect(new AddUntilEndOfCardPlayedModifierEffect(action, self,
                                new MayDeployAsIfFromHandModifier(self, Filters.sameCardId(chosen)), null));
                        action.appendEffect(new DeployStackedCardToTargetEffect(action, chosen, target));
                    }
                });
            }
        });
        return action;
    }
}
