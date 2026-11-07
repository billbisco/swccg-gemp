package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.set1114.dark.Card1114_033;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Virtual Block 5: cancellation and defense count follow the printed Legacy clauses. */
public class Card1250_024 extends Card1114_033 {
    public Card1250_024() {
        setLore("When two Jedi are attempting to breach your bridge, even a destroyer droid's response time seems far too slow.");
        setGameText("Deploy on table. Scum And Villainy is canceled. Non-unique destroyer droids are deploy -1 and forfeit +1. While all your characters at sites are destroyer droids, opponent's characters are defense value -1 for each destroyer droid present. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }
    @Override
    public ExpansionSet getExpansionSet() { return ExpansionSet.VIRTUAL_BLOCK_5; }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof CancelsGameTextModifier || modifier instanceof DefenseValueModifier);
        Evaluator presentDroids = new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying querying, PhysicalCard affected) {
                return -Filters.countActive(gameState.getGame(), self, Filters.and(Filters.destroyer_droid,
                        Filters.presentAt(Filters.wherePresent(affected))));
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying querying, PhysicalCard affected, PhysicalCard other) {
                return evaluateExpression(gameState, querying, affected);
            }
        };
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                new CantSpotCondition(self, Filters.and(Filters.your(self), Filters.character,
                        Filters.at(Filters.site), Filters.not(Filters.destroyer_droid))), presentDroids));
        return modifiers;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Scum_And_Villainy)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return List.of(action);
        }
        return null;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isTableChanged(game, result)
                && GameConditions.canTargetToCancel(game, self, Filters.Scum_And_Villainy)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scum_And_Villainy, "Scum And Villainy");
            return List.of(action);
        }
        return null;
    }
}
