package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Title: 5D6-RA-7 (Fivedesix)
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[5D6-RA-7 (Fivedesix)]] (Decipher archive scan); the current text is [[5D6-RA-7 (Fivedesix) (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_163o extends Card1_163 {
    public Card1_163o() {
        super();
        setGameText("Power +1 if at same site as Motti or Yularen. Deploy +1 for all opponent's droids (and your 'mouse' droids) at same location. Immune to attrition during 'nighttime conditions.'");
        setTextState(new TextState("1_163", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
