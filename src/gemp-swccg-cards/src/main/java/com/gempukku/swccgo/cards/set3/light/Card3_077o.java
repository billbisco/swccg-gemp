package com.gempukku.swccgo.cards.set3.light;

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
 * Title: Medium Repeating Blaster Cannon
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Medium Repeating Blaster Cannon]] and its Decipher archive scan (Hoth-L-mediumrepeatingblastercannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: targets exactly two characters (not "up to two characters or two creatures"), hit if total destiny > total ability (FireWeaponActionBuilder.characterDefenseValueIsAbility).
 */
public class Card3_077o extends Card3_077 {
    public Card3_077o() {
        super();
        setGameText("Deploy on any site. May be moved by two warriors for 1 additional Force. Your warrior present may target two characters at same or adjacent site using 2 Force. Draw two destiny. Characters hit if total destiny > total ability.");
        setTextState(new TextState("3_77", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(2, Filters.or(Filters.character, targetedAsCharacter), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponByCharacterPresentWithHitAction(2);
            return Collections.singletonList(action);
        }
        return null;
    }
}
