package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Spice Mines Of Kessel
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Spice Mines Of Kessel]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Spice Mines Of Kessel (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_125) and differs only in its game text and text-state information.
 */
public class Card2_125o extends Card2_125 {
    public Card2_125o() {
        super();
        setGameText("Deploy on Kessel (may not be moved). Target one captive and one trooper to escort captive. When targets reach Utinni Effect, retrieve lost Force equal to captive’s forfeit (captive and Utinni Effect lost). If trooper lost en route, captive released.");
        setTextState(new TextState("2_125", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
