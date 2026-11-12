package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Type: Objective
 * Title: Watch Your Step / This Place Can Be A Little Rough
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed 0 side from wiki.swccg.com [[Watch Your Step / This Place Can Be A Little Rough]] (Decipher archive scan).
 * The 0 side was not changed by errata; this state carries the printed wording ("Docking Bay 94, and Tatooine system",
 * "Kessel system") and plays as the current card (Card10_026). The errata is on the 7 side: see Card10_026o_BACK.
 */
public class Card10_026o extends Card10_026 {
    public Card10_026o() {
        super();
        setGameText("Deploy Cantina, Docking Bay 94, and Tatooine system. For remainder of game, opponent activates no Force at your Cantina. Your cards with ability are deploy +6 except Luke, smugglers, freighters, and [Independent] starships. Opponent's game text on Kessel system is canceled. While this side up, once during each of your deploy phases, may take Corellia or Kessel into hand from Reserve Deck; reshuffle. Flip this card if you occupy two battlegrounds with smugglers or have completed two Kessel Runs.");
        setTextState(new TextState("10_26", "Printed", "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2023, 7, 30)));
    }
}
