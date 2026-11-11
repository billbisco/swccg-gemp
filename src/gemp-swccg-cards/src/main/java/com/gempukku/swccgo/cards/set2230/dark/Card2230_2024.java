package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_024;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Bantha Herd
 *
 * PC-E2 version of 1230_24: the PC card image (banthaherd.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2024 extends Card1230_024 {
    public Card2230_2024() {
        super();
        setGameText("Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. If a battle was just initiated against your lone Tusken Raider, may 'react' by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_24", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
