package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_191;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Aratech Corporation (V)
 * PC-E1 version of 1230_23 (Batch S1, Bill 2026-11-11, H1): the PC card image wording (slip 24E; the same image is in the
 * swccgvkit-legacy svn r1 of 2009-12-09), dated as the 9 September 2009 errata the PC card list stamps on the card:
 * "you may reveal" (the release sheet, PC-E2 and the current card, says "may reveal"). Same play. The September 2009
 * first printing is lost. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_023 extends Card601_191 {
    public Card2230_023() {
        setGameText("Deploy on table. AT-STs and speeder bikes are power and forfeit +1. During your deploy phase, you may reveal an AT-ST or Speeder Bike from hand to [upload] a unique (•) [Endor] Imperial pilot of ability < 3 (or vice versa) and deploy both simultaneously. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_23", "PC-E1",
                "PC card image (slip 24E; in swccgvkit-legacy svn r1, 2009-12-09), the 2009-09-09 errata", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
