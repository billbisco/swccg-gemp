package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Weapon
 * Subtype: Character
 * Title: Cloud City Blaster (V)
 */
public class Card1283_028 extends AbstractCharacterWeapon {
    public Card1283_028() {
        super(Side.LIGHT, 2, "Cloud City Blaster", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Black market ties make it possible for the Cloud City quartermaster to acquire powerful weapons such as this. Low refraction index provides minimal beam dispersion.");
        setGameText("Deploy on your alien. When fired in battle, your total power is +1. May target a character for free. Draw destiny. Target hit if destiny +1 > defense value. If carried by Lobot or a trooper, \"1\" on this card is treated as \"2.\"");
        addKeywords(Keyword.BLASTER);
    }

    /**
     * The value of each "1" on this card: 2 while carried by Lobot or a trooper, otherwise 1.
     */
    static int one(SwccgGame game, PhysicalCard self) {
        PhysicalCard carrier = self.getAttachedTo();
        return (carrier != null && Filters.or(Filters.Lobot, Filters.trooper).accepts(game, carrier)) ? 2 : 1;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, one(game, self), Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.weaponJustFired(game, effectResult, self)
                && GameConditions.isDuringBattle(game)) {
            int amount = one(game, self);
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make total power +" + amount);
            action.appendEffect(
                    new ModifyTotalPowerUntilEndOfBattleEffect(action, amount, self.getOwner(), "Makes total power +" + amount));
            return Collections.singletonList(action);
        }
        return null;
    }
}
