package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set601.light.Card601_167;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Impressive, Most Impressive (V)
 *
 * PC-E1 version of 1220_145: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1145 extends Card601_167 {
    public Card2220_1145() {
        super();
        setGameText("Cancel All Too Easy, Stunning Leader (where your Jedi participating), Imperial Barrier (targeting your Jedi), or You Are Beaten (except when canceling Uncontrollable Fury). (Immune to Sense.) OR Once per game, if opponent just initiated a battle, you may take the first weapons segment action.");
        setLegacyVersion(new LegacyVersion("1220_145", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
