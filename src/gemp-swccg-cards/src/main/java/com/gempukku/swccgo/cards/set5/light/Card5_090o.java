package com.gempukku.swccgo.cards.set5.light;

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
 * Set: Cloud City
 * Type: Weapon
 * Title: Luke's Blaster Pistol
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Luke's Blaster Pistol]] and its Decipher archive scan (CloudCity-L-lukesblasterpistol-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed weapon text: a character target is compared to its ability, not its defense value (ability or armor), and creatures cannot be targeted (FireWeaponActionBuilder.characterDefenseValueIsAbility). "Any warrior" deploys only on your own warrior (Decipher Glossary 2.0, "weapons — deploying and carrying"). The character +1 is the bare card's total weapon destiny modifier.
 */
public class Card5_090o extends Card5_090 {
    public Card5_090o() {
        super();
        setGameText("Use 1 Force to deploy on Luke, 3 on any other warrior. May target using 2 Force. (If targeted by Luke, target loses immunity to attrition for remainder of turn.) Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny > armor or maneuver.");
        setTextState(new TextState("5_90", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponLukesBlasterPistolAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
