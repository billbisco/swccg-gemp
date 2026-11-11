package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_103;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Device
 * Title: Tatooine Utility Belt (V)
 * Original version of 1210_116: the text of the Virtual Block 1 release sheet (2009-12-07), card #118; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "Force loss from
 * Or Be Destroyed". The PC website card list (by 2012-07-21, PC-E1, the current card) writes "...Or Be Destroyed",
 * with the dots of the Decipher card's title (swccg-guild/work#236: spelling, capitals and punctuation are versions;
 * batch C1). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_116 extends Card601_103 {
    public Card2210_116() {
        super();
        setGameText("Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2.");
        setLegacyVersion(new LegacyVersion("1210_116", "",
                "Virtual Block 1 release sheet (2009-12-07), card #118; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
