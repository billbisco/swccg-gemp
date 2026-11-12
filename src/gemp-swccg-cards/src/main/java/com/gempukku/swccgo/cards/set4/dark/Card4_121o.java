package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Field Promotion
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Emperor Palpatine" is the Emperor; Decipher's Glossary Supplement (2002) only reworded it.
 */
public class Card4_121o extends Card4_121 {
    public Card4_121o() {
        super();
        setGameText("Deploy on an Imperial of ability < 5 present with Vader, Emperor Palpatine or one of your admirals, generals or moffs. Imperial gains '''leader''' skill, is power +1 and is immune to Demotion, Report To Lord Vader and What Is Thy Bidding, My Master?. (Immune to Alter.)");
        setTextState(new TextState("4_121", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2002, 1, 29)));
    }
}
