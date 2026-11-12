package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Bespin Motors Void Spider THX 1138
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Bespin Motors Void Spider THX 1138]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), wiki [[Bespin Motors Void Spider THX 1138 (Errata)]].
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_156) and differs only in its game text and text-state information.
 */
public class Card2_156o extends Card2_156 {
    public Card2_156o() {
        super();
        setGameText("May carry 3 passengers. Permanent pilot aboard provides ability of 1. May move as a 'react.' *Landspeed = 3. OR Up to 3 characters may shuttle to or from same site for free.");
        setTextState(new TextState("2_156", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
