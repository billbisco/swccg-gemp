package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Expand The Empire (V)
 *
 * PC-E2 version of 1250_14: the PC card image (expandtheempire.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2014 extends Card1250_014 {
    public Card2250_2014() {
        super();
        setGameText("Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your Maul, aliens (except Mara), [Independent Starship] starships, and senators are lost. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_14", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
