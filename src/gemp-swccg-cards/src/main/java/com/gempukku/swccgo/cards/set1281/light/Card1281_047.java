package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Kyle Katarn's Blaster Rifle
 */
public class Card1281_047 extends AbstractCharacterWeapon {
    public Card1281_047() {
        super(Side.LIGHT, 2, "Kyle Katarn's Blaster Rifle", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("BlasTech E-11 blaster rifle. Standard issue for Imperial forces. So numerous that many have been stolen by Rebels. Extendable stock. Carries energy for 100 shots.");
        setGameText("Deploy on Kyle Katarn. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or Creature hit, and it's forfeit = 0, if destiny > defense value. Vehicle hit, if destiny -2 > defense value.");
        addKeywords(Keyword.BLASTER_RIFLE);
        setMatchingCharacterFilter(Filters.persona(Persona.KYLE_KATARN));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.persona(Persona.KYLE_KATARN));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.persona(Persona.KYLE_KATARN);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        int numDestiny = GameConditions.canSpot(game, self, Filters.title("Scoundrel's Luck")) ? 2 : 1;
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();

        FireWeaponActionBuilder characterBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (characterBuilder != null) {
            actions.add(characterBuilder.buildFireWeaponWithHitAction(numDestiny, Statistic.DEFENSE_VALUE, true, 0));
        }

        FireWeaponActionBuilder vehicleBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.vehicle, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (vehicleBuilder != null) {
            actions.add(vehicleBuilder.buildFireWeaponWithHitAction(numDestiny, -2, Statistic.DEFENSE_VALUE));
        }

        return actions.isEmpty() ? null : actions;
    }
}
