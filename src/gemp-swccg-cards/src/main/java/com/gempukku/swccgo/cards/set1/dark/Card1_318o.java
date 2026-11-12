package com.gempukku.swccgo.cards.set1.dark;

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
 * Set: Premiere
 * Type: Weapon
 * Title: Ion Cannon
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Ion Cannon]] and its Decipher archive scan (Premiere-D-ioncannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: "all weapons aboard target are lost" (also character weapons on cards aboard), not only starship weapons deployed on it (FireWeaponActionBuilder.ionHitLosesAllWeaponsAboard).
 */
public class Card1_318o extends Card1_318 {
    public Card1_318o() {
        super();
        setGameText("Use 2 Force to deploy on any Star Destroyer. May target using 1 Force. Draw destiny. If destiny +2 > target's armor or maneuver, all weapons aboard target are lost, armor or maneuver = 0 and hyperspeed = 0.");
        setTextState(new TextState("1_318", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.starship, Filters.canBeTargetedByWeaponAsStarfighter), 1, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.ionHitLosesAllWeaponsAboard().buildFireWeaponIonCannonAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
