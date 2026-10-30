package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.ExchangeCardFromLostPileWithStackedCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: Dark Forces
 */
public class Card1103_031 extends AbstractNormalEffect {
    public Card1103_031() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Dark_Forces, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'The ability to destroy a planet is insignificant next to the power of the Force.'");
        setGameText("Deploy on table. Once per turn, if opponent just drew weapon destiny targeting your Dark Jedi, may lose 1 Force to cancel and redraw that destiny. Once per game, may exchange a card in opponent's Lost Pile with a card on their Colo Claw Fish. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId redrawId = GameTextActionId.LEGACY__DARK_FORCES_V__CANCEL_AND_REDRAW_WEAPON_DESTINY;

        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.your(self), Filters.Dark_Jedi))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, redrawId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, redrawId);
            action.setText("Cancel and redraw weapon destiny");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId exchangeId = GameTextActionId.LEGACY__DARK_FORCES_V__EXCHANGE_COLO_CLAW_FISH;

        if (GameConditions.isOncePerGame(game, self, exchangeId)
                && GameConditions.hasLostPile(game, opponent)
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.Colo_Claw_Fish))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, exchangeId);
            action.setText("Exchange Lost Pile card with Colo Claw Fish");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new ExchangeCardFromLostPileWithStackedCardEffect(action, opponent, Filters.any, Filters.and(Filters.opponents(self), Filters.Colo_Claw_Fish), Filters.any, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
