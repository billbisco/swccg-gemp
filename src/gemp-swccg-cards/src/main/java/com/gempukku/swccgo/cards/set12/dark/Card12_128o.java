package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Allegations Of Corruption
 * Text state: o (as printed, August 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The printed card has no Grabber icon; the Players Committee added it (the game text is unchanged), so this
 * state is the current card without the Grabber icon. The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * Text from wiki.swccg.com [[Allegations Of Corruption]].
 */
public class Card12_128o extends Card12_128 {
    public Card12_128o() {
        super();
        removeIcon(Icon.GRABBER);
        setTextState(new TextState("12_128", "Printed",
                "Coruscant printing (Decipher, August 2001)",
                LocalDate.of(2001, 8, 1), LocalDate.of(2017, 6, 1)));
    }
}
