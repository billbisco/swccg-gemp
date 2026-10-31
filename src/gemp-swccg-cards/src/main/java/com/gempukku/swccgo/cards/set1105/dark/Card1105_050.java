package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Voyeur
 */
public class Card1105_050 extends AbstractUsedOrLostInterrupt {
    public Card1105_050() {
        super(Side.DARK, 3, Title.Voyeur, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("As there are very few local authorities on Dagobah, peeping Nav Computers can get away with just about anything.");
        setGameText("USED: If opponent has three non-battleground locations on table, activate 5 Force. LOST: Deploy a battleground location from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canActivateForce(game, playerId)
                && GameConditions.canSpotLocation(game, 3, Filters.and(Filters.opponents(playerId), Filters.non_battleground_location))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 5 Force");
            action.allowResponses("Activate 5 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 5));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VOYEUR_V__DEPLOY_BATTLEGROUND_LOCATION;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy battleground location from Reserve Deck");
            action.allowResponses("Deploy a battleground location from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.battleground, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
