package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;

/**
 * He Always Wins! (1283_11): "If you have won a Podrace, ... 'free' on You're A Slave? may be treated as '2 Force'."
 * Called from You're A Slave? (12_180 back) when the face-down card cannot deploy for free: the owner may instead
 * deploy it for 2 Force (the card's deploy cost is set to 2 by a change in cost from its current deploy cost).
 */
public class LegacyYoureASlaveTwoForceDeploy {

    private LegacyYoureASlaveTwoForceDeploy() {
    }

    /**
     * @return true if the 2 Force option was offered (and the caller must not resolve the card itself)
     */
    public static boolean offer(final AbstractGameTextAction action, final SwccgGame game, final PhysicalCard youreASlave, final String playerId, final PhysicalCard card) {
        if (!GameConditions.hasGameTextModification(game, youreASlave, ModifyGameTextType.LEGACY__YOURE_A_SLAVE__FREE_MAY_BE_TREATED_AS_2_FORCE))
            return false;
        final float changeInCost = 2 - game.getModifiersQuerying().getDeployCost(game.getGameState(), card);
        if (!Filters.deployable(youreASlave, null, false, changeInCost).accepts(game, card))
            return false;

        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + " for 2 Force instead of for free?") {
                            @Override
                            protected void yes() {
                                game.getGameState().sendMessage(playerId + " treats 'free' on You're A Slave? as '2 Force'");
                                action.appendEffect(
                                        new StackActionEffect(action, card.getBlueprint().getPlayCardAction(card.getOwner(), game, card,
                                                youreASlave, false, changeInCost, null, null, null, null, null, false, 0, Filters.any, null)));
                            }

                            @Override
                            protected void no() {
                                game.getGameState().sendMessage(playerId + " is not able to deploy " + GameUtils.getCardLink(card));
                                action.appendEffect(
                                        new LoseForceEffect(action, playerId, 2));
                                action.appendEffect(
                                        new LoseCardsFromOffTableSimultaneouslyEffect(action, Collections.singleton(card), false));
                            }
                        }));
        return true;
    }
}
