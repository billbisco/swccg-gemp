package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Order 66
 * Original version of 1250_20: the text of the Virtual Block 5 release sheet (2009-12-07), card #20; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1020) changes it: “Expand The Empire” is
 * written “Expand the Empire”, with a small t. The Master List of 2013-04-27 prints the original words again; it is
 * listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_020 extends Card1250_020 {
    public Card2250_020() {
        super();
        setGameText("Deploy on Sidious. If with [Virtual Block 5] Expand The Empire during your control phase: relocate this card to an [Episode I] Jedi at a battleground or Senate (otherwise, retrieve 1 Force). If on a Jedi, add one battle destiny here, and if that Jedi just lost, relocate this card to Sidious.");
        setLegacyVersion(new LegacyVersion("1250_20", "",
                "Virtual Block 5 release sheet (2009-12-07), card #20; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
