package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Title: Knowledge And Defense
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher's Glossary Supplement (29 January 2002) text: ", but" became "and"; it plays as the current card (Card4_125).
 */
public class Card4_125d extends Card4_125 {
    public Card4_125d() {
        super();
        setGameText("Insert in opponent's Reserve Deck. When Effect reaches top it is lost and opponent may not initiate any battles for remainder of turn. (Immune to Alter.)");
        setTextState(new TextState("4_125", "D-E1",
                "Decipher Glossary Supplement, 29 January 2002",
                LocalDate.of(2002, 1, 29), LocalDate.of(2017, 3, 1)));
    }
}
