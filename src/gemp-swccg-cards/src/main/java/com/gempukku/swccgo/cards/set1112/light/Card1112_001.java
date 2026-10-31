package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Weapon
 * Subtype: Character
 * Title: Anakin's Lightsaber
 */
public class Card1112_001 extends AbstractCharacterWeapon {
    public Card1112_001() {
        super(Side.LIGHT, 1, Title.Anakins_Lightsaber, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Constructed by Anakin Skywalker. Kept by Obi-Wan Kenobi until given to Luke.");
        setGameText("Deploy on Leia, non-Jedi Luke, or any Mara. May add 1 to Force drain where present. May target a character or creature. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value. Instead of firing, may use 1 Force to cancel a just drawn weapon destiny here.");
        addPersona(Persona.ANAKINS_LIGHTSABER);
        addIcons(Icon.HOTH);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.or(Filters.Leia, Filters.and(Filters.Luke, Filters.not(Filters.Jedi)), Filters.Mara_Jade));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Leia, Filters.and(Filters.Luke, Filters.not(Filters.Jedi)), Filters.Mara_Jade));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Leia, Filters.and(Filters.Luke, Filters.not(Filters.Jedi)), Filters.Mara_Jade);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }

        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.any, Filters.here(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && self.getAttachedTo() != null
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)
                && GameConditions.canCancelDestiny(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny instead of firing");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new InsteadOfFiringWeaponEffect(action, self, new CancelDestinyEffect(action)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), 1, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }
}
