package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E1 version of 1261_33: the Rancor Pit text announced in the PC news post "Pre-Worlds 2010 errata changes"
 * (2010-07-23, effective 2010-07-26). The Virtual Cards Master List (2013-04-27) and the PC card image print the same
 * words again (PC-E5, the current card). Superseded by the forum errata of 2010-07-27 (PC-E2, 2261_2033).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1033 extends Card1261_033 {
    public Card2261_1033() {
        setGameText("Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E1",
                "PC news post \"Pre-Worlds 2010 errata changes\" (2010-07-23, effective 2010-07-26)", LocalDate.of(2010, 7, 26), LocalDate.of(2010, 7, 27)));
    }
}
