package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Objective
 * Title: Systems Will Slip Through Your Fingers
 * Text state: d (back of Card7_136d; Decipher Glossary Supplement, 29 January 2002)
 * <p>
 * Same play as the bare back side (Card7_136_BACK); the wording differs only in quotes and punctuation.
 */
public class Card7_136d_BACK extends Card7_136_BACK {
    public Card7_136d_BACK() {
        super();
        setGameText("While this side up, to draw a card from Force Pile, opponent must first use 1 Force. For each battleground system you control, you may cancel one opponent's Force drain (limit twice per turn). You may not deploy any systems. At each system opponent occupies during any deploy phase, opponent may 'probe' there by placing one card from hand face down beneath that system. Place out of play if \"Hidden Base\" system is 'probed;' Dark Side places 'probe' cards in Used Pile (and may retrieve 1 Force for each Probe Droid used to 'probe').");
        setTextState(new TextState("7_136_BACK", "D-E1",
                "Decipher Glossary Supplement, 29 January 2002",
                LocalDate.of(2002, 1, 29), LocalDate.of(2017, 3, 1)));
    }
}
