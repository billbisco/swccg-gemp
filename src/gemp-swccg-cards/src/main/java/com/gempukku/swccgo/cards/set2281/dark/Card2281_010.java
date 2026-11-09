package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Death Mark & Hutt Bounty
 * Original version of 1281_10: the Virtual Block 8.1 release sheet text (2012-05-11, slip 05; the Master List of 2013-04-27 reprints it).
 * The PC website card list (by 2012-07-21) has the current wording ("If on the Hunt on Table", "the 'mark'" twice). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_010 extends Card1281_010 {
    public Card2281_010() {
        super();
        setGameText("If On The Hunt on table, deploy on a 'mark' (opponent's non-droid character at a battleground site). Target an assassin at another site. During any move phase, when reached by target, draw destiny and add target's power; opponent draws two destiny (choosing one) and adds 'mark's defense value. If your total is higher, 'mark' is 'assassinated:' opponent loses 2 Force (4 if a Jedi), 'mark' is lost, and relocate Death Mark to your side of table. Otherwise, place Death Mark (and may place assassin it targets) in Used Pile. May not be canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_10", "",
                "Virtual Block 8.1 release sheet (2012-05-11), card #05; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
