package com.gempukku.swccgo.cards.set4.dark;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Weapon
 * Title: IG-88's Pulse Cannon
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[IG-88's Pulse Cannon]] and its Decipher archive scan (Dagobah-D-ig88spulsecannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed weapon text: a character target is compared to its ability, not its defense value (ability or armor), and creatures cannot be targeted (FireWeaponActionBuilder.characterDefenseValueIsAbility). "Any warrior" deploys only on your own warrior (Decipher Glossary 2.0, "weapons — deploying and carrying").
 */
public class Card4_178o extends Card4_178 {
    public Card4_178o() {
        super();
        setGameText("Use 1 Force to deploy on IG-88, 4 on any other warrior. Adds 2 to power. May target X non-droid characters using X Force. Draw destiny for each. If destiny = 0, character is power -1 and forfeit -1 until end of turn. If destiny -1 > ability, character hit.");
        setTextState(new TextState("4_178", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        if (forFree) {
            return null;
        }
        // Printed: "May target X non-droid characters" (no creatures); "If destiny -1 > ability, character hit."
        Filter targetFilter = Filters.or(Filters.non_droid_character, targetedAsCharacter);
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();
        int forceAvailable = GameConditions.forceAvailableToUse(game, playerId);
        int maxX = Math.min(Math.max(forceAvailable, 0), 9);
        for (int x = 1; x <= maxX; x++) {
            if (!GameConditions.canUseForce(game, playerId, x + extraForceRequired)) {
                break;
            }
            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                    .targetUsingForce(x, targetFilter, x, TargetingReason.TO_BE_HIT).finishBuildPrep();
            if (actionBuilder != null) {
                actions.add(actionBuilder.characterDefenseValueIsAbility().buildFireWeaponIG88sPulseCannonAction());
            }
        }
        return actions.isEmpty() ? null : actions;
    }
}
