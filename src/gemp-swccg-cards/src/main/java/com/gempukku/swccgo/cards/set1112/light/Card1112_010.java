package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Weapon
 * Subtype: Starship
 * Title: Enhanced Proton Torpedoes
 */
public class Card1112_010 extends AbstractStarshipWeapon {
    public Card1112_010() {
        super(Side.LIGHT, 3, "Enhanced Proton Torpedoes", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Often modified by Rebellion technicians. Heavy warhead provides increased armor penetration. Decreased maneuverability and effectiveness against starfighters.");
        setGameText("Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (if on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.PROTON_TORPEDOES, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starfighter);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(SwccgGame game, PhysicalCard self) {
        return Filters.starfighter;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.starship, Filters.canBeTargetedByWeaponAsStarfighter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard starfighter = self.getAttachedTo();
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.sameCardId(self), Filters.any)
                && starfighter != null
                && Filters.and(Filters.starfighter, Keyword.HEROIC).accepts(game, starfighter)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Use 1 Force to add 3 to weapon destiny");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect(action, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
