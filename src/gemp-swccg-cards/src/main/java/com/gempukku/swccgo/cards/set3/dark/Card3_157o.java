package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Blizzard Walker
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: The printed card does not say "Landspeed may not be increased" (added by Decipher Glossary 2.0, November 1998); GEMP enforces that as a game rule for every AT-AT (AT_AT_LandspeedRule), not as card text, so this state plays as the current card.
 */
public class Card3_157o extends Card3_157 {
    public Card3_157o() {
        super();
        setGameText("May add 2 pilots and 8 passengers. Immune to attrition < 4.");
        setTextState(new TextState("3_157", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
