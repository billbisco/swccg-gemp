package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Keder The Black (V)
 * Original version of 1250_9: the Virtual Block 5 release sheet text (Dec 2009, slip 07; the Master List of 2013-04-27
 * reprints it). The PC website card list (by 2012-07-21) has the current wording (the closing quotation mark after
 * 'senators' dropped, "Squabbling" spelled "Squabling"). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_009 extends Card1250_009 {
    public Card2250_009() {
        super();
        setGameText("While at Senate: Sidious is politics +3; 'senators' on Squabbling Delegates is treated as 'Coruscant Guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile.");
        setLegacyVersion(new LegacyVersion("1250_9", "",
                "Virtual Block 5 release sheet (2009-12-07), card #07; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
