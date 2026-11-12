package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Theed Palace
 * Title: DFS Squadron Starfighter
 * Text state: o (as printed, December 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[DFS Squadron Starfighter]] (Decipher archive scan); the current text is [[DFS Squadron Starfighter (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The printed stat box says ARMOR 2, a misprint for MANEUVER 2 (Appendix A); like every text state this one keeps
 * the bare card's stats. The PC errata's "during your deploy phase ... one" plays as GEMP already did, so this state
 * reuses the current card's game text code.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card14_115o extends Card14_115 {
    public Card14_115o() {
        super();
        setGameText("Deploys -1 to Naboo or same system as your battleship. While at same system as your battleship, may deploy Droid Starfighter Laser Cannons here from Reserve Deck; reshuffle.");
        setTextState(new TextState("14_115", "Printed",
                "Theed Palace printing (Decipher, December 2001)",
                LocalDate.of(2001, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
