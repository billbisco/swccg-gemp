package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_125;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Hopping Mad
 *
 * PC-E1 version of 1220_125: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1125 extends Card1220_125 {
    public Card2220_1125() {
        super();
        setGameText("Deploy on non-[Episode I] C-3PO. Once per turn, you may take How Did We Get Into This Mess?, Thank The Maker, We're Doomed, or Mantellian Savrip into hand from Reserve Deck; reshuffle. If C-3PO present at a battleground, Savrip is immune to Alter. Thank The Maker is a Used Interrupt and treat '5' in its game text as '3'.");
        setLegacyVersion(new LegacyVersion("1220_125", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
