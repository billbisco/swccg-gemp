package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Commando Training & K'lor'slug
 * Original version of 1261_79: the Virtual Block 6.1 release sheet text, "ignore location deployment restrictions (and
 * "power -1")". The PC card list (PC-E1, the current card, 2012-07-21 to 2013-01-11) spells it "deploytment". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_079 extends Card1261_079 {
    public Card2261_079() {
        setGameText("Deploy on table. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Rebels (except Jedi or [Permanent Weapon] characters) ignore location deployment restrictions (and \"power -1\") in their game text, apply their Endor deployment modifiers at any location, and are immune to Bad Feeling Have I. Your Interrupts that retrieve exactly 1 Force (except Corellian Retort) are not stacked on Do They Have A Code Clearance?. Cannot be canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_79", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #78", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
