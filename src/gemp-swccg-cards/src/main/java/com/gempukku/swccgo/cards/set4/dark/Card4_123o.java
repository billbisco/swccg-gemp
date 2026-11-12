package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: He Is Not Ready
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Dagobah (Decipher, April 1997) card from wiki.swccg.com [[He Is Not Ready]] and its Decipher archive scan
 * (Dagobah-D-heisnotready-decipher-archive.gif): the same game text, printed without the uniqueness dot. The Players
 * Committee errata (SWCCG 2023 Advanced Rulebook Appendix A, p. 114: "This Effect is now unique (•)"; upper bound
 * 30 July 2023) made it unique, which the bare card has. Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text
 * readings" 6): the printed card is non-unique; the training destiny modifier of two copies on one character does not
 * stack (cumulative rule, ModifiersLogic).
 */
public class Card4_123o extends Card4_123 {
    public Card4_123o() {
        super();
        setUniqueness(Uniqueness.UNRESTRICTED);
        setTextState(new TextState("4_123", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
