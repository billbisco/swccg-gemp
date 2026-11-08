package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfAttackModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Weapon
 * Subtype: Character
 * Title: Galen's Lightsaber
 */
public class Card1240_064 extends AbstractCharacterWeapon {
    public Card1240_064() {
        super(Side.DARK, 1, "Galen's Lightsaber", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("");
        setGameText("Deploy on Galen. While Galen defending a battle, his and Juno Eclipse's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. Target hit, power -3, and may not be used to satisfy attrition if total destiny > defense value.");
        addKeywords(Keyword.LIGHTSABER);
        addIcons(Icon.CLOUD_CITY);
        addPersona(Persona.GALENS_LIGHTSABER);
        setMatchingCharacterFilter(Filters.Galen);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Galen);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Galen;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition galenDefending = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard galen = self.getAttachedTo();
                return galen != null && Filters.defendingBattle.accepts(gameState, modifiersQuerying, galen);
            }
        };
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmunityToAttritionChangeModifier(self,
                Filters.or(Filters.hasAttached(self), Filters.Juno), galenDefending, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.or(Filters.character, Filters.creature), self)) {
            final PhysicalCard hit = ((HitResult) effectResult).getCardHit();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Target is power -3");
            // Weapons fire during a battle or during an attack; the power reduction lasts for that combat.
            PowerModifier powerMinusThree = new PowerModifier(self, hit, -3);
            if (GameConditions.isDuringBattle(game)) {
                action.appendEffect(new AddUntilEndOfBattleModifierEffect(action, powerMinusThree, "Target is power -3"));
            } else if (GameConditions.isDuringAttack(game)) {
                action.appendEffect(new AddUntilEndOfAttackModifierEffect(action, powerMinusThree, "Target is power -3"));
            } else {
                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, powerMinusThree, "Target is power -3"));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.builderDarkAndLightJediLightSaberV();
            return Collections.singletonList(action);
        }
        return null;
    }
}
