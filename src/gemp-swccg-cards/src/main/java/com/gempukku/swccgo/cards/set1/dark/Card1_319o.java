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
 * Title: Laser Projector
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Laser Projector]] and its Decipher archive scan (Premiere-D-laserprojector-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: characters are compared to their ability, creatures cannot be targeted; a seeker is hit if destiny > 2 (destiny -1 > 1, as the bare card).
 */
public class Card1_319o extends Card1_319 {
    public Card1_319o() {
        super();
        setGameText("Use 2 Force to deploy at an interior site. Cannot move. May target for free. Draw destiny. Character hit if destiny -1 > ability. Seeker hit if destiny > 2. Laser Projector may be targeted by another weapon and hit if destiny > 1.");
        setTextState(new TextState("1_319", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, Filters.seeker, 1f, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.seeker, Filters.character), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(1, -1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
