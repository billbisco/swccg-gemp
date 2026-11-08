package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E1 version of 1261_33: the 2010 errata text (Rancor Pit; "May [download] a non-[Selective] ferocious creature
 * here"), which the Virtual Cards Master List reprints. The PC card list (PC-E2, now the current card) moves the clause
 * and says "May deploy ... here" (no Reserve Deck). Download tests moved here from Card_1261_033_Tests.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1033 extends Card1261_033 {
    public Card2261_1033() {
        setGameText("Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E1",
                "PC errata (2010-07-26; Virtual Cards Master List 2013-04-27)", LocalDate.of(2010, 7, 26), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean downloadsCreature() {
        return true;
    }
}
