package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Droid
 * Title: DSD1 Dwarf Spider Droid
 */
public class Card1291_003 extends AbstractDroid {
    public Card1291_003() {
        super(Side.DARK, 3, 2, 2, 2, "DSD1 Dwarf Spider Droid", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(3);
        setGameText("While with a [Clone Army] card or another spider droid, draws one battle destiny if unable to otherwise. Permanent weapon is Self-Destruct (may target a character present; draw destiny; if destiny +2 > defense value, target hit and forfeit -4, and this character also hit).");
        addIcons(Icon.PRESENCE, Icon.SEPARATIST, Icon.WARRIOR, Icon.EPISODE_I, Icon.PERMANENT_WEAPON);
        addModelType(ModelType.SPIDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter cloneArmyCardOrOtherSpiderDroid = Filters.or(Icon.CLONE_ARMY,
                Filters.and(Filters.other(self), Filters.droid, ModelType.SPIDER));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new WithCondition(self, cloneArmyCardOrOtherSpiderDroid), 1));
        return modifiers;
    }

    // Define "Self-Destruct" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon("Self-Destruct") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .target(Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.present(self)), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE, false, -4);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // "and this character also hit"
        if (TriggerConditions.justHitBy(game, effectResult, Filters.not(self), Filters.permanentWeaponOf(self), self)
                && !self.isHit()) {
            HitResult hitResult = (HitResult) effectResult;
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Self-Destruct: this character also hit");
            action.appendEffect(new HitCardEffect(action, self, self, hitResult.getHitByPermanentWeapon(), self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
