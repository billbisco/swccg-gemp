package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_125;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Hopping Mad
 *
 * PC-E2 version of 1220_125: the PC card image (hoppingmad.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2125 extends Card1220_125 {
    public Card2220_2125() {
        super();
        setGameText("Deploy on non-[Episode I] C-3PO. Once per turn, you may take How Did We Get Into This Mess?, Mantellian Savrip, Thank The Maker, or We're Doomed into hand from Reserve Deck; reshuffle. If C-3PO present at a battleground, Savrip is immune to Alter. Thank The Maker is a Used Interrupt and treat '5' in its game text as '3'.");
        setLegacyVersion(new LegacyVersion("1220_125", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
