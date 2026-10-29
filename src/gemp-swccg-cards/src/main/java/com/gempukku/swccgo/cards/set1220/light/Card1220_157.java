package com.gempukku.swccgo.cards.set1220.light;

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
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Visored Vision
 */
public class Card1220_157 extends AbstractUsedOrLostInterrupt {
    public Card1220_157() {
        super(Side.LIGHT, 3, Title.Visored_Vision, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("It's 15 parsecs to Kessel, we've replaced the negative power coupling, it's dark and we're wearing welding goggles. Hit it.");
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

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VISORED_VISION_V__DEPLOY_BATTLEGROUND_LOCATION;
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
