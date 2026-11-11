package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_149;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: No Bargain (V)
 *
 * PC-E2 version of 1240_34: the PC card image (nobargain.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2034 extends Card601_149 {
    public Card2240_2034() {
        super();
        setGameText("Deploy on your [Reflections II] objective; 'Xizor' on it (and on Guri) is treated as 'Shada.' Non-[Permanent Weapon] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent Starship] starships. Mistryl (except Kitik) are power +1 (+2 if unique (•)) and, while Scum And Villainy on table, deploy +1. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_34", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
