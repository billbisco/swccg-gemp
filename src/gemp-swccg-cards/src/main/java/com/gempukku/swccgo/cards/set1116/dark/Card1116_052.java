package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractStarshipWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Weapon
 * Subtype: Starship
 * Title: Concussion Missiles
 */
public class Card1116_052 extends AbstractStarshipWeapon {
    public Card1116_052() {
        super(Side.DARK, 4, Title.Concussion_Missiles, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Originally developed by smugglers and pirates as defense against starfighters. Adapted by the Empire to serve as onboard defense for slow moving bombers.");
        setGameText("Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (if on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.MISSILE, Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starfighter);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.starfighter;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.starship, Filters.canBeTargetedByWeaponAsStarfighter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CONCUSSION_MISSILES_V__ADD_DESTINY;
        PhysicalCard attachedTo = self.getAttachedTo();
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.sameCardId(self), Filters.any)
                && attachedTo != null
                && Filters.and(Filters.starfighter, Keyword.HEROIC).accepts(game, attachedTo)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use 1 Force to add 3 to total");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new ModifyTotalWeaponDestinyEffect(action, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
