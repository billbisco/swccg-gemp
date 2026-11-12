package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Captain Jonus
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Captain Jonus]] (Decipher archive scan); the current text is
 * [[Captain Jonus (PC Errata)]]. As printed, Captain Jonus satisfies attrition and battle damage when forfeited from a TIE/sa; the PC errata adds "he is piloting". A TIE/sa (TIE bomber) carries only its pilot, so GEMP plays both the same and this state reuses the current card's game text code. The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_101o extends Card9_101 {
    public Card9_101o() {
        super();
        setGameText("Deploys -2 aboard Scimitar 2. Adds 2 to power of anything he pilots. When forfeited from a TIE/sa, also satisfies all remaining attrition and battle damage against you.");
        setTextState(new TextState("9_101", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
