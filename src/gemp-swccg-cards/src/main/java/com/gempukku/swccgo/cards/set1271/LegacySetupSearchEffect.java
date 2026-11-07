package com.gempukku.swccgo.cards.set1271;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** The shared three actions on Block 7.1's matched Light and Dark search Effects. */
public abstract class LegacySetupSearchEffect extends AbstractNormalEffect {
    private final GameTextActionId setupSearch;
    private final GameTextActionId immediateEffectSearch;

    protected LegacySetupSearchEffect(Side side, String title, GameTextActionId setupSearch, GameTextActionId immediateEffectSearch) {
        super(side, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        this.setupSearch = setupSearch;
        this.immediateEffectSearch = immediateEffectSearch;
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        if (GameConditions.isOncePerGame(game, self, setupSearch)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, setupSearch)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, setupSearch);
            action.setText("Take a Setup card into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                Filters.and(Icon.SETUP, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, immediateEffectSearch)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, immediateEffectSearch);
            action.setText("Place in Used Pile to take an Immediate Effect");
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                Filters.and(Filters.Immediate_Effect, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.forceDrainInitiated(game, result) || !GameConditions.canCancelForceDrain(game, self)) return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Place in Lost Pile to cancel Force drain");
        action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
        action.appendEffect(new CancelForceDrainEffect(action));
        return List.of(action);
    }
}
