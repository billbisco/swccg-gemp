package com.gempukku.swccgo.cards.set2282.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Under Jabba's Protection: original version of 1282_26 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_026_Tests {
    @Test
    public void UnderJabbaSProtectionVersionIdentityAndText() {
        assertVersion("2282_26", "1282_26", "Under Jabba's Protection",
                "When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it).",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_26", "1282_26");
    }
}
