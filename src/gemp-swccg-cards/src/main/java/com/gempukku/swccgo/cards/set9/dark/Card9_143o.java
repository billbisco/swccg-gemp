package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Death Star II: Capacitors
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Death Star II: Capacitors]] (Decipher archive scan); the current text is
 * [[Death Star II: Capacitors (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_143o extends Card9_143 {
    public Card9_143o() {
        super();
        setLocationDarkSideGameText("Deploys only if Coolant Shaft on table. While That Thing's Operational on table, you may add one battle destiny during battles at Death Star II system and system it orbits.");
        setLocationLightSideGameText("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 2, starship is lost.");
        setTextState(new TextState("9_143", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
