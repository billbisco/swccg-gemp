package com.gempukku.swccgo.cards.set3.dark;

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
 * Set: Hoth
 * Type: Weapon
 * Title: AT-AT Cannon
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[AT-AT Cannon]] and its Decipher archive scan (Hoth-D-atatcannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed weapon text: a character target is compared to its ability, not its defense value (ability or armor), and creatures cannot be targeted (FireWeaponActionBuilder.characterDefenseValueIsAbility). "Any warrior" deploys only on your own warrior (Decipher Glossary 2.0, "weapons — deploying and carrying"). Printed: "May select a target only at an adjacent site"; a starfighter is hit if destiny > 3 (defense value 3, as the bare card).
 */
public class Card3_158o extends Card3_158 {
    public Card3_158o() {
        super();
        setGameText("Use 2 Force to deploy on any AT-AT. May fire during a battle at same or adjacent site. May select a target only at an adjacent site using 2 Force. Draw destiny. Vehicle hit if destiny +2 > armor or maneuver. Starfighter hit if destiny > 3. Character hit if destiny +1 > ability.");
        setTextState(new TextState("3_158", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(Filters.and(Filters.or(Filters.starfighter, Filters.character, targetedAsCharacter, Filters.vehicle), Filters.at(Filters.adjacentSite(self))), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE, Filters.starfighter, 3f);
            return Collections.singletonList(action);
        }
        return null;
    }
}
