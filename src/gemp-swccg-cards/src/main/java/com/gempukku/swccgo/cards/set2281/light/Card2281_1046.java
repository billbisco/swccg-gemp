package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Han's Blaster, So Uncivilized
 * PC-E1 version of 1281_46: the PC website card list text from 2012-07-21 (Virtual Block 8 list until it changed by
 * 2012-12-12; Virtual master list to 2013-01-11): "Got a Bad Feeling", "cancelled", "Target hit, forfeit = 0". The
 * Virtual Block 8 list from 2012-12-12 spells "canceled" (PC-E2, the current card); the two lists overlap
 * (swccg-guild/work#236). Same play as the original. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1046 extends Card2281_046 {
    public Card2281_1046() {
        super();
        setGameText("Deploy on Han. I've Got a Bad Feeling About This is cancelled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_46", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2012, 12, 12)));
    }
}
