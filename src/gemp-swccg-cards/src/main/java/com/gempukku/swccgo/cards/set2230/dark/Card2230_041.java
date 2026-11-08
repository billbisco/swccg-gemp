package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_041;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Dead Ewok (V)
 * Original version of 1230_41: the Virtual Block 3 release sheet text (Dec 2009), "Cancel Ellorrs Madak." (the card list misspells "Ellors").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_041 extends Card1230_041 {
    public Card2230_041() {
        super();
        setGameText("Cancel Ellorrs Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1230_41", "",
                "Virtual Block 3 release sheet (2009-12-07), card #42", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
