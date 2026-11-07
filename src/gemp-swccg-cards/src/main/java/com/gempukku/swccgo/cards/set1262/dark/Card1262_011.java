package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDuelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CancelCardOnTableResult;

import java.util.List;

/** Legacy Block 6.2: So Be It, Jedi. */
public class Card1262_011 extends AbstractNormalEffect {
    public Card1262_011() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "So Be It, Jedi", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("\"Now, young Skywalker ... you will die.\"");
        setGameText("Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may take Rise, My Friend into hand from Reserve Deck; reshuffle. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__SO_BE_IT_JEDI__TAKE_RISE_MY_FRIEND;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) return null;
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Take Rise, My Friend into hand from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Rise, My Friend"), true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var objectives = Filters.and(Filters.your(self), Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place));
        return List.of(new MayNotCancelDuelModifier(self, Filters.any, (state, querying) -> {
            if (!state.isDuringDuel()) return false;
            var initiator = state.getDuelState().getCardInitiatedDuel();
            return initiator != null && objectives.accepts(state, querying, initiator);
        }));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!(result instanceof CancelCardOnTableResult canceled) || canceled.getCard() != self
                || !game.getOpponent(self.getOwner()).equals(canceled.getPerformingPlayerId())) return null;
        var rebellion = Filters.findFirstActive(game, self, Filters.Insignificant_Rebellion);
        if (rebellion == null) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Stack So Be It, Jedi on Insignificant Rebellion");
        action.appendEffect(new StackOneCardFromLostPileEffect(action, self, rebellion, false, false, true));
        return List.of(action);
    }
}
