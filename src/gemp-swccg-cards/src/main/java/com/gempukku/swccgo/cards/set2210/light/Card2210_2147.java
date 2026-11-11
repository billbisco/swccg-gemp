package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_147;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Beru Stew
 *
 * PC-E2 version of 1210_147: the PC card image (berustew.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2147 extends Card1210_147 {
    public Card2210_2147() {
        super();
        setGameText("USED: Activate 1 Force (2 if a Lars on table). OR During battle at a farm, add the destiny number of your device on this farm to your total power. LOST: During battle at a farm, after battle destiny is drawn, place a 'crop' out of play to make attrition against you = 0.");
        setLegacyVersion(new LegacyVersion("1210_147", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
