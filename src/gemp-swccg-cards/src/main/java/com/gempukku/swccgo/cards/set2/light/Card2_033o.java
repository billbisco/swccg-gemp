package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Grappling Hook
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Grappling Hook]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Grappling Hook (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed card has no Grabber icon (the errata added it); otherwise this state plays as the bare card (Card2_033).
 */
public class Card2_033o extends Card2_033 {
    public Card2_033o() {
        super();
        setGameText("Use 1 Force to deploy on table just after opponent has played any Interrupt. That Interrupt is still played out but is then 'grappled' (placed here but is out of play). Any new Interrupts of the same name are now unique (•). (Immune to Control.)");
        removeIcon(Icon.GRABBER);
        setTextState(new TextState("2_33", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
