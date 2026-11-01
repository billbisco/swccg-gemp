package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wookiee Guide (V)
 */
public class Card1116_019 extends AbstractLostInterrupt {
    public Card1116_019() {
        super(Side.LIGHT, 4, "Wookiee Guide", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Stack a Wookiee from your Lost Pile on Grrrghrrrgh!. OR Deploy a Wookiee stacked on Grrrghrrrgh! to Kashyyyk (as if from hand). OR Once per game, draw X cards from top of Reserve Deck, where X = number of cards stacked on Grrrghrrrgh!.");
        addIcons(Icon.ENDOR, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (GameConditions.canSpot(game, self, Filters.title("Grrrghrrrgh!"))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Stack a Wookiee on Grrrghrrrgh!");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    PhysicalCard roar = Filters.findFirstActive(game, self, Filters.title("Grrrghrrrgh!"));
                    if (roar != null) {
                        action.appendEffect(new StackCardFromLostPileEffect(action, roar, Filters.Wookiee, false));
                    }
                }
            });
            actions.add(action);

            final PlayInterruptAction deploy = new PlayInterruptAction(game, self);
            deploy.setText("Deploy a stacked Wookiee");
            deploy.allowResponses(new RespondablePlayCardEffect(deploy) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    PhysicalCard roar = Filters.findFirstActive(game, self, Filters.title("Grrrghrrrgh!"));
                    if (roar != null) {
                        deploy.appendEffect(new DeployStackedCardsEffect(deploy, roar, Filters.Wookiee, 1, 1, true));
                    }
                }
            });
            actions.add(deploy);
        }
        return actions;
    }
}
