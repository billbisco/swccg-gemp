package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: I Will Find Them Quickly, Master
 * Text state: o (as printed, May 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The PC text reads "no Jedi there" (where Maul and Amidala are), which is what the printed "here" means.
 * The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[I Will Find Them Quickly, Master]].
 */
public class Card11_071o extends Card11_071 {
    public Card11_071o() {
        super();
        setGameText("Deploy on table. Opponent may deploy Amidala from Reserve Deck; reshuffle. If Maul present with Amidala and opponent has no Jedi here, she is captured. Once per game may take If The Trace Was Correct into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLore("Utilizing his \"dark eye\" probe droids and sensory equipment, Maul knew he would eventually find his target.");
        setTextState(new TextState("11_71", "Printed",
                "Tatooine printing (Decipher, May 2001)",
                LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)));
    }
}
