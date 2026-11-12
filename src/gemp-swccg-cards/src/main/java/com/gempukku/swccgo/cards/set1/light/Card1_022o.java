package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: Owen Lars
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Owen Lars]]; reworded by [[Owen Lars (Errata)]] (Decipher
 * Glossary Version 2.0 (November 1998)).
 * "If lost during the opponent's turn" is the current "if lost from table during opponent's turn".
 * So this state reuses the current card's (Card1_022) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_022o extends Card1_022 {
    public Card1_022o() {
        super();
        setGameText("Deploys free at Lars' Moisture Farm. Power +2 if at same site as Beru Lars or one Vaporator. If lost during the opponent's turn, Luke is power +3 until the end of your next turn.");
        setTextState(new TextState("1_22", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
