package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Undercover (Dark)
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Undercover (Dark)]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Undercover (Dark) (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed text has no "(Immune to Alter.)" (the errata added it); otherwise this state plays as the bare card (Card2_129).
 */
public class Card2_129o extends Card2_129 {
    public Card2_129o() {
        super();
        setGameText("Deploy on your spy at a site and cross spy to opponent's side. Spy is now Undercover. If at a site during your deploy phase, may voluntarily \"break cover\" (lose Effect).");
        removeImmuneToCardTitle(Title.Alter);
        setTextState(new TextState("2_129", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
