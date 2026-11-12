package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Red Leader
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) text, from wiki.swccg.com [[Red Leader (Errata)]]. It adds "This
 * character is a leader." to the current text; the current card (Card1_029) already has the leader keyword, so this
 * state reuses its game text code and differs only in its text and text-state information.
 */
public class Card1_029d extends Card1_029 {
    public Card1_029d() {
        super();
        setGameText("Adds 2 to power of anything he pilots. When piloting Red 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Adds 1 to forfeit of each other Red Squadron pilot at same location. This character is a leader. (See characteristics.)");
        setTextState(new TextState("1_29", "D-E1", "Decipher Glossary Version 2.0, November 1998",
                LocalDate.of(1998, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
