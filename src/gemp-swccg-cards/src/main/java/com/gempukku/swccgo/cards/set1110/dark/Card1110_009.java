package com.gempukku.swccgo.cards.set1110.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndStackEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Effect
 * Title: Imperial Justice
 */
public class Card1110_009 extends AbstractNormalEffect {
    public Card1110_009() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Justice", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("'There's nothing you could have done Luke, had you been there. You'd have been killed too.'");
        setGameText("Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a card here into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndStackEffect(action, playerId, 3, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take stacked card into hand");
            action.setActionMsg("Prevent all battle destiny draws from being modified or canceled");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new TakeStackedCardIntoHandEffect(action, playerId, self));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotModifyBattleDestinyModifier(self), "Prevents all battle destiny draws from being modified"));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotCancelBattleDestinyModifier(self), "Prevents all battle destiny draws from being canceled"));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, playerId, 1));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
