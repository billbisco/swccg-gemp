package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_103;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Device
 * Title: Tatooine Utility Belt (V)
 *
 * PC-E1 version of 1210_116: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1116 extends Card601_103 {
    public Card2210_1116() {
        super();
        setGameText("Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from ...Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2.");
        setLegacyVersion(new LegacyVersion("1210_116", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
