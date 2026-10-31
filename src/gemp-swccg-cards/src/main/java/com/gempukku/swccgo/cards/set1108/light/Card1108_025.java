package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Title: Yerka Mig (V)
 */
public class Card1108_025 extends AbstractNormalEffect {
    public Card1108_025() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Yerka Mig", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("An Imperial bureaucrat with high security clearance who resigned and fled in remorse after the occupation of his home planet, Ralltiir. Now a fugitive from the ISB.");
        setGameText("Deploy on Cantina. While all your ability on table is provided by aliens and [Independent Starship] starship pilots: whenever you deploy a unique (•) alien to a battleground, may retrieve 1 Force; and, during battle, your total power is +3. If opponent controls this location, this Effect lost.");
    }

    /**
     * The cards that may provide ability for this Effect's game text to be active: aliens and [Independent] starships
     * (whose permanent pilots provide the ability).
     */
    static Filter aliensAndIndependentStarships() {
        return Filters.or(Filters.alien, Filters.and(Filters.starship, Icon.INDEPENDENT));
    }

    private static Condition allAbilityFromAliens(PhysicalCard self) {
        return new AllAbilityOnTableProvidedByCondition(self, self.getOwner(), aliensAndIndependentStarships());
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Cantina;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.any, allAbilityFromAliens(self), 3, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YERKA_MIG_V__RETRIEVE_FORCE;

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, Filters.and(Filters.unique, Filters.alien), Filters.battleground)
                && allAbilityFromAliens(self).isFulfilled(game.getGameState(), game.getModifiersQuerying())
                && (!mayRetrieveOnlyOncePerTurn(game, self) || GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            if (mayRetrieveOnlyOncePerTurn(game, self)) {
                action.appendUsage(
                        new OncePerTurnEffect(action));
            }
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static boolean mayRetrieveOnlyOncePerTurn(SwccgGame game, PhysicalCard self) {
        return !game.getModifiersQuerying().getModifiersAffectingCard(game.getGameState(), ModifierType.MAY_RETRIEVE_FORCE_WITH_CARD_ONLY_ONCE_PER_TURN, self).isEmpty();
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
