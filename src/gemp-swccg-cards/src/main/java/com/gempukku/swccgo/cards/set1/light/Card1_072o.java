package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Beru Stew
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (Decipher, December 1995) card from wiki.swccg.com [[Beru Stew]] and its Decipher archive scan
 * (Premiere-L-berustew-decipher-archive.gif): the same game text, printed without the uniqueness dot. The Decipher
 * Glossary Version 2.0 (November 1998) revised the title to "•Beru Stew" (unique), as does the SWCCG 2023 Advanced
 * Rulebook Appendix A (p. 104); the bare card is unique. Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text
 * readings" 6): the printed card is non-unique.
 */
public class Card1_072o extends Card1_072 {
    public Card1_072o() {
        super();
        setUniqueness(Uniqueness.UNRESTRICTED);
        setTextState(new TextState("1_72", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
