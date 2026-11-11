package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Title: Bane Malar, Spice Addict
 * Original version of 1281_2. Order (Bill 2026-11-11: every publication is a version): Original (2281_2) = the
 * Virtual Block 8.1 release sheet (2012-05-11), card #02, “Until … Peek … Force Pile”; PC-E1 (2281_1002) = the PC
 * card image, “until … Peek … Force Pile” (made after the sheet; its wording sits between the sheet and the card
 * list, so it is placed before 2012-07-21; server copy dated 2013-05-07); PC-E2 (2281_2002) = the PC website card
 * list (by 2012-07-21), “until … peek … Force pile”; PC-E3 = the Master List (2013-04-27), the original words again,
 * the current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_002 extends Card1281_002 {
    public Card2281_002() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. Once per turn, may 'mindscan' by placing a card from hand on your Used Pile; choose: Until end of turn Malar is power +2 and may fire a weapon twice per battle. OR Peek at the top X cards of your Force Pile where X = number of characters here.");
        setLegacyVersion(new LegacyVersion("1281_2", "",
                "Virtual Block 8.1 release sheet (2012-05-11), card #02; superseded by the PC card image and the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
