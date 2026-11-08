package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Subtype: Immediate
 * Title: Redeemed Apprentice
 * Original version of 1281_33 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-07-21, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_033 extends Card1281_033 {
    public Card2281_033() {
        super();
        setGameText("If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a (Virtual Block 4) objective targeting your non-Jedi character, reduce loss by 1. Elis Helrot and Nabrun Leids are canceled. (Immune to Control.)");
        setLegacyVersion(new LegacyVersion("1281_33", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
