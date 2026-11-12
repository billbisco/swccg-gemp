package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Weapon
 * Title: Jawa Ion Gun
 * Text state: o (as printed, July 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Jawa Ion Gun]] and its Decipher archive scan (ANewHope-L-jawaiongun-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: a non-droid character is excluded from battle if destiny = ability (not defense value) (FireWeaponActionBuilder.characterDefenseValueIsAbility).
 */
public class Card2_078o extends Card2_078 {
    public Card2_078o() {
        super();
        setGameText("Use 1 Force to deploy on any Jawa, 3 on any warrior. May target using 1 Force. Draw destiny. Droid immediately 'stolen' if destiny +1 > forfeit. Non-droid character excluded from battle if destiny = ability.");
        setTextState(new TextState("2_78", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.droid, 1, TargetingReason.TO_BE_STOLEN)
                .targetUsingForce(Filters.or(Filters.non_droid_character, targetedAsCharacter), 1, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponJawaIonGunAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
