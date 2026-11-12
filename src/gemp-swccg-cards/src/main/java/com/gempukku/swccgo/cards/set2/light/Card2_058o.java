package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Wookiee Roar
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Wookiee Roar]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Wookiee Roar (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_058) and differs only in its game text and text-state information.
 */
public class Card2_058o extends Card2_058 {
    public Card2_058o() {
        super();
        setGameText("If Chewbacca is defending a battle alone at a site, add two battle destiny. OR Use 3 Force to search through your Reserve Deck and take Chewbacca into your hand. Shuffle deck, cut and replace. OR Scare off (lose) one 'mouse' droid on table.");
        setTextState(new TextState("2_58", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
