package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Breached Defenses & •Molator
 *
 * PC-E2 version of 1261_29: the PC card image (breacheddefenses&molator.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2029 extends Card1261_029 {
    public Card2261_2029() {
        super();
        setGameText("Deploy on table. Once per game, unless [Virtual] Crossfire on table (or out of play), may use 3 Force to [upload] an Effect that deploys on a location (except Expand The Empire). If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Your non-Dark Jedi characters without [Permanent Weapon] or [Maintenance] are immune to Goo Nee Tay. May not be canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_29", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
