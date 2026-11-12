package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: There Is No Conflict
 * Text state: o (as printed, May 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text names "I Can Feel The Conflict"; the card is titled I Feel The Conflict, and the PC text corrects
 * the name. The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[There Is No Conflict]].
 */
public class Card11_088o extends Card11_088 {
    public Card11_088o() {
        super();
        setGameText("USED: If your Imperial of ability > 2 just won a battle, randomly take into hand one card stacked on I Can Feel The Conflict. (Immune to Sense.) LOST: If Vader in battle, subtract 2 from each of opponent's battle destiny draws.");
        setTextState(new TextState("11_88", "Printed",
                "Tatooine printing (Decipher, May 2001)",
                LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)));
    }
}
