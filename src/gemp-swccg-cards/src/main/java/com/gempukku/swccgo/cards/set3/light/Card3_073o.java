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
 * Title: Concussion Grenade
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Concussion Grenade]] and its Decipher archive scan (Hoth-L-concussiongrenade-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: cards "at that site" are lost (also those aboard vehicles and starships there), not only cards "present" (FireWeaponActionBuilder.hitsCardsAtSiteNotOnlyPresent).
 */
public class Card3_073o extends Card3_073 {
    public Card3_073o() {
        super();
        setGameText("Use 2 Force to deploy on any warrior. May 'throw' at same or adjacent site. Draw destiny. All characters, weapons and devices with that destiny number at that site are lost. (Only your warrior is lost if destiny = 0.) Concussion Grenade also lost.");
        setTextState(new TextState("3_73", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .firesWithoutTargetingAtSameOrAdjacentSite().finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.hitsCardsAtSiteNotOnlyPresent().buildFireWeaponConcussionGrenadeAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
