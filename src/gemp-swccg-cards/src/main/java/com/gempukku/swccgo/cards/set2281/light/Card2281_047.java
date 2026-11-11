package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
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

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Kyle Katarn's Blaster Rifle
 * Original version of 1281_47, printed as Rebel Agent's Blaster Rifle on the Virtual Block 8.1 release sheet (May 2012); Rebel Agent is the original title of Kyle Katarn. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_047 extends AbstractCharacterWeapon {
    public Card2281_047() {
        super(Side.LIGHT, 2, "Kyle Katarn's Blaster Rifle", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("BlasTech E-11 blaster rifle. Standard issue for Imperial forces. So numerous that many have been stolen by Rebels. Extendable stock. Carries energy for 100 shots.");
        setGameText("Deploy on Rebel Agent. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or creature hit, and its forfeit = 0, if total destiny > defense value. Vehicle hit if total destiny -2 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_47", "", "Virtual Block 8.1 release sheet (2012-05-11), printed as Rebel Agent's Blaster Rifle", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
        setDisplayTitle("Rebel Agent's Blaster Rifle");
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
