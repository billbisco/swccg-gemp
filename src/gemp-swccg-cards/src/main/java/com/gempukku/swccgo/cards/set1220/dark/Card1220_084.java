package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Weapon
 * Subtype: Character
 * Title: Boba Fett's Blaster Rifle
 */
public class Card1220_084 extends AbstractCharacterWeapon {
    public Card1220_084() {
        super(Side.DARK, 1, Title.Boba_Fetts_Blaster_Rifle, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sawed off BlasTech EE-3 blaster rifle. Although its barrel is a few centimeters under the legal limit, no one has lived to file an official complaint.");
        setGameText("Deploy on non-[Maintenance] Boba Fett. During your turn, opponent's characters here may not use landspeed. While a captive at same site, your Force drains are +1 here. May target a character for free. Draw destiny. Target captured if destiny +2 > defense value.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.BLASTER_RIFLE);
        addPersona(Persona.BOBA_FETTS_BLASTER_RIFLE);
        setMatchingCharacterFilter(Filters.and(Filters.Boba_Fett, Filters.not(Filters.icon(Icon.MAINTENANCE))));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Boba_Fett, Filters.not(Filters.icon(Icon.MAINTENANCE)));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.Boba_Fett, Filters.not(Filters.icon(Icon.MAINTENANCE)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_CAPTURED).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponCaptureAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationUsingLandspeedModifier(self,
                Filters.and(Filters.opponents(self), Filters.character),
                new PlayersTurnCondition(self.getOwner()),
                Filters.here(self)));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self),
                new OnTableCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.atSameSite(self))),
                1, self.getOwner()));
        return modifiers;
    }
}
