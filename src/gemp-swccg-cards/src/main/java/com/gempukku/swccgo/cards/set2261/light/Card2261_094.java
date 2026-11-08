package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_258;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Don't Underestimate Our Chances (V)
 * Original version of 1261_94: the Virtual Block 6.1 release sheet text, "If Dantooine Base Operations on table, ▼"
 * (the PC card list, PC-E1, says "Dantooine Operations" and spells out the download). Same play: the card checks
 * for Dantooine Base Operations either way.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_094 extends Card601_258 {
    public Card2261_094() {
        setGameText("USED: Initiate a battle for free. STARTING: If Dantooine Base Operations on table, ▼ Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_94", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #94", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
