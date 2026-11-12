package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: WED15-l7 'Septoid' Droid
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[WED15-l7 'Septoid' Droid]] (Decipher archive scan); superseded by the Players Committee errata, wiki
 * [[WED15-l7 'Septoid' Droid (PC Errata)]] (date unknown; upper bound March 2017).
 * GEMP's bare card (Card2_109) already plays this printed text: it does not implement the errata's "(unless 'hit')",
 * so this state reuses its game text code and differs only in its text-state information.
 */
public class Card2_109o extends Card2_109 {
    public Card2_109o() {
        super();
        setGameText("* Forfeit value begins at 7. When 'forfeited,' droid remains in play, but forfeit value is reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.");
        setTextState(new TextState("2_109", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
