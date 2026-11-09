package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6
 * Type: Effect
 * Title: Breached Defenses & Molator
 * Original version of 1261_29: the Virtual Block 6.1 release sheet text (Sep 2009, slip 31; the Master List of 2013-04-27
 * reprints it): "(except Expand The Empire)" and "lost at end of battle". The PC website card list (by 2012-07-21) has
 * the current wording ("Expand the Empire", "lost at the end of battle"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_029 extends Card1261_029 {
    public Card2261_029() {
        super();
        setGameText("Deploy on table. Once per game, unless [Virtual] Crossfire on table (or out of play), may use 3 Force to [upload] an Effect that deploys on a location (except Expand The Empire). If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Your non-Dark Jedi characters without [Permanent Weapon] or [Maintenance] are immune to Goo Nee Tay. May not be canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_29", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #31; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
