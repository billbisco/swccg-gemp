package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_103;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Device
 * Title: Tatooine Utility Belt (V)
 *
 * PC-E2 version of 1210_116: the PC card image (tatooineutilitybelt.gif), dated by its server upload (2013-02-25,
 * a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2116 extends Card601_103 {
    public Card2210_2116() {
        super();
        setGameText("Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2.");
        setLegacyVersion(new LegacyVersion("1210_116", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
