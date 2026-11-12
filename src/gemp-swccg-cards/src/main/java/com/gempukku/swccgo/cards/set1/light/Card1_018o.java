package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: LIN-V8K (Elleyein-Veeatekay)
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[LIN-V8K (Elleyein-Veeatekay)]]; reworded by [[LIN-V8K (Elleyein-
 * Veeatekay) (PC Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata
 * date is not known)).
 * "Lays mines" names no game action (Premiere has no mines), so only the defuse ability plays.
 * So this state reuses the current card's (Card1_018) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_018o extends Card1_018 {
    public Card1_018o() {
        super();
        setGameText("Lays mines. OR At any time during your turn, may use 1 Force to 'defuse' (lose) any one mine at same site.");
        setTextState(new TextState("1_18", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
