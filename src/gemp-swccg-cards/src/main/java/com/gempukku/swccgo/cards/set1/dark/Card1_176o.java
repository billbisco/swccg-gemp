package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: Feltipern Trevagg
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Feltipern Trevagg]]; reworded by [[Feltipern Trevagg (PC
 * Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not
 * known)).
 * "If a droid is present, Trevagg's game text is unusable" is the current "while no droid present with Trevagg".
 * So this state reuses the current card's (Card1_176) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_176o extends Card1_176 {
    public Card1_176o() {
        super();
        setGameText("To initiate battles at same location as Trevagg, player must use X Force, where X = total number of [Light Side Force] icons and [Dark Side Force] icons present. If a droid is present, Trevagg's 'game text' is unusable.");
        setTextState(new TextState("1_176", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
