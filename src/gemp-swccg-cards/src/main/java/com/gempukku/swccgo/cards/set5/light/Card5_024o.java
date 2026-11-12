package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Haven
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Haven]] (Decipher archive scan); the current text is [[Haven (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_024o extends Card5_024 {
    public Card5_024o() {
        super();
        setGameText("Deploy on Rendezvous Point, Hoth, Yavin 4, Alderaan or Sullust. Your starships deploy -2 and your pilots deploy -1 here. When battling here, you may add one battle destiny. Effect canceled if opponent controls this system. (Immune to Alter.)");
        setTextState(new TextState("5_24", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
