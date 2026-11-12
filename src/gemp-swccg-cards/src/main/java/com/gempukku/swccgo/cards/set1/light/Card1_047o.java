package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Demotion
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Demotion]]; reworded by [[Demotion (Errata)]] (Decipher Glossary
 * Supplement (29 January 2002)).
 * Only the wording of the excluded Imperials (Darth Vader, Emperor Palpatine, Stormtrooper) and of the canceled game
 * text changed.
 * So this state reuses the current card's (Card1_047) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_047o extends Card1_047 {
    public Card1_047o() {
        super();
        setGameText("Use 2 Force to deploy on any Imperial, except Darth Vader, Emperor Palpatine or Stormtrooper. That Imperial is power -2 and cannot use its \"game text.\"");
        setTextState(new TextState("1_47", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }
}
