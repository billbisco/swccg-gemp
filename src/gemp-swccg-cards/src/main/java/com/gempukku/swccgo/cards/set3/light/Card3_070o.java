package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Tauntaun
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Passenger" is the rider, and "deploy only on Hoth, but may move elsewhere" is how the current "deploy only on Hoth" plays; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_070o extends Card3_070 {
    public Card3_070o() {
        super();
        setGameText("May carry 1 passenger. Deploy only on Hoth, but may move elsewhere. Ability = 1/4. May move as a 'react' from a battle. May sacrifice (lose) to make passenger immune to Exposure this turn.");
        setTextState(new TextState("3_70", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
