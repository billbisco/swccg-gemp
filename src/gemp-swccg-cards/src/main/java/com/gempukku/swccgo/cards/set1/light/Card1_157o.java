package com.gempukku.swccgo.cards.set1.light;

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
 * Subtype: Character
 * Title: Obi-Wan's Lightsaber
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Obi-Wan's Lightsaber]] and its Decipher archive scan (Premiere-L-obiwanslightsaber-decipher-archive.gif); superseded by the Decipher
 * Glossary Version 2.0 errata (November 1998), which the bare card plays.
 * <p>
 * Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 4): "you may then use as follows: ... OR ..." lists
 * two separate abilities (add 1 to Force drain where present; fire), not one choice per turn, as the bare card plays
 * them. Printed weapon text (docs/legacy-versions.md "Printed weapons"): a character target is compared to its ability,
 * not its defense value (FireWeaponActionBuilder.characterDefenseValueIsAbility), and creatures cannot be targeted.
 * "(for free)": the bare card also deploys on Obi-Wan for free.
 */
public class Card1_157o extends Card1_157 {
    public Card1_157o() {
        super();
        setGameText("Deploy only on Obi-Wan (for free), and you may then use as follows: Add 1 to Force drain where present. OR Target a character for free. Draw two destiny. Character hit, and its forfeit = 0, if total destiny > ability.");
        setTextState(new TextState("1_157", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }
}
