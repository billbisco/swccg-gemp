package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Death Star II: Coolant Shaft
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Death Star II: Coolant Shaft]] (Decipher archive scan); the current text is
 * [[Death Star II: Coolant Shaft (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_144o extends Card9_144 {
    public Card9_144o() {
        super();
        setLocationDarkSideGameText("Deploys only if Death Star II system on table. Your TIEs at Death Star II locations may not Tallon Roll. Your non-Objective cards with 'Occupation' in title are canceled.");
        setLocationLightSideGameText("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 1, starship is lost.");
        setTextState(new TextState("9_144", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
