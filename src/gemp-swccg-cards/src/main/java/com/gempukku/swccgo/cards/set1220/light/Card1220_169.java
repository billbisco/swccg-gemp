package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Weapon
 * Subtype: Character
 * Title: Luke's Blaster Pistol
 */
public class Card1220_169 extends AbstractCharacterWeapon {
    public Card1220_169() {
        super(Side.LIGHT, 3, Title.Lukes_Blaster_Pistol, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Merr-Sonn Model 57. Part of an arms lot purchased for the Alliance from a black market dealer on Ord Mantell. Carried by Luke as a backup for his father's lightsaber.");
        setGameText("Deploy on Luke. During battle, opponent may not move from here. Once per battle, may target a character (target loses immunity to attrition for remainder of turn) or vehicle (with maneuver) for free. Draw destiny. Target hit and forfeit -3 if destiny +2 > defense value.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.Luke);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Luke);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Luke;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.and(Filters.vehicle, Filters.maneuverMoreThan(0))), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE, false, -3);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.opponents(self),
                new DuringBattleAtCondition(Filters.here(self)), Filters.here(self)));
        modifiers.add(new TotalWeaponDestinyModifier(self, 2, Filters.any));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.sameCardId(self), Filters.any)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.sameCardId(self), Filters.character)) {
            Collection<PhysicalCard> targets = game.getGameState().getWeaponFiringState().getTargets();
            PhysicalCard target = targets.isEmpty() ? null : targets.iterator().next();
            if (target != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel immunity to attrition");
                action.appendEffect(
                        new CancelImmunityToAttritionUntilEndOfTurnEffect(action, target,
                                "Cancels " + GameUtils.getCardLink(target) + "'s immunity to attrition"));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
