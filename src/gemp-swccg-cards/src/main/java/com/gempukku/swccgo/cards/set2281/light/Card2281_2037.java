package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Luck
 * PC-E2 version of 1281_37: the PC website card list (2012-07-21 to 2012-12-12), which printed the original words (no
 * "(even if inactive)") again after the 2012-07-13 errata (every published text is a version, Bill 2026-11-11).
 * Superseded by the Virtual Cards Master List (2013-04-27, PC-E3, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2037 extends Card1281_037 {
    public Card2281_2037() {
        super();
        setGameText("Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here. Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_37", "PC-E2", "PC website card list (2012-07-21 to 2012-12-12)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected boolean stackAtStartOfTurnEvenIfInactive() {
        return false;
    }
}
