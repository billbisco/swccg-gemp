package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_023;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Dreaded Imperial Starfleet
 * PC-E1 version of 1240_23. Order (Bill 2026-11-10, ruling H1; 2026-11-11 every publication is a version): the
 * September 2009 first printing is lost; PC-E1 (2240_23) = the PC card image (in the vkit copy by 2009-12-09; the 9
 * September 2009 errata), “Imperial-Class”; PC-E2 (2240_1023) = the Virtual Block 4 release sheet (2009-12-07), card
 * #22E, “Imperial-class”; PC-E3 (2240_2023) = the PC website card list (by 2012-07-21), “Imperial-Class”; PC-E4 = the
 * Master List (2013-04-27), “Imperial-class”, the current card. Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_023 extends Card1240_023 {
    public Card2240_023() {
        super();
        setGameText("Deploy on table. While you have more systems on table than opponent, your capital starships may deploy at -2 (to a maximum of -3) to battlegrounds. When you deploy an Imperial-Class Star Destroyer, may deploy (for free) a [Death Star II] or [A New Hope] squadron there (no replacement necessary) from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_23", "PC-E1",
                "PC card image (9 September 2009 errata; vkit copy 2009-12-09); superseded by the release sheet (2009-12-07)", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
