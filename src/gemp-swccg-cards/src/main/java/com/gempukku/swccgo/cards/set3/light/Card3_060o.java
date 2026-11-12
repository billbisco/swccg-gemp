package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Hoth: Echo Med Lab
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Hoth: Echo Med Lab]] (Decipher archive scan); the current text is [[Hoth: Echo Med Lab (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card3_060o extends Card3_060 {
    public Card3_060o() {
        super();
        setLocationLightSideGameText("Once per turn, one of your medical droids is deploy -2.");
        setLocationDarkSideGameText("If you control, with an Imperial present, Force drain +1 here.");
        setTextState(new TextState("3_60", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
