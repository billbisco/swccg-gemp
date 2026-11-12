package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Death Squadron
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "May be shuttled" and "may shuttle" are the same; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_100o extends Card3_100 {
    public Card3_100o() {
        super();
        setGameText("Deploy on any system. Adds X to total power of your starships at the system, where X = the number of your starships present. Your troopers and combat vehicles may be shuttled to related sites for free.");
        setTextState(new TextState("3_100", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
