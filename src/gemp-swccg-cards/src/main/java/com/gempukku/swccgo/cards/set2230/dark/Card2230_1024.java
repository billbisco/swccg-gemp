package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_024;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Bantha Herd
 *
 * PC-E1 version of 1230_24: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1024 extends Card1230_024 {
    public Card2230_1024() {
        super();
        setGameText("Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. You may 'react' to battles initiated against your lone Tusken Raider by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_24", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
