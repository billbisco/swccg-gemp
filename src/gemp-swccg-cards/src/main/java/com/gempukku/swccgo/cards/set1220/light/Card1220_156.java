package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfUsedPileOnTopOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.PlaceUsedPileUnderForcePileEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Through The Force Things You Will See
 */
public class Card1220_156 extends AbstractLostInterrupt {
    public Card1220_156() {
        super(Side.LIGHT, 3, Title.Through_The_Force_Things_You_Will_See, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("One training exercise for a Jedi's apprentice is to invert one's view to see things from a different perspective. 'The future, the past. Old friends long gone.'");
        setGameText("Once per game, choose: if you are about to recirculate at the end of your turn, and you have fewer cards in Used Pile than in Force Pile, place your Used Pile under your Force Pile instead. OR Place the top card of your Used Pile on top of your Force Pile.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THROUGH_THE_FORCE_V__RECIRCULATE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasUsedPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Place top card of Used Pile on Force Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Place the top card of Used Pile on top of Force Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PlaceTopCardOfUsedPileOnTopOfForcePileEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THROUGH_THE_FORCE_V__RECIRCULATE;
        if (effect instanceof RecirculateEffect
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && playerId.equals(game.getGameState().getCurrentPlayerId())
                && GameConditions.isPhaseForPlayer(game, Phase.END_OF_TURN, playerId)
                && GameConditions.hasUsedPile(game, playerId)
                && game.getGameState().getUsedPile(playerId).size() < game.getGameState().getForcePile(playerId).size()) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Place Used Pile under Force Pile instead");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Place Used Pile under Force Pile instead of recirculating",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PlaceUsedPileUnderForcePileEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
