package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Type: Objective
 * Title: Watch Your Step / This Place Can Be A Little Rough
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The 7 side of the printed state 10_26o (Card10_026o): flipping 10_26o loads 10_26o_BACK. Printed text from
 * wiki.swccg.com [[Watch Your Step / This Place Can Be A Little Rough]] (Decipher archive scan): "may play one
 * interrupt from Lost Pile (then place that card out of play)". The Players Committee's errata (SWCCG 2023 Advanced
 * Rulebook Appendix A, PDF dated 30 July 2023) added "as if from hand", which is the current back (Card10_026_BACK).
 * GEMP already plays an Interrupt from Lost Pile as if from hand, so this state plays as the current back.
 */
public class Card10_026o_BACK extends Card10_026_BACK {
    public Card10_026o_BACK() {
        super();
        setGameText("While this side up, your Force generation is +1 at each system you control with a smuggler. Opponent's Force generation at non-battlegrounds is limited to 1. When you have two or more smugglers in a battle, add one battle destiny. Each of your smugglers is forfeit +2. Once during each turn, may play one interrupt from Lost Pile (then place that card out of play). Sense, Alter, and Keep Your Eyes Open may not be played. Flip this card if you do not occupy two battlegrounds (unless you have completed two Kessel Runs).");
        setTextState(new TextState("10_26_BACK", "Printed", "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2023, 7, 30)));
    }
}
