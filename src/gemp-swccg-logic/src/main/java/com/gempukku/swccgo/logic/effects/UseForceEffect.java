package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;

public class UseForceEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _amountOfForce;
    private int _amountForOpponentToUse;
    private boolean _onlyOwnForce;
    private int _amountSubstituted;
    private boolean _substitutionDeclined;

    public UseForceEffect(Action action, String playerId, float amountOfForce) {
        this(action, playerId, amountOfForce, false);
    }

    public UseForceEffect(Action action, String playerId, float amountOfForce, boolean onlyOwnForce) {
        super(action);
        _playerId = playerId;
        _amountOfForce = (int) Math.ceil(amountOfForce);
        _onlyOwnForce = onlyOwnForce;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Use " + (getTotalAmountOfForceToUse() - _amountForOpponentToUse) + " Force";
    }

    @Override
    public Type getType() {
        return Type.BEFORE_USE_FORCE;
    }

    public void setAmountForOpponentToUse(int amount) {
        _amountForOpponentToUse = amount;
    }

    public int getAmountForOpponentToUse() {
        return _amountForOpponentToUse;
    }

    /**
     * Gets the amount of Force still to be used from Force Piles (the original amount minus any amount the player
     * chose to replace via a MAY_SUBSTITUTE_FOR_USING_FORCE card).
     * @return the amount of Force
     */
    public int getTotalAmountOfForceToUse() {
        return _amountOfForce - _amountSubstituted;
    }

    /**
     * Records that the player did something else instead of using the specified amount of this Force.
     * @param amount the amount of Force no longer to be used
     */
    public void addAmountSubstituted(int amount) {
        _amountSubstituted = Math.min(_amountOfForce - _amountForOpponentToUse, _amountSubstituted + Math.max(0, amount));
    }

    public int getAmountSubstituted() {
        return _amountSubstituted;
    }

    /**
     * Records that the player declined to substitute for (any more of) this Force, so the rest must come from Force Piles.
     */
    public void setSubstitutionDeclined() {
        _substitutionDeclined = true;
    }

    public boolean isSubstitutionDeclined() {
        return _substitutionDeclined;
    }

    /**
     * Gets the amount of Force the player could still replace for this effect via MAY_SUBSTITUTE_FOR_USING_FORCE cards.
     * @param game the game
     * @return the amount of Force
     */
    public int getSubstitutesStillAvailable(SwccgGame game) {
        if (_substitutionDeclined)
            return 0;
        return game.getModifiersQuerying().getForceUseSubstitutesAvailable(game.getGameState(), _playerId);
    }

    public boolean canOnlyUseOwnForce() {
        return _onlyOwnForce;
    }

    public String getPlayerId() {
        return _playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return getTotalAmountOfForceToUse() <= game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), _playerId);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        final List<UseOneForceEffect> useOneForceEffects = new LinkedList<UseOneForceEffect>();
        final int amountOfForce = getTotalAmountOfForceToUse();
        for (int i = 0; i < amountOfForce; i++) {
            final UseOneForceEffect effect = new UseOneForceEffect(subAction, i < _amountForOpponentToUse ? game.getOpponent(_playerId) : _playerId,
                    i==0 || i==_amountForOpponentToUse, i==(_amountForOpponentToUse-1) || i==(amountOfForce-1));
            subAction.appendEffect(effect);
            useOneForceEffects.add(effect);
        }
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsUsedAsForceByPlayer = new LinkedList<PhysicalCard>();
                        List<PhysicalCard> cardsUsedAsForceByOpponent = new LinkedList<PhysicalCard>();
                        int opponentUsedCount = 0;
                        int playerUsedCount = 0;
                        for (UseOneForceEffect useOneForceEffect : useOneForceEffects) {
                            if (useOneForceEffect.wasCarriedOut()) {
                                if (useOneForceEffect.getPlayerId().equals(_playerId)) {
                                    cardsUsedAsForceByPlayer.add(useOneForceEffect.getCard());
                                    playerUsedCount++;
                                }
                                else {
                                    cardsUsedAsForceByOpponent.add(useOneForceEffect.getCard());
                                    opponentUsedCount++;
                                }
                            }
                        }
                        if (opponentUsedCount > 0) {
                            String cardsUsedForMsg = game.getGameState().isUsedPilesTurnedOver() ? " - " + GameUtils.getAppendedNames(cardsUsedAsForceByOpponent) : "";
                            game.getGameState().sendMessage(_playerId + " uses " + opponentUsedCount + " of " + game.getOpponent(_playerId) + "'s Force" + cardsUsedForMsg);
                        }
                        if (playerUsedCount > 0) {
                            String cardsUsedForMsg = game.getGameState().isUsedPilesTurnedOver() ? " - " + GameUtils.getAppendedNames(cardsUsedAsForceByPlayer) : "";
                            game.getGameState().sendMessage(_playerId + " uses " + playerUsedCount + " Force"  + cardsUsedForMsg);
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
