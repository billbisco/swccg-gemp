package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E4 version of 1261_33: the PC website card list (2012-07-21 to 2013-01-11), which printed the PC-E2 words again
 * after the PC-E3 errata of 2012-05-09 (every published text is a version, Bill 2026-11-11; the list wrote the ▼ out as
 * "May deploy", notation only). Superseded by the Virtual Cards Master List (2013-04-27, PC-E5, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_4033 extends Card1261_033 {
    public Card2261_4033() {
        setGameText("Deploy on Rancor Pit. May ▼ a non-[Selective] ferocious creature here. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E4",
                "PC website card list (2012-07-21 to 2013-01-11)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
