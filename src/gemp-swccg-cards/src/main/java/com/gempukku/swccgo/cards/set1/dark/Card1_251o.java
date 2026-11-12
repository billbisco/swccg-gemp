package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Imperial Reinforcements
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Imperial Reinforcements]]; reworded by [[Imperial Reinforcements
 * (Errata)]] (Decipher Glossary Supplement (29 January 2002)).
 * "Outnumbers you" and "TIE fighters ... retrieved from Lost Pile" are the current "more total characters and
 * starships" and retrieving TIE/lns.
 * So this state reuses the current card's (Card1_251) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_251o extends Card1_251 {
    public Card1_251o() {
        super();
        setGameText("If opponent outnumbers you in total characters and starships on table, use 1 Force to draw destiny. That number of Stormtroopers and/or TIE fighters may be retrieved from Lost Pile.");
        setTextState(new TextState("1_251", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }
}
