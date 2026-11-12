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
 * Title: Planet Defender Ion Cannon
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Planet Defender Ion Cannon]] and its Decipher archive scan (Hoth-L-planetdefenderioncannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: "all weapons aboard target are lost" (also character weapons on cards aboard), not only starship weapons deployed on it (FireWeaponActionBuilder.ionHitLosesAllWeaponsAboard).
 */
public class Card3_078o extends Card3_078 {
    public Card3_078o() {
        super();
        setGameText("Deploy on an exterior site at a Rebel Base. Cannot be moved. During a battle at related system, may target a capital starship there using 2 Force. Draw destiny. If destiny +3 > armor, all weapons aboard target are lost, power = 0 and hyperspeed = 0.");
        setTextState(new TextState("3_78", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtRelatedSystemUsingForce(Filters.capital_starship, 2, TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.ionHitLosesAllWeaponsAboard().buildFireWeaponPlanetDefenderIonCannonAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
