package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Away Put Your Weapon
 * PC-E1 version of 1220_139 (Batch S1, Bill 2026-11-11, H1): the PC card image wording (slip 140E; the same image is in the
 * swccgvkit-legacy svn r1 of 2009-12-09), dated as the 9 September 2009 errata the PC card list stamps on the card:
 * "retrieve up to 5 weapons (may take one retrieved card into hand)". The September 2009 first printing is lost.
 * Followed by the release sheet text (PC-E2, 2009-12-07, 2220_139). Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1139 extends Card2220_139 {
    public Card2220_1139() {
        setGameText("USED: An opponent's just drawn [Permanent Weapon] weapon destiny is -3. (Immune to Sense.) LOST: Once per game, retrieve up to 5 weapons (may take one retrieved card into hand).");
        setLegacyVersion(new LegacyVersion("1220_139", "PC-E1",
                "PC card image (slip 140E; in swccgvkit-legacy svn r1, 2009-12-09), the 2009-09-09 errata", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
