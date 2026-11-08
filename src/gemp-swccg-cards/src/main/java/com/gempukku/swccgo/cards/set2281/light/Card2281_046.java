package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_046;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Han's Blaster, So Uncivilized
 * Original version of 1281_46 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-07-21, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_046 extends Card1281_046 {
    public Card2281_046() {
        super();
        setGameText("Deploy on Han. I've Got A Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_46", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
