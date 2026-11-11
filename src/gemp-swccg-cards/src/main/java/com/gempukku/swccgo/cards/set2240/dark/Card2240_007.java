package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Janus Greejatus
 * Original version of 1240_7. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2240_7) = the Virtual Block 4 release sheet (2009-12-07), card #08; PC-E1
 * (2240_1007) = the PC website card list (by 2012-07-21): “your total influence here” is written “your total
 * Influence here”, with a capital I. PC-E2 (2240_2007) = the PC card image (uploaded 2013-02-25; it may have been
 * made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the original words again, the
 * current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_007 extends Card1240_007 {
    public Card2240_007() {
        super();
        setGameText("Agendas: logistics, rule. Once per turn, if at Conference Room and you just drew a non-Epic Event destiny < your total influence here, may use 2 Force to cancel and redraw that destiny or use 4 Force to add 2 to that destiny.");
        setLegacyVersion(new LegacyVersion("1240_7", "",
                "Virtual Block 4 release sheet (2009-12-07), card #08; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
