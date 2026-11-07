package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.set1115.dark.Card1115_061;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5: Sebulba (V). */
public class Card1250_010 extends Card1115_061 {
    public Card1250_010() {
        setGameText("Adds 2 to power of any combat vehicle he pilots. Brisky Morning Munchen is canceled. Once per turn, if any podrace total > 0, may take your just drawn destiny into hand to cancel and redraw that destiny. Draws one battle destiny if unable to otherwise.");
        setSpecies(Species.DUG);
        addIcons(Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() { return ExpansionSet.VIRTUAL_BLOCK_5; }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof CancelsGameTextModifier);
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Brisky_Morning_Munchen))
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
                && GameConditions.canTargetToCancel(game, self, Filters.title(Title.Brisky_Morning_Munchen))) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Brisky_Morning_Munchen), "Brisky Morning Munchen");
            return List.of(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;
        var state = game.getGameState();
        var querying = game.getModifiersQuerying();
        if (TriggerConditions.isDestinyJustDrawnBy(game, result, playerId)
                && (querying.getHighestRaceTotal(state, playerId) > 0
                    || querying.getHighestRaceTotal(state, game.getOpponent(playerId)) > 0)
                && GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            var action = new OptionalGameTextTriggerAction(self, sourceId, id);
            action.setText("Take destiny into hand and redraw");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new TakeDestinyCardIntoHandEffect(action));
            action.appendEffect(new CancelDestinyAndCauseRedrawEffect(action));
            return List.of(action);
        }
        return null;
    }
}
