package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Kyle Katarn's Blaster Rifle
 * PC-E1 version of 1281_47: the PC website card list text (2012-07-21 .. 2013-01-11), still titled Rebel Agent's
 * Blaster Rifle, which dropped "its" and both "total"s ("Character or Creature hit, and forfeit = 0, if destiny >
 * defense value. Vehicle hit, if destiny -2 > defense value."). Same play as the original (both destiny draws are
 * totalled either way). The Master List (2013-04-27, PC-E2, the current card) retitled the card Kyle Katarn's Blaster
 * Rifle. Restored in batch B21 (this id had been retired and aliased to 1281_47).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1047 extends Card2281_047 {
    public Card2281_1047() {
        setGameText("Deploy on Rebel Agent. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or Creature hit, and forfeit = 0, if destiny > defense value. Vehicle hit, if destiny -2 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_47", "PC-E1",
                "PC website card list (by 2012-07-21), printed as Rebel Agent's Blaster Rifle", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
