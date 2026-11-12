package com.gempukku.swccgo.cards.set4.dark;

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
 * Set: Dagobah
 * Type: Weapon
 * Subtype: Character
 * Title: Zuckuss' Snare Rifle
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Dagobah (Decipher, April 1997) text from wiki.swccg.com [[Zuckuss' Snare Rifle]] and its Decipher archive scan
 * (Dagobah-D-zuckusssnarerifle-decipher-archive.gif); superseded by the Decipher Glossary Version 2.0 errata
 * (November 1998; [[Zuckuss' Snare Rifle (Errata)]]: "May target a character or creature ... Character captured if
 * destiny -1 &gt; defense value"), which the bare card plays.
 * <p>
 * Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 5): the printed rifle may target anything; it only
 * has an effect on a character or creature (so it can target e.g. a Rebel Snowspeeder, which still counts as your
 * bounty hunter firing a character weapon for Jabba's Through With You). "May be immediately captured": the choice
 * belongs to the target character's owner, not the shooter (FireWeaponActionBuilder.captureIsTargetOwnersChoice). A
 * character is compared to its ability (characterDefenseValueIsAbility); a creature to its defense value.
 */
public class Card4_180o extends Card4_180 {
    public Card4_180o() {
        super();
        setGameText("Deploy on Zuckuss, or use 1 Force to deploy on any other bounty hunter. May target using 2 Force. Draw destiny. Character may be immediately captured if destiny -1 > ability. Creature immediately lost if destiny +1 > defense value.");
        setTextState(new TextState("4_180", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        Filter characterTarget = Filters.or(Filters.character, targetedAsCharacter);
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(characterTarget, 2, TargetingReason.TO_BE_CAPTURED)
                .targetUsingForce(Filters.creature, 2, TargetingReason.TO_BE_LOST)
                .targetUsingForce(Filters.not(Filters.or(characterTarget, Filters.creature)), 2, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().captureIsTargetOwnersChoice().buildFireWeaponZuckussSnareRifleAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
