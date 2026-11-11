package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_149;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: No Bargain (V)
 * Original version of 1240_34: the text of the Virtual Block 4 release sheet (2009-12-07), card #32E; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "objective",
 * "'Shada.'" and "(•)) and, while". The PC website card list (by 2012-07-21, PC-E1, the current card) writes
 * "Objective", "'Shada'." and "(•)), and, while" (swccg-guild/work#236: spelling, capitals and punctuation are
 * versions; batch C1). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_034 extends Card601_149 {
    public Card2240_034() {
        super();
        setGameText("Deploy on your [Reflections II] objective; 'Xizor' on it (and on Guri) is treated as 'Shada.' Non-[Permanent Weapon] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent Starship] starships. Mistryl (except Kitik) are power +1 (+2 if unique (•)) and, while Scum And Villainy on table, deploy +1. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_34", "",
                "Virtual Block 4 release sheet (2009-12-07), card #32E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
