package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Weapon
 * Subtype: Character
 * Title: Black Market Blaster
 */
public class Card1283_027 extends AbstractCharacterWeapon {
    public Card1283_027() {
        super(Side.LIGHT, 4, "Black Market Blaster", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Illegal weapon easily purchased by Rebel suppliers on the black market. Rechargeable energy packs make it a cost-effective weapon.");
        setGameText("Deploy on Kyle Katarn or your smuggler. May target a character or creature for free. Draw destiny. Target hit, and may not be used to satisfy attrition, if destiny +2 > defense value. If fired by Kyle Katarn, he is immunity to attrition +1 for remainder of turn.");
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.persona(Persona.KYLE_KATARN));
    }

    private static Filter kyle() {
        return Filters.persona(Persona.KYLE_KATARN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(kyle(), Filters.smuggler));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(kyle(), Filters.smuggler);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildBlasterRifleVAction(2);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.weaponJustFiredBy(game, effectResult, self, kyle())) {
            PhysicalCard kyle = ((FiredWeaponResult) effectResult).getCardFiringWeapon();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(kyle) + " immunity to attrition +1");
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new ImmunityToAttritionChangeModifier(self, Filters.sameCardId(kyle), 1),
                            "Makes " + GameUtils.getCardLink(kyle) + "'s immunity to attrition +1"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
