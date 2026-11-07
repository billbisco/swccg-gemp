package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Virtual Block 7.1: I'll Take Them Myself. */
public class Card1271_011 extends AbstractNormalEffect {
    public Card1271_011() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I'll Take Them Myself", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("'Several fighters have broken off from the main group. Come with me.' Darth Vader targets his TIE fighter's fire-linked blaster cannons at the Rebel pilots in the trench.");
        setGameText("Deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this card in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                      PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId setupId = GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_SETUP;
        if (GameConditions.isOncePerGame(game, self, setupId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, setupId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, setupId);
            action.setText("Take a Setup card into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.SETUP, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        GameTextActionId immediateId = GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_IMMEDIATE_EFFECT;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, immediateId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, immediateId);
            action.setText("Place in Used Pile to take an Immediate Effect");
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Immediate_Effect, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                 EffectResult effectResult, PhysicalCard self,
                                                                                 int gameTextSourceCardId) {
        if (!TriggerConditions.forceDrainInitiated(game, effectResult)
                || !GameConditions.canCancelForceDrain(game, self))
            return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Place in Lost Pile to cancel Force drain");
        action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
        action.appendEffect(new CancelForceDrainEffect(action));
        return Collections.singletonList(action);
    }
}
