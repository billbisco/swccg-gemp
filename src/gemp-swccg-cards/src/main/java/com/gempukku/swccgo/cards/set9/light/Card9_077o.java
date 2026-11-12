package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Luminous
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Luminous]] (Decipher archive scan); the current text is
 * [[Luminous (PC Errata)]]. The current card's code already plays the printed text (its display text is the print; ship-docking capability is not modelled in GEMP and Covert Landing is allowed through MayBeTargetedByModifier); the PC errata (no ship-docking capability, Covert Landing "even while landed") is not implemented separately. This state reuses that code. The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_077o extends Card9_077 {
    public Card9_077o() {
        super();
        setGameText("Deploys and moves like a starfighter. May add 1 pilot and 6 passengers. Has ship-docking capability. Permanent pilot provides ability of 2. May use Covert Landing like a shuttle or starfighter.");
        setTextState(new TextState("9_77", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
