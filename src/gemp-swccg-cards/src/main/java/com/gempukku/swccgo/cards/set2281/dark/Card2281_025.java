package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Mara Jade's Lightsaber (V)
 * Original version of 1281_25 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_025 extends Card1281_025 {
    public Card2281_025() {
        super();
        setGameText("Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1281_25", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
