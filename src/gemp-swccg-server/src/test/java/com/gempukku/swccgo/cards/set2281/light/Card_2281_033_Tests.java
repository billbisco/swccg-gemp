package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Redeemed Apprentice: original version of 1281_33 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_033_Tests {
    @Test
    public void RedeemedApprenticeVersionIdentityAndText() {
        assertVersion("2281_33", "1281_33", "Redeemed Apprentice",
                "If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a (Virtual Block 4) objective targeting your non-Jedi character, reduce loss by 1. Elis Helrot and Nabrun Leids are canceled. (Immune to Control.)",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_33", "1281_33");
    }
}
