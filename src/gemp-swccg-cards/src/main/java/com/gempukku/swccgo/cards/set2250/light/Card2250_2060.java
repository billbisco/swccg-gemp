package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_060;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: An Unusual Amount Of Fear (V)
 *
 * PC-E2 version of 1250_60: the PC card image (anunusualamountoffear.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2060 extends Card1250_060 {
    public Card2250_2060() {
        super();
        setGameText("Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_60", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
