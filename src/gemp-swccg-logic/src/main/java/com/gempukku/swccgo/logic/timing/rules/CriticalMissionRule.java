package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.List;

/**
 * Records that a player has deployed a Critical Mission this game (AR 2.0: one on the table per game).
 */
public class CriticalMissionRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    public CriticalMissionRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(CardType.MISSION, CardSubtype.CRITICAL))) {
                            PhysicalCard playedCard = ((PlayCardResult) effectResult).getPlayedCard();
                            game.getGameState().setPlayedCriticalMission(playedCard.getOwner());
                        }
                        return null;
                    }
                });
    }
}
