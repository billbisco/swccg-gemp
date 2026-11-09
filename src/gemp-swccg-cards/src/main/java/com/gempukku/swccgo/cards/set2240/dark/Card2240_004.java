package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Colonel Wullf Yularen (V)
 * Original version of 1240_4: the Virtual Block 4 release sheet text (Dec 2009), "Agenda: logistics." (the card list reads "Agendas"); the sheet's "oponent" typo is not kept.
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_004 extends Card1240_004 {
    public Card2240_004() {
        super();
        setGameText("Imperial Council Member. Agenda: logistics. ISB Agents are forfeit +1. While at Conference Room with Sim Aloo, may use 1 Force to subtract 1 from an oponent's just drawn non-Epic Event destiny.");
        setLegacyVersion(new LegacyVersion("1240_4", "",
                "Virtual Block 4 release sheet (2009-12-07), card #03", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
