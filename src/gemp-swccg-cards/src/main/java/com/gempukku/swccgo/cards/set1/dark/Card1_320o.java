package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.GameConditions;
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
 * Title: Light Repeating Blaster Rifle
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Light Repeating Blaster Rifle]] and its Decipher archive scan (Premiere-D-lightrepeatingblasterrifle-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed weapon text: a character target is compared to its ability, not its defense value (ability or armor), and creatures cannot be targeted (FireWeaponActionBuilder.characterDefenseValueIsAbility). "Any warrior" deploys only on your own warrior (Decipher Glossary 2.0, "weapons — deploying and carrying"). The power source may be either player's ("a power droid or any fusion generator").
 */
public class Card1_320o extends Card1_320 {
    public Card1_320o() {
        super();
        setGameText("Use 2 Force to deploy on any warrior. If a power droid or any fusion generator present, may target using 2 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor. May fire repeatedly for 2 Force each time.");
        setTextState(new TextState("1_320", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        // Printed: "If a power droid or any fusion generator present" (either player's)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.or(Filters.power_droid, Filters.fusion_generator), Filters.present(self)))) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
        }
        return null;
    }
}
