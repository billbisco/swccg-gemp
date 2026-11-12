package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: DFS Squadron Starfighter
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Theed Palace (December 2001) card, from wiki.swccg.com [[DFS Squadron Starfighter]] (Decipher archive scan):
 * armor 2 and no maneuver, and "may deploy Droid Starfighter Laser Cannons here from Reserve Deck" with no limit of one.
 * The Players Committee's errata ([[DFS Squadron Starfighter (PC Errata)]], SWCCG 2023 Advanced Rulebook Appendix A)
 * gave it maneuver 2 instead of armor and limited the download to one during your deploy phase. GEMP's current card
 * (Card14_115) has the errata statistics; its download already has no limit of one, and plays the same here.
 */
public class Card14_115o extends Card14_115 {
    public Card14_115o() {
        super();
        setArmorAndManeuver(2f, null);
        setGameText("Deploys -1 to Naboo or same system as your battleship. While at same system as your battleship, may deploy Droid Starfighter Laser Cannons here from Reserve Deck; reshuffle.");
        setTextState(new TextState("14_115", "Printed", "Theed Palace printing (Decipher, December 2001)",
                LocalDate.of(2001, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
