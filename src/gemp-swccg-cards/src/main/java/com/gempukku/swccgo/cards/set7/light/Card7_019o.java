package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Grondorn Muse
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print names "Yavin 4 Sentry"; the Players Committee Advanced Rulebook (Appendix A) writes the card's real title,
 * "Yavin Sentry". The card meant is the same, so this state reuses the bare card's code (Card7_019) and differs only
 * in its game text and text-state information.
 */
public class Card7_019o extends Card7_019 {
    public Card7_019o() {
        super();
        setGameText("While Grondorn is on Yavin 4, your Yavin 4 Sentry is not unique (•), is doubled, deploys free, applies all three of its modifiers and is immune to Alter. Power -1 when not on Yavin 4.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_19", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
