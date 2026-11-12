package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Tatooine
 * Type: Epic Event
 * Title: I Did It!
 * Text state: o (as printed, May 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The PC text says "reveal" the bottom card instead of "take" it; GEMP moves the card the same way for both.
 * The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[I Did It!]].
 */
public class Card11_025o extends Card11_025 {
    public Card11_025o() {
        super();
        setGameText("Deploy on table if you've won a Podrace. Once per game, may place Boonta Eve Podrace out of play to retrieve 4 Force. If you occupy a battleground site and a battleground system, once during each of your control phases may take the bottom card of your Lost Pile and place it on top of your Force Pile. At the end of opponent's turn opponent must lose 2 Force or place their Force Pile onto their Used Pile.");
        setTextState(new TextState("11_25", "Printed",
                "Tatooine printing (Decipher, May 2001)",
                LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)));
    }
}
