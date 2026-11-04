package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PlaceOnBottomOfForcePileWhenCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Effect
 * Title: All My Urchins & Cloud City Celebration
 */
public class Card1283_020 extends AbstractNormalEffect {
    public Card1283_020() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "All My Urchins & Cloud City Celebration", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        addComboCardTitles(Title.All_My_Urchins, Title.Cloud_City_Celebration);
        setGameText("Deploy on table. If Independent Operation on table and all your ability is provided by Leia, Luke, aliens, Cloud Cars, and [Independent] starship pilots: Cloud City Troopers ignore location deployment restrictions (and \"power -1\") in their game text; immunity to attrition of opponent's characters and starships at Bespin locations is limited to < 6; your total battle destiny is +1 (+2 if your trooper in battle); and you retrieve 2 Force whenever you initiate battle. Place Effect on bottom of Force Pile if canceled.");
        addIcons(Icon.SETUP);
    }

    /**
     * Cards that may provide all of your ability: Leia, Luke, aliens, Cloud Cars, and [Independent] starship pilots
     * (characters piloting an [Independent] starship, or the starship's own permanent pilot).
     */
    static Filter abilityProviders() {
        Filter independentStarship = Filters.and(Filters.starship, Icon.INDEPENDENT);
        return Filters.or(Filters.Leia, Filters.Luke, Filters.alien, Filters.cloud_car, independentStarship,
                Filters.and(Filters.character, Filters.piloting(independentStarship)));
    }

    static Condition active(PhysicalCard self) {
        return new AndCondition(new OnTableCondition(self, Filters.Independent_Operation),
                new AllAbilityOnTableProvidedByCondition(self, self.getOwner(), abilityProviders()));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PlaceOnBottomOfForcePileWhenCanceledModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition active = active(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, Filters.Cloud_City_trooper, active));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Cloud_City_trooper, active, ModifyGameTextType.LEGACY__CLOUD_CITY_TROOPER__IGNORES_POWER_MINUS_ONE));
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, Filters.and(Filters.opponents(playerId), Filters.or(Filters.character, Filters.starship),
                Filters.at(Filters.Bespin_location)), active, 6));
        modifiers.add(new TotalBattleDestinyModifier(self, active,
                new ConditionEvaluator(1, 2, new InBattleCondition(self, Filters.and(Filters.your(playerId), Filters.trooper))), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && active(self).isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 2 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
