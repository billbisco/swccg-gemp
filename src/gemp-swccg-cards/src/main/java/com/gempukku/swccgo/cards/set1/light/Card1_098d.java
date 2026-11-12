package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Narrow Escape
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Supplement (29 January 2002) text, from wiki.swccg.com [[Narrow Escape (Errata)]] and the
 * Supplement's own entry: the cards with ability move away and the battle is canceled. The current card (Card1_098)
 * no longer cancels the battle. No dated source for that later change is on the wiki, so this state has no end date.
 */
public class Card1_098d extends Card1_098 {
    public Card1_098d() {
        super();
        setGameText("If opponent just initiated battle at a site where you have a Rebel of ability > 2 present, move all of your cards with ability there away (using their landspeed at normal use of Force) and cancel battle.");
        setTextState(new TextState("1_98", "D-E1", "Decipher Glossary Supplement, 29 January 2002",
                LocalDate.of(2002, 1, 29), null));
    }

    @Override
    protected boolean cancelsBattle() {
        return true;
    }
}
