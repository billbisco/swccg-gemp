package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Premium (Jedi Pack)
 * Type: Effect
 * Title: For Luck
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Jedi Pack (Decipher, April 1996) text from wiki.swccg.com [[For Luck]] and its Decipher archive scan
 * (Jedi-L-forluck-decipher-archive.gif). The Decipher Glossary Supplement (29 January 2002) rewrote it to "exclude X Dark
 * Jedi" ([[For Luck (Errata)]]), which the bare card plays.
 * <p>
 * Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 1): printed "Jedi" includes Dark Jedi ("A Dark
 * Jedi is a Jedi"; nothing in the Premiere rules distinguished them), and the card works on either player's
 * characters, your own included. So the printed card may exclude any Jedi or Dark Jedi on table.
 */
public class Card102_001o extends Card102_001 {
    public Card102_001o() {
        super();
        setGameText("Deploy on your side of table. When Counter Assault is played, may use 1 Force to add one destiny to your total. Also, when Sense or Alter is played, may use X Force to exclude X Jedi from being the \"highest-ability character\".");
        setTextState(new TextState("102_1", "Printed",
                "Jedi Pack printing (Decipher, April 1996)",
                LocalDate.of(1996, 4, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected Filter getExcludableFromHighestAbilityFilter() {
        return Filters.or(Filters.Jedi, Filters.Dark_Jedi);
    }
}
