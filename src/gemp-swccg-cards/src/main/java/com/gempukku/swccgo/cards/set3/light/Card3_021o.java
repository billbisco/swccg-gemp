package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Tauntaun Handler
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Drives" and "rides" a creature vehicle are the same, as are "on a tauntaun" and "riding a tauntaun"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_021o extends Card3_021 {
    public Card3_021o() {
        super();
        setGameText("Adds 2 to power of any creature vehicle he drives. On a tauntaun, draws one battle destiny if not able to otherwise.");
        setTextState(new TextState("3_21", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
