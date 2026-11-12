package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: There'll Be Hell To Pay
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[There'll Be Hell To Pay]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[There'll Be Hell To Pay (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed card has no Grabber icon (the errata added it); otherwise this state plays as the bare card (Card2_128).
 */
public class Card2_128o extends Card2_128 {
    public Card2_128o() {
        super();
        setGameText("Use 3 Force to deploy on table and stack one just-played Interrupt here. To play any new Interrupt of the same name, player must first stack it here and use +1 Force for each Interrupt in stack, even if Interrupt is normally free. (Immune to Control.)");
        removeIcon(Icon.GRABBER);
        setTextState(new TextState("2_128", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
