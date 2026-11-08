package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Han's Blaster, So Uncivilized: original version of 1281_46 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_046_Tests {
    @Test
    public void HanSBlasterSoUncivilizedVersionIdentityAndText() {
        assertVersion("2281_46", "1281_46", "Han's Blaster, So Uncivilized",
                "Deploy on Han. I've Got A Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_46", "1281_46");
    }
}
