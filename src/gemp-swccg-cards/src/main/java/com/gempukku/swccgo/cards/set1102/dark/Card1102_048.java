package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Empire's Back
 */
public class Card1102_048 extends AbstractLostInterrupt {
    public Card1102_048() {
        super(Side.DARK, 3, Title.The_Empires_Back, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'No star system will dare oppose the Emperor now.'");
        setGameText("Use 1 Force to deploy (or retrieve) Motti, Tagge, Tarkin, or Vader from Reserve Deck; reshuffle. OR Once during your control phase, for each battleground controlled by Motti, Tagge, Tarkin, or Vader, opponent loses 1 Force.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);
        final Filter uniqueFilter = Filters.or(Filters.Motti, Filters.Tagge, Filters.Tarkin, Filters.Vader);
        GameTextActionId searchActionId = GameTextActionId.LEGACY__THE_EMPIRES_BACK_V__DEPLOY_OR_RETRIEVE_UNIQUE;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Deploy from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Deploy Motti, Tagge, Tarkin, or Vader from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, uniqueFilter, true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSearchLostPile(game, playerId, self, searchActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Retrieve from Lost Pile");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Retrieve Motti, Tagge, Tarkin, or Vader",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, uniqueFilter));
                        }
                    }
            );
            actions.add(action);
        }

        Filter battlegrounds = Filters.and(Filters.battleground,
                Filters.controlsWith(playerId, self, Filters.or(Filters.Motti, Filters.Tagge, Filters.Tarkin, Filters.Vader)));
        final int count = Filters.countTopLocationsOnTable(game, battlegrounds);
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, self.getCardId(), Phase.CONTROL)
                && count > 0) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.allowResponses("Make opponent lose " + count + " Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, count));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
