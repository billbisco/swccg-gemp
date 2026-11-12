package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: What Is Thy Bidding, My Master?
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Emperor Palpatine" is the Emperor; Decipher's Glossary Supplement (2002) only reworded it.
 */
public class Card4_042o extends Card4_042 {
    public Card4_042o() {
        super();
        setGameText("Deploy on Emperor Palpatine or any system. Target one character whose lore uses the word 'Emperor.' Target cannot use ability to draw battle destiny and is excluded from being the \"highest-ability character.\" Utinni Effect canceled when reached by target.");
        setTextState(new TextState("4_42", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2002, 1, 29)));
    }
}
