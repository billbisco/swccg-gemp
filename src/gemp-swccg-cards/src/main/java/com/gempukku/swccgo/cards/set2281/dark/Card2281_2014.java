package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_194;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: You'll Be Dead!
 * PC-E2 version of 1281_14. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2281_14) = the Virtual Block 8.1 release sheet (2012-05-11), card #03; PC-E1
 * (2281_1014) = the PC website card list (by 2012-07-21): “a Lost Interrupt” is written “a Lost interrupt”, with a
 * small i. PC-E2 (2281_2014) = the Master List (2013-04-27), the original words again; PC-E3 = the PC card image
 * (uploaded 2013-05-07; it may have been made earlier), the original words again, the current card. Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2014 extends Card601_194 {
    public Card2281_2014() {
        super();
        setGameText("Deploy on table. Non-lightsaber weapons carried by your non-Dark Jedi characters may not be stolen. During your control phase, opponent loses 1 Force for each battleground site you control with a non-[Permanent Weapon] blaster present. Imperial Artillery is a Lost Interrupt. Immune to Alter.");
        setLegacyVersion(new LegacyVersion("1281_14", "PC-E2",
                "Master List (2013-04-27; the original words again); superseded by the PC card image (uploaded 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
