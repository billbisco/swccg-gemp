package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_024;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Bantha Herd (V)
 * Original version of 1230_24: the Virtual Block 3 release sheet text (Dec 2009), "If a battle was just initiated against your lone Tusken Raider, may 'react' by deploying ...".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_024 extends Card1230_024 {
    public Card2230_024() {
        super();
        setGameText("Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. If a battle was just initiated against your lone Tusken Raider, may 'react' by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_24", "",
                "Virtual Block 3 release sheet (2009-12-07), card #24", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
