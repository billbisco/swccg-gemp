package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * A condition that is fulfilled while a player's first battle destiny of the current battle is being drawn.
 */
public class FirstBattleDestinyBeingDrawnCondition implements Condition {

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null) {
            return false;
        }
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect effect = drawDestinyState.getDrawDestinyEffect();
            if (effect.getDestinyType() != DestinyType.BATTLE_DESTINY) {
                return false;
            }
            List<?> draws = battleState.getBattleDestinyDraws(effect.getPlayerDrawingDestiny());
            return draws == null || draws.size() <= 1;
        }
        List<?> dsDraws = battleState.getBattleDestinyDraws(gameState.getDarkPlayer());
        List<?> lsDraws = battleState.getBattleDestinyDraws(gameState.getLightPlayer());
        boolean dsNone = dsDraws == null || dsDraws.isEmpty();
        boolean lsNone = lsDraws == null || lsDraws.isEmpty();
        return dsNone || lsNone;
    }
}
