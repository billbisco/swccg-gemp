package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Device
 * Title: Droid Detector
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Droid Detector]]; reworded by [[Droid Detector (Errata)]]
 * (Decipher Glossary Supplement (29 January 2002)).
 * Droids at the site are lost at the end of any turn after the turn it deploys (the printed "move away by the end of
 * the opponent's next turn, or they are lost"), as the current card does.
 * So this state reuses the current card's (Card1_202) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_202o extends Card1_202 {
    public Card1_202o() {
        super();
        setGameText("Deploy at any interior site. Cannot be moved. All droids there must move away (at normal use of the Force) by the end of the opponent's next turn, or they are lost. Droids may not deploy to same site and are lost if present at end of any turn.");
        setTextState(new TextState("1_202", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }
}
