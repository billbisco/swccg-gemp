package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7
 * Type: Character
 * Subtype: Republic
 * Title: Grievous, Hunter Of Jedi (printed as Cyborg Commander, Hunter Of Jedi)
 *
 * PC-E1 version of 1271_1: the PC website card list text (VBlock7Type, by 2012-07-21, same to 2013-01-11), still titled
 * Cyborg Commander, Hunter Of Jedi: "may deploy on Cyborg." and "your battle destiny draws are +1 here and Cyborg
 * Commander's forfeit may not be reduced". Superseded by the persona change to Grievous (2013-01-29) and the Virtual
 * Cards Master List (Apr 2013, PC-E2, the current card). Plays as the original (2271_1). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1001 extends Card2271_001 {
    public Card2271_1001() {
        setGameText("[Virtual Block 5] Dark Jedi Lightsaber may deploy on Cyborg. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws are +1 here and Cyborg Commander's forfeit may not be reduced. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1271_1", "PC-E1",
                "PC website card list (by 2012-07-21; last seen 2013-01-11); superseded by the Virtual Cards Master List (2013-04-27)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
