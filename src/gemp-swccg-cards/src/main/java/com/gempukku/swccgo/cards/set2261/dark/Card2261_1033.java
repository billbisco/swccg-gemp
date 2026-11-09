package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E1 version of 1261_33: the Rancor Pit text of 2010-07-26 (PC news post), also on the PC card image and reprinted
 * by the Virtual Cards Master List (2013-04-27, a stale reprint). The PC website card list (by 2012-07-21) moved the
 * creature sentence to the front (PC-E2, the current card). Same play as the current card. Restored as a version by
 * legacy batch B16 (it was aliased to 1261_33 as an icon-loss version; the card-list text also reorders the text).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1033 extends Card1261_033 {
    public Card2261_1033() {
        setGameText("Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E1",
                "PC news post (2010-07-26); PC card image; Virtual Cards Master List reprint (2013-04-27)", LocalDate.of(2010, 7, 26), LocalDate.of(2012, 7, 21)));
    }
}
