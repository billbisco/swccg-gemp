package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Twilight Is Upon Me
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Twilight Is Upon Me]] (Decipher archive scan); the current text is
 * [[Twilight Is Upon Me (PC Errata)]]. The current card plays the PC errata (only the [Death Star II] Anakin Skywalker); this state takes any Anakin Skywalker, as printed. The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_046o extends Card9_046 {
    public Card9_046o() {
        super();
        setGameText("Deploy on table. Once per turn, may take one The Force Is Strong With This One or Anakin Skywalker into hand from Reserve Deck; reshuffle. May use 2 Force to cancel You Are Beaten (unless canceling Uncontrollable Fury) or Dark Jedi Presence. (Immune to Alter.)");
        setTextState(new TextState("9_46", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: any Anakin Skywalker. */
    @Override
    protected Filter anakinSkywalkerFilter() {
        return Filters.Anakin_Skywalker;
    }
}
