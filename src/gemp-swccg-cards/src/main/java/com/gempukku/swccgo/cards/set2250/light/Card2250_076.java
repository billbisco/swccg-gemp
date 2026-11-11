package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Location
 * Title: Kashyyyk (V)
 * PC-E1 version of 1250_76 (Batch S1, Bill 2026-11-11, H1): the PC card image wording (slip 75E; the same image is in the
 * swccgvkit-legacy svn r1 of 2009-12-09), dated as the 9 September 2009 errata: "if this was your starting location"
 * (the release sheet, PC-E2 and the current card, says "if Kashyyyk was"). Same play. The September 2009 first printing
 * is lost. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_076 extends Card1250_076 {
    public Card2250_076() {
        setLocationLightSideGameText("Once per turn, if this was your starting location, may deploy a Kashyyyk site from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_76", "PC-E1",
                "PC card image (slip 75E; in swccgvkit-legacy svn r1, 2009-12-09), the 2009-09-09 errata", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
