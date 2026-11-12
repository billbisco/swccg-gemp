package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Commander Vanden Willard
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Commander Vanden Willard]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), wiki [[Commander Vanden Willard (Errata)]].
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_005) and differs only in its game text and text-state information.
 */
public class Card2_005o extends Card2_005 {
    public Card2_005o() {
        super();
        setGameText("When at a war room you control, Rebel starships at related system are power +1. May use 1 Force to cancel Astromech Shortage.");
        setTextState(new TextState("2_5", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
