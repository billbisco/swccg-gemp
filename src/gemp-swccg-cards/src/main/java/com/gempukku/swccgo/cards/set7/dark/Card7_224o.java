package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Come Here You Big Coward
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Special Edition (Decipher, November 1999) card from wiki.swccg.com [[Come Here You Big Coward]] and its
 * Decipher archive scan (SE-D-comehereyoubigcoward-decipher-archive.gif): the same game text, printed as "Come Here You
 * Big Coward!" without the uniqueness dot. The Players Committee errata (SWCCG 2023 Advanced Rulebook Appendix A,
 * p. 107: "This Effect is now unique (•) and should be read as "•Come Here You Big Coward". The exclamation point ...";
 * upper bound 30 July 2023) made it unique, which the bare card has. Bill 2026-11-12 (ai-team-skills RULINGS.md
 * "printed-text readings" 6): the printed card is non-unique. Its cancels do not stack (two copies cancel the same
 * things).
 */
public class Card7_224o extends Card7_224 {
    public Card7_224o() {
        super();
        setUniqueness(Uniqueness.UNRESTRICTED);
        setDisplayTitle("Come Here You Big Coward!");
        setTextState(new TextState("7_224", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
