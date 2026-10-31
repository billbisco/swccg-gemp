package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.UseOpponentsForceIconsWhenActivatingModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Imperial Decree
 */
public class Card1106_038 extends AbstractNormalEffect {
    public Card1106_038() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Decree, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("To Imperial command personnel: The Rebellion must be crushed! Minor acts of sedition are to be ignored. The destruction of the Alliance is your primary goal.");
        setGameText("Deploy on table. At start of your turn, may lose 1 Force to activate this turn using [Light Side Force] instead of [Dark Side Force]. Whenever you lose Force (except from Force drains, battle damage, or your card), may reduce loss (to a minimum of 1) by the number of battlegrounds you occupy. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        GameTextActionId activateActionId = GameTextActionId.LEGACY__IMPERIAL_DECREE_V__ACTIVATE_USING_LIGHT_ICONS;
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, activateActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, activateActionId);
            action.setText("Activate using Light Side icons");
            action.setActionMsg("Lose 1 Force to activate this turn using opponent's Force icons");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new UseOpponentsForceIconsWhenActivatingModifier(self, playerId),
                            "uses opponent's Force icons when activating Force"));
            actions.add(action);
        }

        GameTextActionId reduceActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.your(self))
                && !TriggerConditions.isAboutToLoseForceFromBattleDamage(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.any)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, reduceActionId)) {
            int numToReduce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId)));
            if (numToReduce > 0) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, reduceActionId);
                action.setText("Reduce Force loss by " + numToReduce);
                action.setActionMsg("Reduce Force loss by " + numToReduce + " (to a minimum of 1)");
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, numToReduce, 1));
                actions.add(action);
            }
        }

        return actions.isEmpty() ? null : actions;
    }
}
