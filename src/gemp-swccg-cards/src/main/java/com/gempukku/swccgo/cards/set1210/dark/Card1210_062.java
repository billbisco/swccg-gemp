package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Informant
 */
public class Card1210_062 extends AbstractUsedInterrupt {
    public Card1210_062() {
        super(Side.DARK, 6, Title.Informant, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire's network of spies and petty informants allows Imperial operatives to discover and react to Rebel assaults before they occur.");
        setGameText("Deploy Undercover on table from Reserve Deck; reshuffle. OR Peek at top X cards of opponent's Reserve Deck, where X = number of opponent's characters present with your Undercover spy. OR If your Undercover spy on table, peek at top card of your Reserve Deck.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId deployActionId = GameTextActionId.LEGACY__INFORMANT_V__DEPLOY_UNDERCOVER;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployActionId, true, false)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployActionId);
            action.setText("Deploy Undercover from Reserve Deck");
            action.allowResponses("Deploy Undercover from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Undercover, true));
                        }
                    }
            );
            actions.add(action);
        }

        int x = getOpponentCharactersPresentWithYourUndercoverSpies(game, self);
        if (x > 0 && GameConditions.hasReserveDeck(game, opponent)) {
            final int numToPeek = x;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top " + numToPeek + " of opponent's Reserve Deck");
            action.allowResponses("Peek at top " + numToPeek + " cards of opponent's Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckEffect(action, playerId, opponent, numToPeek));
                        }
                    }
            );
            actions.add(action);
        }

        if (hasYourUndercoverSpyOnTable(game, self)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top card of your Reserve Deck");
            action.allowResponses("Peek at top card of your Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    private boolean hasYourUndercoverSpyOnTable(SwccgGame game, PhysicalCard self) {
        return !Filters.filterAllOnTable(game, Filters.and(Filters.your(self), Filters.undercover_spy)).isEmpty();
    }

    private int getOpponentCharactersPresentWithYourUndercoverSpies(SwccgGame game, PhysicalCard self) {
        Collection<PhysicalCard> spies = Filters.filterAllOnTable(game, Filters.and(Filters.your(self), Filters.undercover_spy));
        int count = 0;
        for (PhysicalCard spy : spies) {
            count += Filters.filterAllOnTable(game, Filters.and(Filters.opponents(self), Filters.character, Filters.presentWith(spy))).size();
        }
        return count;
    }
}
