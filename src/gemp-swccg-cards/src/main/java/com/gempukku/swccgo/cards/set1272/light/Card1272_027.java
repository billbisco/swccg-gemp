package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Clinging To The Edge (V)
 */
public class Card1272_027 extends AbstractUsedOrLostInterrupt {
    public Card1272_027() {
        super(Side.LIGHT, 5, "Clinging To The Edge", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("There are some times, more than others, when you should not look down.");
        setGameText("USED: During opponent's deploy phase, if both players occupy the same battleground, activate 2 Force. LOST: During any deploy phase, deploy a lightsaber from Lost Pile on your character at an interior Naboo battleground.");
        addIcons(Icon.EPISODE_I);
    }

    static Filter yourCharacterAtInteriorNabooBattleground(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.and(Filters.interior_Naboo_site, Filters.battleground)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // USED
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canActivateForce(game, playerId)
                && GameConditions.canSpotLocation(game, Filters.and(Filters.battleground, Filters.occupies(playerId), Filters.occupies(opponent)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 2 Force");
            // Allow response(s)
            action.allowResponses("Activate 2 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 2));
                        }
                    }
            );
            actions.add(action);
        }

        // LOST
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CLINGING_TO_THE_EDGE_V__DEPLOY_LIGHTSABER;
        final Filter targetFilter = yourCharacterAtInteriorNabooBattleground(self);
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, true)
                && GameConditions.canSpot(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy lightsaber from Lost Pile");
            // Allow response(s)
            action.allowResponses("Deploy a lightsaber from Lost Pile on your character at an interior Naboo battleground",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, targetFilter, false));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
