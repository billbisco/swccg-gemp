package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Keir Santage
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Keir Santage]] (Decipher archive scan); the current text is
 * [[Keir Santage (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_019o extends Card9_019 {
    public Card9_019o() {
        super();
        setGameText("Adds 2 to power of anything he pilots. When at a system, sector or docking bay, once during each of your deploy phases, may subtract 2 from deploy cost of your unique (•) X-wing deploying here.");
        setTextState(new TextState("9_19", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
