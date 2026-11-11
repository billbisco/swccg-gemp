package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Title: Bane Malar, Spice Addict
 * PC-E1 version of 1281_2. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): Original (2281_2) = the Virtual Block 8.1 release sheet (2012-05-11), card #02, “Until … Peek
 * … Force Pile”; PC-E1 (2281_1002) = the PC website card list (by 2012-07-21), “until … peek … Force pile”; PC-E2
 * (2281_2002) = the Master List (2013-04-27), the original words again; PC-E3 = the PC card image (uploaded
 * 2013-05-07; it may have been made earlier), “until … Peek … Force Pile”, the current card. Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2281_1002 extends Card1281_002 {
    public Card2281_1002() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. Once per turn, may 'mindscan' by placing a card from hand on your Used Pile; choose: until end of turn Malar is power +2 and may fire a weapon twice per battle. OR peek at the top X cards of your Force pile where X = number of characters here.");
        setLegacyVersion(new LegacyVersion("1281_2", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
