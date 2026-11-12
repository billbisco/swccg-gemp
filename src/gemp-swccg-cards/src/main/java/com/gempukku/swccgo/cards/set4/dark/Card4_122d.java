package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Flagship
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) text, from wiki.swccg.com [[Flagship (Errata)]] and the glossary's own
 * entry: "If starship lost". The current card (Card4_122) reads "If starship about to be lost"; GEMP already takes the
 * Force loss as the starship is about to be lost (Flagship leaves table with it), so this state plays as the current
 * card with Decipher's wording. No dated source for the later rewording is on the wiki, so this state has no end date.
 */
public class Card4_122d extends Card4_122 {
    public Card4_122d() {
        super();
        setGameText("Use 2 Force to deploy on your Star Destroyer. Your other starships may move as a 'react' to same system or sector (for free). If starship lost, you lose X Force, where X = starship's armor. (Immune to your Alter.)");
        setTextState(new TextState("4_122", "D-E1", "Decipher Glossary Version 2.0, November 1998",
                LocalDate.of(1998, 11, 1), null));
    }
}
