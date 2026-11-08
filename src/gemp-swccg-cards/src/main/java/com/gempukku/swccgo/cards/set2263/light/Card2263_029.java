package com.gempukku.swccgo.cards.set2263.light;

import com.gempukku.swccgo.cards.set1263.light.Card1263_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Interrupt
 * Title: I'm Here To Rescue You (V)
 * Original version of 1263_29: the Virtual Block 6.3 release sheet text ("▼ Cell 2187 and one always-[Immune to Alter]
 * Effect"; the PC card list, PC-E1 and now the current card, spells out the download). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_029 extends Card1263_029 {
    public Card2263_029() {
        setGameText("If Rescue The Princess on table, LOST: Once per game, choose: Cancel an attempt to make non-Heroic R2-D2 lost. OR Lose 2 Force to cancel a battle just initiated if Leia defending alone. (Immune to Sense.) STARTING: ▼ Cell 2187 and one always-[Immune to Alter] Effect. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1263_29", "",
                "Virtual Block 6.3 release sheet (2010-03-15), card #3-29", LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21)));
    }
}
