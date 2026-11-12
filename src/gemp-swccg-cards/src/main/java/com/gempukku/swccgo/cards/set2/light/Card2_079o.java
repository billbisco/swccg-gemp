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
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Weapon
 * Title: Luke's Hunting Rifle
 * Text state: o (as printed, July 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Luke's Hunting Rifle]] and its Decipher archive scan (ANH-L-lukeshuntingrifle-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: a character is hit if the destiny beats its ability; a creature is not hit but immediately lost (two fire actions: character / creature).
 */
public class Card2_079o extends Card2_079 {
    public Card2_079o() {
        super();
        setGameText("Use 1 Force to deploy on Luke or Owen Lars, 3 on any non-droid character (even if not a warrior). May target using 1 Force. Draw destiny. Character hit if destiny -1 > ability. Creature immediately lost if destiny +2 > defense value.");
        setTextState(new TextState("2_79", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();
        // Printed: "Character hit if ... > ability."
        FireWeaponActionBuilder characterBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter), 1, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (characterBuilder != null) {
            actions.add(characterBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE));
        }
        // Printed: "Creature immediately lost if destiny +2 > defense value." (the +2 is the bare card's creature modifier)
        FireWeaponActionBuilder creatureBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.creature, 1, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (creatureBuilder != null) {
            actions.add(creatureBuilder.buildFireWeaponLostAction(1, Statistic.DEFENSE_VALUE));
        }
        return actions.isEmpty() ? null : actions;
    }
}
