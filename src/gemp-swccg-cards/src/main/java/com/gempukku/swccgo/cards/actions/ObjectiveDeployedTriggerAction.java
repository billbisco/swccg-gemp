package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.logic.actions.ObjectiveSetupAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.IllegalObjectiveEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An action that performs the deployment text of the Objective.
 */
public class ObjectiveDeployedTriggerAction extends RequiredGameTextTriggerAction implements ObjectiveSetupAction {
    private ObjectiveDeployedTriggerAction _that;
    // #142 R3
    private boolean _setupFromHand;
    private final List<StandardEffect> _requiredEffects = new ArrayList<StandardEffect>();

    /**
     * Creates an action that performs the deployment text of the Objective.
     * @param objective the Objective card
     */
    public ObjectiveDeployedTriggerAction(final PhysicalCard objective) {
        super(objective, objective.getCardId());
        _that = this;
        setPerformingPlayer(objective.getOwner());
        skipInitialMessageAndAnimation();

        // Insert an after effect that if any required effects fail, the Objective is placed out of play and any other cards
        // (except Starting Effect) are placed back in Reserve Deck
        insertAfterEffect(
                new PassthruEffect(_that) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_that.isAnyCostFailed()) {
                            game.getGameState().sendMessage(GameUtils.getCardLink(objective) + " did not satisfy its deployment requirements, so it will be placed out of play");
                            // Insert so it happens before any other after effects
                            insertAfterEffect(
                                    new IllegalObjectiveEffect(_that, objective));
                        }
                    }
                }
        );
    }

    /**
     * Append required effect. If any of the required effect fails, the Objective is placed out of play.
     * @param effect the effect
     */
    public final void appendRequiredEffect(StandardEffect effect) {
        _requiredEffects.add(effect);
        appendCost(effect);
    }

    /**
     * @return the required effects, in order (#142 R3: the cards an Objective deployed from hand needs)
     */
    public final List<StandardEffect> getRequiredEffects() {
        return Collections.unmodifiableList(_requiredEffects);
    }

    @Override
    public boolean isSetupFromHand() {
        return _setupFromHand;
    }

    public void setSetupFromHand(boolean setupFromHand) {
        _setupFromHand = setupFromHand;
    }

    /**
     * Append optional effect.
     * @param effect the effect
     */
    public final void appendOptionalEffect(StandardEffect effect) {
        appendEffect(effect);
    }
}
