package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Type: Objective
 * Title: Rebel Strike Team / Garrison Destroyed
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed 0 side from wiki.swccg.com [[Rebel Strike Team / Garrison Destroyed]] (Decipher archive scan): "take one
 * Bunker or Deactivate The Shield Generator into hand; reshuffle". Decipher's Glossary Supplement (29 January 2002)
 * clarified it to "into hand from Reserve Deck; reshuffle" ([[Rebel Strike Team / Garrison Destroyed (Errata)]]),
 * which is the current card (Card8_078). "Reshuffle" already means the Reserve Deck, so the Supplement wording is a
 * clarification and this state reuses the current card's game text code. The 7 side is Card8_078o_BACK.
 */
public class Card8_078o extends Card8_078 {
    public Card8_078o() {
        super();
        setGameText("Deploy Endor system and Rebel Landing Site. While this side up, opponent loses no more than 1 Force from each of your Force drains at Endor locations. Once during each of your deploy phases, may use 2 Force to take one Bunker or Deactivate The Shield Generator into hand; reshuffle. Flip this card if Bunker 'blown away' or if, during your move phase, you control three exterior Endor sites (with two Rebel scouts at each). Place out of play if Endor 'blown away.'");
        setTextState(new TextState("8_78", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2002, 1, 29)));
    }
}
