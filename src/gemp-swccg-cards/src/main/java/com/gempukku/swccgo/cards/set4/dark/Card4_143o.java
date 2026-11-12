package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: HoloNet Transmission
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Search your Used Pile and take ... Shuffle, cut and replace" is the long form of the current "take ... from Used Pile; reshuffle"; Decipher's Glossary Supplement (2002) only reworded it.
 */
public class Card4_143o extends Card4_143 {
    public Card4_143o() {
        super();
        setGameText("USED: Cancel Transmission Terminated. LOST: Search your Used Pile and take one Imperial or Visage Of The Emperor into hand. Shuffle, cut and replace.");
        setTextState(new TextState("4_143", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2002, 1, 29)));
    }
}
