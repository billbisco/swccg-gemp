package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Blast The Door, Kid!
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) text. The Glossary Supplement (29 January 2002) repeats it and adds
 * that characters of ability &gt; 2 or leaders introduced to the battle after this Interrupt has been played are also
 * immediately excluded, which is how the current card (Card2_045) already plays, so this state reuses its game text
 * code and differs only in its text-state information.
 */
public class Card2_045d extends Card2_045 {
    public Card2_045d() {
        super();
        setGameText("If a battle was just initiated at an interior site, use 1 Force to exclude from that battle all characters of ability > 2 and all leaders (on both sides).");
        setTextState(new TextState("2_45", "D-E1",
                "Decipher Glossary Version 2.0, November 1998; Glossary Supplement, 29 January 2002",
                LocalDate.of(1998, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
