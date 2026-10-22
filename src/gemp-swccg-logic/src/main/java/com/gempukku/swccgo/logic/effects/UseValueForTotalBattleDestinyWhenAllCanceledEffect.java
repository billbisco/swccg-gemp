package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;

/**
 * An effect that gives the specified player a total battle destiny of the specified value when every battle destiny
 * the player drew this battle was canceled (and not redrawn). Used by Chasm (V) / Abyss (V): "If all your battle
 * destinies were canceled (and not redrawn) use 1 for your total battle destiny instead."
 *
 * The value is recorded the same way a substituted destiny is (a draw with no card), so the player has a total battle
 * destiny for attrition and for cards that check whether battle destiny was drawn.
 */
public class UseValueForTotalBattleDestinyWhenAllCanceledEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _value;

    /**
     * Creates an effect that gives the player a total battle destiny of the specified value when all of the player's
     * battle destinies were canceled.
     * @param action the action performing this effect
     * @param playerId the player
     * @param value the value to use as total battle destiny
     */
    public UseValueForTotalBattleDestinyWhenAllCanceledEffect(Action action, String playerId, float value) {
        super(action);
        _playerId = playerId;
        _value = value;
    }

    /**
     * Determines if the specified player drew battle destiny this battle and every draw was canceled (and not redrawn).
     * @param gameState the game state
     * @param playerId the player
     * @return true if all battle destinies were canceled, otherwise false
     */
    public static boolean allBattleDestiniesCanceled(GameState gameState, String playerId) {
        BattleState battleState = gameState.getBattleState();
        return battleState != null
                && battleState.getNumBattleDestinyDrawn(playerId) > 0
                && battleState.getNumUncanceledBattleDestinyDrawn(playerId) == 0;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState == null || !allBattleDestiniesCanceled(gameState, _playerId)) {
            return;
        }
        battleState.increaseTotalBattleDestinyFromDraws(_playerId, Collections.<PhysicalCard>singletonList(null),
                Collections.singletonList(false), Collections.singletonList(_value), _value);
        gameState.sendMessage("All of " + _playerId + "'s battle destinies were canceled, so " + _playerId + "'s total battle destiny is "
                + GuiUtils.formatAsString(battleState.getTotalBattleDestiny(game, _playerId)) + " instead");
    }
}
