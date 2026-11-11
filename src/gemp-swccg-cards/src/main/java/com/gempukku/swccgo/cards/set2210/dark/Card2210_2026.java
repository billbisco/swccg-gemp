package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Ur'Ru'r
 *
 * PC-E2 version of 1210_26: the PC card image (urrur.gif), dated by its server upload (2013-02-25, a bulk upload;
 * the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current card)
 * follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8). Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2026 extends Card1210_026 {
    public Card2210_2026() {
        super();
        setGameText("Deploys only on Tatooine. During your deploy phase, if with URoRRuR'R'R, may deploy a weapon with 'Tusken' in lore here from Reserve Deck; reshuffle; your weapon destiny draws for such weapons are +2. During your deploy phase, Ur'Ru'r may make a regular move.");
        setLegacyVersion(new LegacyVersion("1210_26", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
