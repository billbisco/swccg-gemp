package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Rogue Asteroid
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Rogue Asteroid]] (Decipher archive scan); the current text is [[Rogue Asteroid (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_035o extends Card4_035 {
    public Card4_035o() {
        super();
        setGameText("Deploy on an asteroid sector and specify starting direction. Every move phase, moves to next adjacent asteroid sector (reversing direction as necessary). Where present, adds 2 to asteroid destiny. May be targeted by starship weapons (armor = 3).");
        setTextState(new TextState("4_35", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
